// app.js — vanilla JS client for the described Spring Boot backend (SockJS + STOMP)
//
// Requirements implemented:
// - Signup / Signin (POST /auth/signup, /auth/signin)
// - Store JWT in localStorage
// - Connect to SockJS /ws?token=... and STOMP.over(socket)
// - Subscribe to /user/queue/events and /user/queue/errors
// - nextRequestId() and pendingRequests map
// - createPrivateChat -> /app/private-chat.create
// - sendMessage -> /app/private-chat.send_private_message
// - handle ChatCreatedResponse, SimpleResponse, PrivateChatCreated, MessageArrived, InternalErrorResponse
// - Basic reconnect with exponential backoff
//
// NOTE: If backend lives on another origin, set API_BASE and make sure CORS/SockJS endpoint accessible.

'use strict';

const API_BASE = ''; // empty => same origin. Put 'http://localhost:8080' if backend elsewhere.

/////////////////////
// Utilities / state
/////////////////////

const storage = window.localStorage;
const TOKEN_KEY = 'jwt_token_chat_demo';

let jwtToken = storage.getItem(TOKEN_KEY) || null;
let stompClient = null;
let sock = null;
let connected = false;
let reconnectAttempts = 0;

let requestIdCounter = 1;
function nextRequestId() { return requestIdCounter++; }
const pendingRequests = {}; // requestId -> {type, meta, timestamp}

const chats = new Map(); // chatId -> { chatId, companionId, messages: [{id?, text, ts, status}] , unread }
let activeChatId = null;

/////////////////////
// DOM refs
/////////////////////
const authScreen = document.getElementById('auth-screen');
const mainScreen = document.getElementById('main-screen');

const loginInput = document.getElementById('login');
const passwordInput = document.getElementById('password');
const signupBtn = document.getElementById('signupBtn');
const signinBtn = document.getElementById('signinBtn');
const checkAuthBtn = document.getElementById('checkAuthBtn');
const authResult = document.getElementById('authResult');

const currentUserDiv = document.getElementById('currentUser');
const logoutBtn = document.getElementById('logoutBtn');

const createUserIdInput = document.getElementById('createUserId');
const createChatBtn = document.getElementById('createChatBtn');
const createChatStatus = document.getElementById('createChatStatus');

const chatsUl = document.getElementById('chatsUl');
const chatHeader = document.getElementById('chatHeader');
const messagesDiv = document.getElementById('messages');
const sendForm = document.getElementById('sendForm');
const messageInput = document.getElementById('messageInput');
const sendBtn = document.getElementById('sendBtn');

const toastEl = document.getElementById('toast');

/////////////////////
// UI helpers
/////////////////////
function showToast(msg, timeout = 3000){
    toastEl.textContent = msg;
    toastEl.classList.remove('hidden');
    clearTimeout(toastEl._t);
    toastEl._t = setTimeout(()=> toastEl.classList.add('hidden'), timeout);
}

function setAuthUI(loggedIn){
    if(loggedIn){
        authScreen.classList.add('hidden');
        mainScreen.classList.remove('hidden');
        currentUserDiv.textContent = 'Пользователь: (получаем...)';
        // show username via /secured later
    }else{
        authScreen.classList.remove('hidden');
        mainScreen.classList.add('hidden');
    }
}

function renderChatList(){
    chatsUl.innerHTML = '';
    for(const [chatId, chat] of chats){
        const li = document.createElement('li');
        li.className = 'chat-item' + (chatId === activeChatId ? ' active' : '');
        li.dataset.chatId = chatId;

        const left = document.createElement('div');
        left.style.display='flex';
        left.style.flexDirection='column';
        left.style.gap='4px';
        const title = document.createElement('div');
        title.className = 'title';
        title.textContent = chat.title || chat.chatId;
        const meta = document.createElement('div');
        meta.className = 'meta';
        meta.textContent = `messages: ${chat.messages.length}`;

        left.appendChild(title);
        left.appendChild(meta);

        const right = document.createElement('div');
        if(chat.unread){
            const badge = document.createElement('span');
            badge.className = 'badge';
            badge.textContent = chat.unread;
            right.appendChild(badge);
        }

        li.appendChild(left);
        li.appendChild(right);

        li.addEventListener('click', ()=> selectChat(chatId));
        chatsUl.appendChild(li);
    }
}

