import { createContext, useContext, useEffect, useRef, useState, useCallback, ReactNode } from 'react';
import { useAuth } from './AuthContext';
import type { Chat, Message, WebSocketMessage, User, LastMessage, Attachment } from '@/types';

interface WebSocketContextType {
  isConnected: boolean;
  chats: Chat[];
  messages: Record<string, Message[]>;
  sendMessage: (opcode: number, payload: unknown, userEventId?: number) => Promise<unknown>;
  createPrivateChat: (userId: number) => Promise<void>;
  createGroupChat: (usersIds: number[], name: string, description: string, photoId: string | null) => Promise<void>;
  getChats: () => Promise<void>;
  getMessages: (chatId: string, start: number, limit: number) => Promise<Message[]>;
  markAsRead: (chatId: string, messagesNumbers: number[]) => Promise<void>;
  searchUsers: (username: string) => Promise<{ id: number; username: string }[]>;
  uploadFile: (file: File) => Promise<Attachment>;
  pendingMessages: Set<string>;
  readMessages: Record<string, Set<number>>;
  updateChatLastMessage: (chatId: string, message: Message) => void;
  lastError: string | null;
  clearError: () => void;
}

const WebSocketContext = createContext<WebSocketContextType | undefined>(undefined);

export function WebSocketProvider({ children }: { children: ReactNode }) {
  const { token, isAuthenticated, user } = useAuth();
  const wsRef = useRef<WebSocket | null>(null);
  const currentUserIdRef = useRef<number | null>(null);
  const [isConnected, setIsConnected] = useState(false);
  const [chats, setChats] = useState<Chat[]>([]);
  const [messages, setMessages] = useState<Record<string, Message[]>>({});
  const pendingResolvers = useRef<Map<number, { resolve: (value: unknown) => void; reject: (reason?: unknown) => void }>>(new Map());
  const requestIdRef = useRef(1);
  const [pendingMessages, setPendingMessages] = useState<Set<string>>(new Set());
  const [readMessages, setReadMessages] = useState<Record<string, Set<number>>>({});
  const [lastError, setLastError] = useState<string | null>(null);

  useEffect(() => {
    if (!isAuthenticated || !token) {
      setIsConnected(false);
      return;
    }

    // Store current user ID for event handling
    currentUserIdRef.current = user?.id || null;

    const ws = new WebSocket(`/ws?token=${token}`);
    wsRef.current = ws;

    ws.onopen = () => {
      setIsConnected(true);
      getChats();
    };

    ws.onclose = () => {
      setIsConnected(false);
    };

    ws.onerror = (error) => {
      console.error('WebSocket error:', error);
      setIsConnected(false);
    };

    ws.onmessage = (event) => {
      const data: WebSocketMessage = JSON.parse(event.data);
      
      if (data.type === 'RESPONSE') {
        const resolver = pendingResolvers.current.get(data.requestId!);
        if (resolver) {
          if (data.payload && typeof data.payload === 'object' && 'success' in data.payload && !data.payload.success) {
            const errorMessage = (data.payload as { message?: string }).message || 'Request failed';
            setLastError(errorMessage);
            resolver.reject(new Error(errorMessage));
          } else {
            resolver.resolve(data.payload);
          }
          pendingResolvers.current.delete(data.requestId!);
        }
      } else if (data.type === 'EVENT') {
        handleEvent(data);
      }
    };

    // Heartbeat: send ping with opcode 0 every 5 seconds
    const heartbeatInterval = setInterval(() => {
      if (ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify({ opcode: 0, requestId: requestIdRef.current++ }));
      }
    }, 5000);

    return () => {
      clearInterval(heartbeatInterval);
      ws.close();
    };
  }, [token, isAuthenticated]);

  const handleEvent = useCallback((event: WebSocketMessage) => {
    switch (event.opcode) {
      case 1: // Private chat created
      case 2: { // Group chat created
        const payload = event.payload as { users?: User[]; participants?: User[]; chatId: string; type?: 'PERSONAL' | 'GROUP'; lastMessageNumber?: number; name?: string | null; description?: string | null; photoId?: string | null; lastMessage?: unknown; unreadMessages?: number };
        // Transform server payload to Chat type (server uses 'users', we need 'participants')
        const chatData: Chat = {
          chatId: payload.chatId,
          type: payload.type || (event.opcode === 1 ? 'PERSONAL' : 'GROUP'),
          participants: payload.participants || payload.users || [],
          lastMessageNumber: payload.lastMessageNumber || 0,
          name: payload.name || null,
          description: payload.description || null,
          photoId: payload.photoId || null,
          lastMessage: payload.lastMessage as LastMessage | null || null,
          unreadMessages: payload.unreadMessages || 0,
        };
        setChats(prev => {
          if (prev.find(c => c.chatId === chatData.chatId)) return prev;
          return [chatData, ...prev];
        });
        break;
      }
      case 3: { // Message received
        const msg = event.payload as Message;
        setMessages(prev => {
          const chatMessages = prev[msg.chatId] || [];
          if (chatMessages.find(m => m.messageNumber === msg.messageNumber)) {
            return prev;
          }
          return {
            ...prev,
            [msg.chatId]: [...chatMessages, msg],
          };
        });
        
        // Mark as confirmed if it was pending
        if (event.userEventId) {
          setPendingMessages(prev => {
            const newSet = new Set(prev);
            newSet.delete(event.userEventId!.toString());
            return newSet;
          });
        }
        
        // Update chat last message and move to top
        setChats(prev => {
          const chatIndex = prev.findIndex(c => c.chatId === msg.chatId);
          if (chatIndex === -1) return prev;
          
          const newChats = [...prev];
          const chat = { ...newChats[chatIndex] };
          chat.lastMessage = {
            senderId: msg.senderId,
            messageText: msg.messageText || '',
            sendingTime: msg.sendingTime,
          };
          // Only increment unread count if message is from another user
          if (msg.senderId !== currentUserIdRef.current) {
            chat.unreadMessages++;
          }
          chat.lastMessageNumber = msg.messageNumber;
          
          newChats.splice(chatIndex, 1);
          return [chat, ...newChats];
        });
        break;
      }
      case 4: { // Messages read
        const readData = event.payload as { chatId: string; messagesIds: number[]; userId: number };
        setReadMessages(prev => ({
          ...prev,
          [readData.chatId]: new Set([...(prev[readData.chatId] || []), ...readData.messagesIds]),
        }));
        // Also update the messages state to add the reader to usersViews
        setMessages(prev => {
          const chatMessages = prev[readData.chatId];
          if (!chatMessages) return prev;
          return {
            ...prev,
            [readData.chatId]: chatMessages.map(msg => 
              readData.messagesIds.includes(msg.messageNumber) && !msg.usersViews?.includes(readData.userId)
                ? { ...msg, usersViews: [...(msg.usersViews || []), readData.userId] }
                : msg
            ),
          };
        });
        // Update chat unread count if current user is the one who read messages
        if (readData.userId === currentUserIdRef.current) {
          setChats(prev => prev.map(chat => 
            chat.chatId === readData.chatId ? { ...chat, unreadMessages: 0 } : chat
          ));
        }
        break;
      }
    }
  }, []);

  const sendMessage = useCallback((opcode: number, payload: unknown, userEventId?: number): Promise<unknown> => {
    return new Promise((resolve, reject) => {
      if (!wsRef.current || wsRef.current.readyState !== WebSocket.OPEN) {
        reject(new Error('WebSocket not connected'));
        return;
      }

      const requestId = requestIdRef.current++;
      pendingResolvers.current.set(requestId, { resolve, reject });


      wsRef.current.send(JSON.stringify({
        opcode,
        requestId,
        payload
      }));

      // Timeout after 10 seconds
      setTimeout(() => {
        if (pendingResolvers.current.has(requestId)) {
          pendingResolvers.current.delete(requestId);
          reject(new Error('Request timeout'));
        }
      }, 10000);
    });
  }, []);

  const createPrivateChat = useCallback(async (userId: number) => {
    const userEventId = requestIdRef.current++;
    await sendMessage(1, { userId, userEventId }, userEventId);
  }, [sendMessage]);

  const createGroupChat = useCallback(async (usersIds: number[], name: string, description: string, photoId: string | null) => {
    const userEventId = requestIdRef.current++;
    await sendMessage(2, { usersIds, name, description, photoId, userEventId }, userEventId);
  }, [sendMessage]);

  const getChats = useCallback(async () => {
    const response = await sendMessage(3, null) as Chat[];
    setChats(response.sort((a, b) => {
      if (!a.lastMessage && !b.lastMessage) return 0;
      if (!a.lastMessage) return 1;
      if (!b.lastMessage) return -1;
      return b.lastMessage.sendingTime - a.lastMessage.sendingTime;
    }));
  }, [sendMessage]);

  const getMessages = useCallback(async (chatId: string, start: number, limit: number) => {
    const response = await sendMessage(5, { chatId, start, limit }) as Message[] | { messages: Message[] };
    // Handle both direct array response and { messages: [...] } format
    const messagesArray = Array.isArray(response) ? response : response.messages || [];
    // Don't auto-save to context - let the caller handle it to avoid race conditions
    return messagesArray;
  }, [sendMessage]);

  const markAsRead = useCallback(async (chatId: string, messagesNumbers: number[]) => {
    if (messagesNumbers.length === 0) return;
    const userEventId = requestIdRef.current++;
    await sendMessage(7, { messagesNumbers, chatId, userEventId }, userEventId);
    
    setChats(prev => prev.map(chat => 
      chat.chatId === chatId ? { ...chat, unreadMessages: 0 } : chat
    ));
  }, [sendMessage]);

  const searchUsers = useCallback(async (username: string) => {
    const response = await sendMessage(6, { username }) as { users: { id: number; username: string }[] };
    return response.users;
  }, [sendMessage]);

  const clearError = useCallback(() => {
    setLastError(null);
  }, []);

  const uploadFile = useCallback(async (file: File): Promise<Attachment> => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch('/api/files/upload', {
      method: 'POST',
      body: formData,
    });

    if (!response.ok) {
      throw new Error('File upload failed');
    }

    const data = await response.json();
    return data as Attachment;
  }, []);

  const updateChatLastMessage = useCallback((chatId: string, message: Message) => {
    setChats(prev => {
      const chatIndex = prev.findIndex(c => c.chatId === chatId);
      if (chatIndex === -1) return prev;
      
      const newChats = [...prev];
      const chat = { ...newChats[chatIndex] };
      chat.lastMessage = {
        senderId: message.senderId,
        messageText: message.messageText || '',
        sendingTime: message.sendingTime,
      };
      
      newChats.splice(chatIndex, 1);
      return [chat, ...newChats];
    });
  }, []);

  return (
    <WebSocketContext.Provider
      value={{
        isConnected,
        chats,
        messages,
        sendMessage,
        createPrivateChat,
        createGroupChat,
        getChats,
        getMessages,
        markAsRead,
        searchUsers,
        uploadFile,
        pendingMessages,
        readMessages,
        updateChatLastMessage,
        lastError,
        clearError,
      }}
    >
      {children}
    </WebSocketContext.Provider>
  );
}

export function useWebSocket() {
  const context = useContext(WebSocketContext);
  if (context === undefined) {
    throw new Error('useWebSocket must be used within a WebSocketProvider');
  }
  return context;
}
