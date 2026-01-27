/* =========================
   HTTP MODULE
========================= */
const http = {
    async post(url, body, token) {
        const res = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                ...(token ? { "Authorization": `Bearer ${token}` } : {})
            },
            body: JSON.stringify(body)
        });

        if (!res.ok) {
            throw new Error(`HTTP ${res.status}`);
        }

        return res.text();
    }
};

let requestIdSeq = 0;

function nextRequestId() {
    return ++requestIdSeq;
}


/* =========================
   STOMP MODULE
========================= */
const ws = {
    stompClient: null,

    connect(token) {
        return new Promise((resolve, reject) => {
            const socket = new SockJS(`/ws?token=${token}`);
            this.stompClient = Stomp.over(socket);
            this.stompClient.debug = null;

            this.stompClient.connect(
                {},
                () => {
                    // подписки
                    this.subscribe();
                    resolve();
                },
                reject
            );
        });
    },

    subscribe() {
        this.stompClient.subscribe("/topic/response", msg => {
            const body = JSON.parse(msg.body);
            console.log("RESPONSE:", body.requestId, body);
        });

        this.stompClient.subscribe("/topic/error", msg => {
            const body = JSON.parse(msg.body);
            console.error("ERROR:", body.requestId, body);
        });
    },

    send(destination, payload) {
        if (!this.stompClient || !this.stompClient.connected) {
            throw new Error("WS not connected");
        }

        const requestId = nextRequestId();

        this.stompClient.send(
            destination,
            {},
            JSON.stringify({
                requestId,
                ...payload
            })
        );

        return requestId;
    }
};


/* =========================
   STATE
========================= */
let jwtToken = null;

/* =========================
   DOM ELEMENTS
========================= */
const signupLogin = document.getElementById("signup-login");
const signupPassword = document.getElementById("signup-password");
const signinLogin = document.getElementById("signin-login");
const signinPassword = document.getElementById("signin-password");


/* =========================
   UI LOGIC
========================= */
const authBlock = document.getElementById("auth");
const actionsBlock = document.getElementById("actions");
const authError = document.getElementById("auth-error");
const actionError = document.getElementById("action-error");
const actionForm = document.getElementById("action-form");

/* SIGN UP */
document.getElementById("signup-form").addEventListener("submit", async e => {
    e.preventDefault();
    authError.textContent = "";

    try {
        await http.post("/auth/signup", {
            login: signupLogin.value,
            password: signupPassword.value
        });
        alert("Регистрация успешна");
    } catch (e) {
        authError.textContent = "Ошибка регистрации";
    }
});

/* SIGN IN */
document.getElementById("signin-form").addEventListener("submit", async e => {
    e.preventDefault();
    authError.textContent = "";

    try {
        jwtToken = await http.post("/auth/signin", {
            login: signinLogin.value,
            password: signinPassword.value
        });

        authBlock.classList.add("hidden");
        actionsBlock.classList.remove("hidden");

        await ws.connect(jwtToken);
    } catch (e) {
        authError.textContent = "Неверный логин или пароль";
    }
});

/* ACTION SELECTION */
document.getElementById("action-create-chat").addEventListener("click", () => {
    actionForm.classList.remove("hidden");
});

/* CREATE CHAT */
document.getElementById("create-chat-btn").addEventListener("click", () => {
    actionError.textContent = "";

    const userId = Number(document.getElementById("chat-user-id").value);
    if (!userId) {
        actionError.textContent = "Некорректный ID";
        return;
    }

    try {
        const requestId = ws.send("/app/private-chat.create", { userId });
        console.log("Отправлен requestId =", requestId);
    } catch (e) {
        actionError.textContent = "Ошибка WebSocket";
    }
});