function selectChat(chatId){
    activeChatId = chatId;
    const chat = chats.get(chatId);
    if(!chat) return;
    chat.unread = 0;
    renderChatList();
    chatHeader.textContent = `Chat: ${chatId}${chat.companionId ? ' (user ' + chat.companionId + ')' : ''}`;
    renderMessages(chat);
    sendForm.classList.remove('hidden');
    messageInput.focus();
}

function renderMessages(chat){
    messagesDiv.innerHTML = '';
    if(!chat) return;
    for(const m of chat.messages){
        const el = document.createElement('div');
        el.className = 'message' + (m.out ? ' out' : '');
        el.innerHTML = `<div>${escapeHtml(m.text)}</div>
      <div class="meta">${formatTs(m.ts)} ${m.status ? ' · ' + m.status : ''}</div>`;
        messagesDiv.appendChild(el);
    }
    // scroll to bottom
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

function escapeHtml(s){
    return (s+'').replace(/[&<>"']/g, (c) => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":"&#39;"}[c]));
}

function formatTs(epochSeconds){
    if(!epochSeconds) return '';
    const d = new Date(epochSeconds * 1000);
    return d.toLocaleString();
}

/////////////////////
// Auth: signup / signin
/////////////////////
signupBtn.addEventListener('click', async () => {
    const login = loginInput.value.trim();
    const password = passwordInput.value;
    if(!login || !password){ showToast('Заполните login и password'); return; }
    try{
        const res = await fetch(API_BASE + '/auth/signup', {
            method: 'POST',
            headers: {'Content-Type':'application/json'},
            body: JSON.stringify({login, password})
        });
        if(res.ok){
            showToast('Регистрация успешна. Теперь войдите.');
        }else{
            const txt = await res.text();
            showToast('Signup failed: ' + txt);
        }
    }catch(err){
        showToast('Ошибка signup: ' + err.message);
    }
});

signinBtn.addEventListener('click', async () => {
    const login = loginInput.value.trim();
    const password = passwordInput.value;
    if(!login || !password){ showToast('Заполните login и password'); return; }
    try{
        const res = await fetch(API_BASE + '/auth/signin', {
            method:'POST',
            headers: {'Content-Type':'application/json'},
            body: JSON.stringify({login, password})
        });
        if(res.ok){
            // token may be plain text or JSON string -> try both
            let token = await res.text();
            try{ const parsed = JSON.parse(token); if(typeof parsed === 'string') token = parsed; }catch(e){}
            jwtToken = token.replace(/^"(.*)"$/, '$1'); // strip quotes if present
            storage.setItem(TOKEN_KEY, jwtToken);
            showToast('Вход успешен');
            setAuthUI(true);
            // connect WS
            connectStomp();
            // show secured username
            fetchSecuredName();
        }else{
            const txt = await res.text();
            showToast('Signin failed: ' + txt);
        }
    }catch(err){
        showToast('Ошибка signin: ' + err.message);
    }
});

logoutBtn.addEventListener('click', () => {
    jwtToken = null;
    storage.removeItem(TOKEN_KEY);
    disconnectStomp();
    setAuthUI(false);
    showToast('Вы вышли');
});

checkAuthBtn.addEventListener('click', fetchSecuredName);

async function fetchSecuredName(){
    if(!jwtToken){ authResult.textContent = 'не авторизован'; return; }
    try{
        const res = await fetch(API_BASE + '/secured', {
            method:'GET',
            headers: {'Authorization':'Bearer ' + jwtToken}
        });
        if(res.ok){
            const name = await res.text();
            authResult.textContent = name;
            currentUserDiv.textContent = 'Пользователь: ' + name;
        }else{
            authResult.textContent = 'Ошибка: ' + res.status;
        }
    }catch(err){
        authResult.textContent = 'Ошибка: ' + err.message;
    }
}

/////////////////////
// STOMP / SockJS
/////////////////////
function connectStomp(){
    if(!jwtToken){
        console.warn('No JWT - cannot connect WS');
        return;
    }
    if(connected) return;

    const tokenParam = encodeURIComponent(jwtToken);
    const wsUrl = API_BASE + '/ws?token=' + tokenParam;

    // create SockJS object
    try{
        sock = new SockJS(wsUrl);
    }catch(err){
        showToast('SockJS init error: ' + err.message);
        scheduleReconnect();
        return;
    }

    stompClient = Stomp.over(sock);
    // optional: don't spam console
    stompClient.debug = function(){ /* noop to silence */ };

    const connectHeaders = {}; // server uses token via query param

    stompClient.connect(connectHeaders, onStompConnect, onStompError);
}

