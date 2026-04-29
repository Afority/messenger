import { useState, useEffect, useRef, useCallback } from 'react';
import { ArrowLeft, Send, Paperclip, Check, CheckCheck, X, Users, FileText, Download } from 'lucide-react';
import { useWebSocket } from '@/context/WebSocketContext';
import type { Chat, MessageWithStatus, Attachment } from '@/types';
import { formatTime } from '@/utils/date';
import { MessageContextMenu } from './MessageContextMenu';

interface ChatViewProps {
  chat: Chat;
  onBack?: () => void;
  currentUserId: number;
}

export function ChatView({ chat, onBack, currentUserId }: ChatViewProps) {
  const [messages, setMessages] = useState<MessageWithStatus[]>([]);
  const [inputText, setInputText] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const [isUploading, setIsUploading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const firstUnreadRef = useRef<HTMLDivElement>(null);
  const markedAsReadRef = useRef<Set<number>>(new Set()); // Track already sent markAsRead requests
  const [firstUnreadIndex, setFirstUnreadIndex] = useState<number>(-1);
  const { getMessages, sendMessage, markAsRead, messages: contextMessages, uploadFile } = useWebSocket();
  const [selectedMessage, setSelectedMessage] = useState<MessageWithStatus | null>(null);
  const [contextMenuPosition, setContextMenuPosition] = useState<{ x: number; y: number } | null>(null);
  const [showGroupInfo, setShowGroupInfo] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  // Reset marked-as-read tracking when chat changes
  useEffect(() => {
    markedAsReadRef.current.clear();
  }, [chat.chatId]);

  // Load messages when chat changes
  useEffect(() => {
    const loadMessages = async () => {
      setIsLoading(true);
      setMessages([]); // Clear previous messages
      setFirstUnreadIndex(-1);
      try {
        const loadedMessages = await getMessages(chat.chatId, 0, 100);
        const messagesWithStatus: MessageWithStatus[] = loadedMessages.map(m => ({
          ...m,
          status: m.senderId === currentUserId 
            ? (m.usersViews?.length > 0 ? 'read' : 'sent')
            : undefined as never,
        }));
        // Sort by messageNumber ascending (oldest first, newest last)
        const sortedMessages = messagesWithStatus.sort((a, b) => a.messageNumber - b.messageNumber);
        setMessages(sortedMessages);
        
        // Find first unread message from others
        const firstUnread = sortedMessages.findIndex(
          m => m.senderId !== currentUserId && !m.usersViews?.includes(currentUserId)
        );
        if (firstUnread !== -1) {
          setFirstUnreadIndex(firstUnread);
        }
      } finally {
        setIsLoading(false);
      }
    };

    loadMessages();
  }, [chat.chatId, getMessages, currentUserId]);

  // Scroll to first unread message after loading
  useEffect(() => {
    if (firstUnreadIndex >= 0 && firstUnreadRef.current) {
      firstUnreadRef.current.scrollIntoView({ behavior: 'auto', block: 'center' });
    }
  }, [firstUnreadIndex, isLoading]);

  // Check if message was read by someone other than the sender
  const isReadByOthers = (msg: MessageWithStatus) => {
    if (!msg.usersViews || msg.usersViews.length === 0) return false;
    // For 1-on-1: check if the other participant read it
    if (chat.type === 'PERSONAL') {
      const otherParticipant = chat.participants.find(p => p.id !== currentUserId);
      if (!otherParticipant) return false;
      return msg.usersViews.includes(otherParticipant.id);
    }
    // For group: check if anyone other than the sender read it
    return msg.usersViews.some(id => id !== msg.senderId);
  };

  // Sync with context messages - only add NEW messages (real-time updates)
  useEffect(() => {
    const chatMessages = contextMessages[chat.chatId];
    if (!chatMessages || chatMessages.length === 0) return;

    setMessages(prev => {
      // Always accept messages from context if we have none locally
      if (prev.length === 0) {
        const allMessages: MessageWithStatus[] = chatMessages.map(msg => ({
          ...msg,
          status: (msg.senderId === currentUserId 
            ? (msg.usersViews?.length > 0 ? 'read' : 'sent')
            : 'sent') as 'read' | 'sent' | 'pending',
        }));
        return allMessages.sort((a, b) => a.messageNumber - b.messageNumber);
      }
      
      let hasChanges = false;
      const updatedMessages = prev.map(localMsg => {
        const contextMsg = chatMessages.find(m => m.messageNumber === localMsg.messageNumber);
        if (contextMsg) {
          // Check if usersViews changed (new reader)
          const localViews = localMsg.usersViews || [];
          const contextViews = contextMsg.usersViews || [];
          const hasNewViews = contextViews.some(id => !localViews.includes(id));
          if (hasNewViews) {
            hasChanges = true;
            return {
              ...localMsg,
              usersViews: contextViews,
              status: localMsg.senderId === currentUserId
                ? (contextViews.length > 0 ? 'read' : localMsg.status)
                : localMsg.status,
            };
          }
        }
        return localMsg;
      });
      
      // Find the highest message number we currently have
      const maxMessageNumber = Math.max(...prev.map(m => m.messageNumber), -1);
      
      // Only add messages that are newer than what we have
      const trulyNewMessages: MessageWithStatus[] = [];
      for (const msg of chatMessages) {
        const exists = prev.find(m => m.messageNumber === msg.messageNumber);
        if (!exists && msg.messageNumber > maxMessageNumber) {
          trulyNewMessages.push({
            ...msg,
            status: msg.senderId === currentUserId 
              ? (msg.usersViews?.length > 0 ? 'read' : 'sent')
              : undefined as never,
          });
          hasChanges = true;
        }
      }
      
      if (!hasChanges) return prev;
      if (trulyNewMessages.length === 0) return updatedMessages;
      return [...updatedMessages, ...trulyNewMessages].sort((a, b) => a.messageNumber - b.messageNumber);
    });

    // Mark received messages as read (only those not already read and not already requested)
    const unreadMessages = chatMessages.filter(
      m => m.senderId !== currentUserId && 
           !m.usersViews?.includes(currentUserId) &&
           !markedAsReadRef.current.has(m.messageNumber)
    );
    if (unreadMessages.length > 0) {
      // Track these IDs as being sent
      unreadMessages.forEach(m => markedAsReadRef.current.add(m.messageNumber));
      markAsRead(chat.chatId, unreadMessages.map(m => m.messageNumber));
    }
  }, [contextMessages, chat.chatId, currentUserId, markAsRead]);

  // Mark unread messages as read (only those not already read and not already requested)
  useEffect(() => {
    const unreadMessages = messages.filter(
      m => m.senderId !== currentUserId && 
           !m.usersViews?.includes(currentUserId) &&
           !markedAsReadRef.current.has(m.messageNumber)
    );
    
    if (unreadMessages.length > 0) {
      // Track these IDs as being sent
      unreadMessages.forEach(m => markedAsReadRef.current.add(m.messageNumber));
      markAsRead(chat.chatId, unreadMessages.map(m => m.messageNumber));
    }
  }, [messages, currentUserId, chat.chatId, markAsRead]);

  // Scroll to bottom immediately without animation
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'auto' });
  }, [messages]);

  // Note: WebSocket connection is managed by WebSocketContext
  // This component receives messages through the shared context state

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(e.target.files || []);
    setSelectedFiles(prev => [...prev, ...files]);
  };

  const handleRemoveFile = (index: number) => {
    setSelectedFiles(prev => prev.filter((_, i) => i !== index));
  };

  const handleSend = async () => {
    if (!inputText.trim() && selectedFiles.length === 0) return;
    
    const text = inputText.trim();
    setInputText('');
    
    if (selectedFiles.length > 0) {
      setIsUploading(true);
      try {
        const uploadedAttachments: Attachment[] = [];
        for (const file of selectedFiles) {
          const attachment = await uploadFile(file);
          uploadedAttachments.push(attachment);
        }
        
        await sendMessage(4, {
          chatId: chat.chatId,
          messageText: text,
          attachments: uploadedAttachments.map(a => a.fileId),
          userEventId: 0
        });
      } catch {
        // Error is handled by WebSocketContext
      } finally {
        setIsUploading(false);
        setSelectedFiles([]);
      }
    } else {
      try {
        await sendMessage(4, {
          chatId: chat.chatId,
          messageText: text,
          attachments: [],
          userEventId: 0
        });
      } catch {
        // Error is handled by WebSocketContext
      }
    }
  };

  const handleContextMenu = useCallback((e: React.MouseEvent, message: MessageWithStatus) => {
    e.preventDefault();
    setSelectedMessage(message);
    setContextMenuPosition({ x: e.clientX, y: e.clientY });
  }, []);

  const handleCloseContextMenu = () => {
    setContextMenuPosition(null);
    setSelectedMessage(null);
  };

  const getMessageStatus = (msg: MessageWithStatus) => {
    if (msg.senderId !== currentUserId) return null;
    
    // Check if read by someone other than the sender
    const isRead = isReadByOthers(msg);
    
    if (isRead) {
      return <CheckCheck className="w-4 h-4 text-blue-400" />;
    }
    
    return <Check className="w-4 h-4 text-gray-400" />;
  };

  const getChatName = () => {
    if (!chat.participants || chat.participants.length === 0) return 'Unknown';
    if (chat.type === 'GROUP') {
      return chat.name || 'Unnamed Group';
    }
    // For personal chat, return the other participant's username
    const otherParticipant = chat.participants.find(p => p.id !== currentUserId);
    return otherParticipant?.username || 'Unknown';
  };

  return (
    <div className="h-full flex flex-col bg-white dark:bg-gray-900">
      {/* Header */}
      <div className="flex items-center gap-3 p-4 border-b border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800">
        {onBack && (
          <button
            onClick={onBack}
            className="p-2 -ml-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
          >
            <ArrowLeft className="w-5 h-5 text-gray-700 dark:text-gray-300" />
          </button>
        )}
        
        {chat.photoId ? (
          <img
            src={`http://localhost:1337/api/files?fileId=${chat.photoId}&chatId=${chat.chatId}&messageId=0`}
            alt=""
            className="w-10 h-10 rounded-full object-cover"
          />
        ) : (
          <div className="w-10 h-10 rounded-full bg-gradient-to-br from-primary-500 to-primary-600 flex items-center justify-center text-white font-semibold">
            {getChatName().charAt(0).toUpperCase()}
          </div>
        )}
        
        <button 
          className="flex-1 text-left"
          onClick={() => chat.type === 'GROUP' && setShowGroupInfo(true)}
        >
          <h2 className="font-semibold text-gray-900 dark:text-white">{getChatName()}</h2>
          <p className="text-xs text-gray-500 dark:text-gray-400">
            {chat.type === 'GROUP' 
              ? `${chat.participants?.length || 0} participants` 
              : chat.participants?.find(p => p.id !== currentUserId)?.username || 'Unknown'}
          </p>
        </button>
      </div>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto p-4 space-y-3">
        {isLoading ? (
          <div className="h-full flex items-center justify-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-500" />
          </div>
        ) : messages.length === 0 ? (
          <div className="h-full flex flex-col items-center justify-center text-gray-500 dark:text-gray-400">
            <p>No messages yet</p>
            <p className="text-sm">Start a conversation!</p>
          </div>
        ) : (
          <div className="space-y-3">
            {messages.map((msg, index) => {
              const isOwn = msg.senderId === currentUserId;
              const showAvatar = !isOwn && (index === 0 || messages[index - 1].senderId !== msg.senderId);
              const isFirstUnread = index === firstUnreadIndex;
              
              return (
                <div key={msg.messageNumber}>
                  {/* Unread banner */}
                  {isFirstUnread && (
                    <div
                      ref={firstUnreadRef}
                      className="flex items-center gap-3 my-4"
                    >
                      <div className="flex-1 h-px bg-primary-500/50" />
                      <span className="text-xs font-medium text-primary-500 px-2 py-1 rounded-full bg-primary-500/10">
                        Unread messages
                      </span>
                      <div className="flex-1 h-px bg-primary-500/50" />
                    </div>
                  )}
                  <div
                    className={`flex ${isOwn ? 'justify-end' : 'justify-start'}`}
                    onContextMenu={(e) => handleContextMenu(e, msg)}
                  >
                  <div className={`flex items-end gap-2 max-w-[70%] ${isOwn ? 'flex-row-reverse' : 'flex-row'}`}>
                    {/* Avatar */}
                    {!isOwn && showAvatar && (
                      <div className="w-8 h-8 rounded-full bg-gradient-to-br from-primary-500 to-primary-600 flex items-center justify-center text-white text-sm flex-shrink-0">
                        {chat.participants.find(p => p.id === msg.senderId)?.username.charAt(0).toUpperCase() || '?'}
                      </div>
                    )}
                    {!isOwn && !showAvatar && <div className="w-8 flex-shrink-0" />}
                    
                    {/* Message Bubble */}
                    <div
                      className={`message-bubble ${isOwn ? 'message-bubble-sent' : 'message-bubble-received'}`}
                    >
                      {!isOwn && showAvatar && (
                        <p className="text-xs font-medium mb-1 opacity-75">
                          {chat.participants.find(p => p.id === msg.senderId)?.username}
                        </p>
                      )}
                      <p>{msg.messageText}</p>
                      
                      {/* Attachments */}
                      {msg.attachments && msg.attachments.length > 0 && (
                        <div className="mt-2 space-y-2">
                          {msg.attachments.map((attachment: Attachment) => {
                            const isImage = attachment.mimeType.startsWith('image/');
                            if (isImage) {
                              return (
                                <img
                                  key={attachment.fileId}
                                  src={`http://localhost:1337/api/files?fileId=${attachment.fileId}&chatId=${chat.chatId}&messageId=${msg.messageNumber}`}
                                  alt={attachment.filename}
                                  className="max-w-full rounded-lg cursor-pointer hover:opacity-90 transition-opacity"
                                />
                              );
                            } else {
                              return (
                                <a
                                  key={attachment.fileId}
                                  href={`http://localhost:1337/api/files?fileId=${attachment.fileId}&chatId=${chat.chatId}&messageId=${msg.messageNumber}`}
                                  download={attachment.filename}
                                  className="flex items-center gap-2 p-2 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors"
                                >
                                  <FileText className="w-4 h-4 text-gray-600 dark:text-gray-300" />
                                  <span className="text-sm text-gray-700 dark:text-gray-300 truncate flex-1">
                                    {attachment.filename}
                                  </span>
                                  <Download className="w-4 h-4 text-gray-500 dark:text-gray-400" />
                                </a>
                              );
                            }
                          })}
                        </div>
                      )}
                      
                      <div className={`flex items-center gap-1 mt-1 ${isOwn ? 'justify-end' : 'justify-start'}`}>
                        <span className="text-xs opacity-70">
                          {formatTime(msg.sendingTime * 1000)}
                        </span>
                        {getMessageStatus(msg)}
                      </div>
                    </div>
                  </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Input */}
      <div className="p-4 border-t border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800">
        {/* Selected Files Preview */}
        {selectedFiles.length > 0 && (
          <div className="mb-3 flex flex-wrap gap-2">
            {selectedFiles.map((file, index) => (
              <div
                key={index}
                className="flex items-center gap-2 px-3 py-2 bg-gray-100 dark:bg-gray-700 rounded-lg text-sm"
              >
                <FileText className="w-4 h-4 text-gray-500 dark:text-gray-400" />
                <span className="text-gray-700 dark:text-gray-300 truncate max-w-[150px]">
                  {file.name}
                </span>
                <button
                  onClick={() => handleRemoveFile(index)}
                  className="text-gray-500 hover:text-red-500 dark:text-gray-400 dark:hover:text-red-400"
                >
                  <X className="w-4 h-4" />
                </button>
              </div>
            ))}
          </div>
        )}
        
        <div className="flex items-center gap-2">
          <input
            ref={fileInputRef}
            type="file"
            multiple
            onChange={handleFileSelect}
            className="hidden"
          />
          <button
            onClick={() => fileInputRef.current?.click()}
            className="p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 text-gray-500 dark:text-gray-400 transition-colors"
          >
            <Paperclip className="w-5 h-5" />
          </button>
          <input
            type="text"
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSend()}
            placeholder="Type a message..."
            className="flex-1 px-4 py-2 rounded-full bg-gray-100 dark:bg-gray-700 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500"
          />
          <button
            onClick={handleSend}
            disabled={(!inputText.trim() && selectedFiles.length === 0) || isUploading}
            className="p-2 rounded-full bg-primary-600 hover:bg-primary-700 text-white disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {isUploading ? (
              <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
            ) : (
              <Send className="w-5 h-5" />
            )}
          </button>
        </div>
      </div>

      {/* Context Menu */}
      {contextMenuPosition && selectedMessage && (
        <MessageContextMenu
          x={contextMenuPosition.x}
          y={contextMenuPosition.y}
          message={selectedMessage}
          chat={chat}
          onClose={handleCloseContextMenu}
        />
      )}

      {/* Group Info Panel */}
      {showGroupInfo && chat.type === 'GROUP' && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <div className="w-full max-w-md mx-4 bg-white dark:bg-gray-800 rounded-2xl shadow-xl overflow-hidden">
            {/* Header */}
            <div className="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
              <h3 className="text-lg font-semibold text-gray-900 dark:text-white">Group Info</h3>
              <button
                onClick={() => setShowGroupInfo(false)}
                className="p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
              >
                <X className="w-5 h-5 text-gray-500 dark:text-gray-400" />
              </button>
            </div>

            {/* Content */}
            <div className="p-4 max-h-[70vh] overflow-y-auto">
              {/* Group Photo & Name */}
              <div className="flex items-center gap-4 mb-6">
                {chat.photoId ? (
                  <img
                    src={`http://localhost:1337/api/files?fileId=${chat.photoId}&chatId=${chat.chatId}&messageId=0`}
                    alt=""
                    className="w-16 h-16 rounded-full object-cover"
                  />
                ) : (
                  <div className="w-16 h-16 rounded-full bg-gradient-to-br from-primary-500 to-primary-600 flex items-center justify-center text-white text-2xl font-semibold">
                    {chat.name?.charAt(0).toUpperCase() || 'G'}
                  </div>
                )}
                <div>
                  <h4 className="font-semibold text-gray-900 dark:text-white text-lg">{chat.name || 'Unnamed Group'}</h4>
                  <p className="text-sm text-gray-500 dark:text-gray-400">{chat.participants.length} participants</p>
                </div>
              </div>

              {/* Description */}
              {chat.description && (
                <div className="mb-6">
                  <h5 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Description</h5>
                  <p className="text-gray-600 dark:text-gray-400 text-sm bg-gray-50 dark:bg-gray-700 rounded-lg p-3">
                    {chat.description}
                  </p>
                </div>
              )}

              {/* Participants */}
              <div>
                <h5 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-3 flex items-center gap-2">
                  <Users className="w-4 h-4" />
                  Participants
                </h5>
                <div className="space-y-2">
                  {chat.participants.map(participant => (
                    <div 
                      key={participant.id}
                      className="flex items-center gap-3 p-2 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                    >
                      <div className="w-8 h-8 rounded-full bg-gradient-to-br from-primary-500 to-primary-600 flex items-center justify-center text-white text-sm font-medium">
                        {participant.username.charAt(0).toUpperCase()}
                      </div>
                      <span className="text-gray-900 dark:text-white text-sm">
                        {participant.username}
                        {participant.id === currentUserId && (
                          <span className="text-gray-400 dark:text-gray-500 ml-1">(You)</span>
                        )}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
