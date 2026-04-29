// Types for messenger

export interface User {
  id: number;
  username: string;
}

export interface AuthUser extends User {
  login?: string;
}

export interface AuthResponse {
  user: User;
  token: string;
}

export interface Chat {
  chatId: string;
  type: 'PERSONAL' | 'GROUP';
  participants: User[];
  lastMessageNumber: number;
  name: string | null;
  description: string | null;
  photoId: string | null;
  lastMessage: LastMessage | null;
  unreadMessages: number;
}

export interface LastMessage {
  senderId: number;
  messageText: string;
  sendingTime: number;
}

export interface Attachment {
  fileId: string;
  filename: string;
  mimeType: string;
  size: number;
}

export interface Message {
  messageNumber: number;
  senderId: number;
  chatId: string;
  usersViews: number[];
  messageText: string | null;
  attachments: Attachment[] | null;
  sendingTime: number;
}

export interface MessageWithStatus extends Message {
  status: 'pending' | 'sent' | 'read';
}

export interface WebSocketMessage {
  type: 'REQUEST' | 'RESPONSE' | 'EVENT';
  requestId?: number;
  userEventId?: number;
  opcode: number;
  payload: unknown;
}

export interface WebSocketResponse {
  requestId: number;
  type: 'RESPONSE';
  payload: {
    success: boolean;
    message?: string;
    [key: string]: unknown;
  };
}

export interface WebSocketEvent {
  type: 'EVENT';
  userEventId?: number;
  opcode: number;
  payload: unknown;
}

// Opcodes
export const OPCODE = {
  // Requests
  CREATE_PRIVATE_CHAT: 1,
  CREATE_GROUP_CHAT: 2,
  GET_CHATS: 3,
  SEND_MESSAGE: 4,
  GET_MESSAGES: 5,
  GET_USER_ID: 6,
  MARK_READ: 7,
  
  // Events
  EVENT_PRIVATE_CHAT_CREATED: 1,
  EVENT_GROUP_CHAT_CREATED: 2,
  EVENT_MESSAGE_RECEIVED: 3,
  EVENT_MESSAGES_READ: 4,
} as const;

export type Theme = 'light' | 'dark';