function onStompConnect(frame){
    connected = true;
    reconnectAttempts = 0;
    showToast('WebSocket connected');
    console.info('STOMP connected', frame);

    // subscribe user queues
    stompClient.subscribe('/user/queue/events', onEventMessage, {id:'events-sub'});
    stompClient.subscribe('/user/queue/errors', onErrorMessage, {id:'errors-sub'});
}

function onStompError(err){
    connected = false;
    console.error('STOMP error', err);
    showToast('WebSocket error or disconnected');
    scheduleReconnect();
}

function disconnectStomp(){
    if(stompClient && connected){
        try{
            stompClient.disconnect(() => {
                connected = false;
                stompClient = null;
                showToast('WebSocket disconnected');
            });
        }catch(e){
            console.warn('disconnect error', e);
        }
    }else if(sock){
        try{ sock.close(); }catch(e){}
    }
    connected = false;
    stompClient = null;
}

function scheduleReconnect(){
    disconnectStomp();
    reconnectAttempts++;
    const delay = Math.min(30_000, 1000 * Math.pow(2, Math.min(6, reconnectAttempts)));
    console.log(`Reconnecting in ${delay}ms...`);
    setTimeout(()=> {
        if(!jwtToken) return;
        connectStomp();
    }, delay);
}

/////////////////////
// STOMP handlers
/////////////////////
function onEventMessage(m){
    if(!m.body) return;
    let payload;
    try{ payload = JSON.parse(m.body); }catch(e){
        console.warn('Invalid JSON event', m.body);
        return;
    }
    // Distinguish by operationCode or fields presence
    const op = payload.operationCode || null;
    if(op === 'response'){
        handleResponse(payload);
    } else if(op === 'PrivateChatCreated'){
        handlePrivateChatCreated(payload);
    } else if(op === 'MessageArrived'){
        handleMessageArrived(payload);
    } else {
        console.info('Unknown event op', payload);
    }
}

function onErrorMessage(m){
    if(!m.body) return;
    let payload;
    try{ payload = JSON.parse(m.body); }catch(e){
        console.warn('Invalid JSON error', m.body);
        return;
    }
    handleInternalError(payload);
}

/////////////////////
// Handlers for types
/////////////////////
function handleResponse(payload){
    // Could be ChatCreatedResponse or SimpleResponse
    const requestId = payload.requestId;
    if(!requestId){
        console.warn('response without requestId', payload);
        return;
    }
    const pending = pendingRequests[requestId];
    if(!pending){
        console.warn('No pending request for id', requestId, payload);
        return;
    }

    if(payload.isError){
        // Simulate error handling
        const errMsg = payload.errorMessage || 'Unknown error';
        showToast('Operation error: ' + errMsg);
        if(pending.type === 'sendMessage' && pending.meta.localMessageId){
            // mark message as failed
            markLocalMessageStatus(pending.meta.chatId, pending.meta.localMessageId, 'Ошибка: ' + errMsg);
        }
    } else {
        // success
        if(pending.type === 'createChat'){
            // ChatCreatedResponse likely contains chatId
            const chatId = payload.chatId;
            if(chatId){
                addOrUpdateChat({chatId, companionId: pending.meta.userId, title: `chat ${chatId}`, messages: []});
                selectChat(chatId);
            }
        } else if(pending.type === 'sendMessage'){
            // mark message as delivered
            markLocalMessageStatus(pending.meta.chatId, pending.meta.localMessageId, 'Отправлено');
        }
    }

    // cleanup
    delete pendingRequests[requestId];
}

function handlePrivateChatCreated(payload){
    // No requestId — event notifying a chat created by someone else
    const chatId = payload.chatId;
    const creatorId = payload.creatorId;
    if(!chatId) return;
    if(!chats.has(chatId)){
        addOrUpdateChat({chatId, companionId: creatorId, title: `chat ${chatId}`, messages: []});
        showToast('Новый приватный чат создан: ' + chatId);
        renderChatList();
    }else{
        console.info('PrivateChatCreated but chat exists', chatId);
    }
}

function handleMessageArrived(payload){
    const chatId = payload.chatId;
    const text = payload.text;
    const ts = payload.timestamp || Math.floor(Date.now()/1000);
    if(!chatId || !text) return;
    if(!chats.has(chatId)){
        addOrUpdateChat({chatId, companionId: null, title: `chat ${chatId}`, messages: []});
    }
    const chat = chats.get(chatId);
    chat.messages.push({text, ts, out:false, status: null});
    if(activeChatId === chatId){
        renderMessages(chat);
    }else{
        chat.unread = (chat.unread || 0) + 1;
        renderChatList();
    }
}

function handleInternalError(payload){
    // InternalErrorResponse with requestId and errorMessage
    const requestId = payload.requestId;
    const errMsg = payload.errorMessage || 'Unknown';
    if(requestId && pendingRequests[requestId]){
        const pending = pendingRequests[requestId];
        if(pending.type === 'sendMessage' && pending.meta.localMessageId){
            markLocalMessageStatus(pending.meta.chatId, pending.meta.localMessageId, 'Ошибка отправки: ' + errMsg);
        }
        delete pendingRequests[requestId];
        showToast('Ошибка: ' + errMsg);
    }else{
        showToast('Server error: ' + errMsg);
    }
}

/////////////////////
// Actions: create chat, send message
/////////////////////
createChatBtn.addEventListener('click', () => {
    const val = createUserIdInput.value.trim();
    const userId = val ? Number(val) : null;
    if(!userId){ showToast('Введите userId'); return; }
    createChatStatus.textContent = 'Создаём...';
    createPrivateChat(userId);
});

async function createPrivateChat(userId){
    if(!connected || !stompClient) { showToast('WebSocket не подключён'); return; }
    const rid = nextRequestId();
    const payload = { userId: Number(userId), requestId: rid };
    pendingRequests[rid] = { type: 'createChat', meta: { userId }, timestamp: Date.now() };
    stompClient.send('/app/private-chat.create', {}, JSON.stringify(payload));
    // UI hint
    createChatStatus.textContent = 'Ожидание ответа...';
    // remove status after 8s if nothing
    setTimeout(()=> {
        if(pendingRequests[rid]) createChatStatus.textContent = 'Ожидание...';
    }, 8000);
}

sendForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const text = messageInput.value.trim();
    if(!text) return;
    if(!activeChatId){ showToast('Выберите чат'); return; }
    sendMessage(activeChatId, text);
    messageInput.value = '';
});

function sendMessage(chatId, text){
    if(!connected || !stompClient){ showToast('WebSocket не подключён'); return; }
    const rid = nextRequestId();
    // prepare a local message id so we can mark status later
    const localMessageId = 'local-' + Date.now() + '-' + Math.floor(Math.random()*1000);
    // add to UI immediately
    const ts = Math.floor(Date.now() / 1000);
    const chat = chats.get(chatId);
    if(!chat) return;
    chat.messages.push({ id: localMessageId, text, ts, out:true, status: 'Отправка...' });
    renderMessages(chat);

    // remember pending
    pendingRequests[rid] = { type: 'sendMessage', meta: { chatId, localMessageId, text }, timestamp: Date.now() };

    const payload = { requestId: rid, chatId: chatId, message: text };
    stompClient.send('/app/private-chat.send_private_message', {}, JSON.stringify(payload));
}

function markLocalMessageStatus(chatId, localMessageId, status){
    const chat = chats.get(chatId);
    if(!chat) return;
    const m = chat.messages.find(x => x.id === localMessageId);
    if(m){
        m.status = status;
        renderMessages(chat);
    } else {
        // sometimes server responds with no match; ignore
    }
}

/////////////////////
// Helpers: chats management
/////////////////////
function addOrUpdateChat(chatObj){
    const { chatId, companionId, title } = chatObj;
    if(!chats.has(chatId)){
        chats.set(chatId, { chatId, companionId, title: title || chatId, messages: [], unread:0 });
    } else {
        const c = chats.get(chatId);
        c.companionId = c.companionId || companionId;
        c.title = c.title || title;
    }
    renderChatList();
}

/////////////////////
// Init
/////////////////////

function tryAutoLogin(){
    if(jwtToken){
        setAuthUI(true);
        connectStomp();
        fetchSecuredName();
    }else{
        setAuthUI(false);
    }
}

// Start
tryAutoLogin();

/////////////////////
// Optional: debug helpers to populate UI with test data (disabled by default)
/////////////////////
// Uncomment to add a sample chat for dev:
// addOrUpdateChat({chatId: 'demo-chat-1', companionId: 42, title: 'Chat demo'}); selectChat('demo-chat-1');

