import { useState } from 'react';
import { Menu, Plus, Search, MessageCircle } from 'lucide-react';
import type { Chat } from '@/types';
import { formatDistanceToNow } from '@/utils/date';
import { motion } from 'framer-motion';
import { useAuth } from '@/context/AuthContext';

interface ChatListProps {
  chats: Chat[];
  selectedChatId?: string;
  onSelectChat: (chat: Chat) => void;
  onOpenProfile: () => void;
  onCreateChat: () => void;
}

export function ChatList({ chats, selectedChatId, onSelectChat, onOpenProfile, onCreateChat }: ChatListProps) {
  const [searchQuery, setSearchQuery] = useState('');
  const { user } = useAuth();

  const getChatName = (chat: Chat, currentUserId?: number) => {
    if (chat.name) return chat.name;
    if (chat.type === 'PERSONAL') {
      const otherParticipant = chat.participants.find(p => p.id !== currentUserId);
      return otherParticipant?.username || 'Unknown';
    }
    return chat.participants.map(p => p.username).join(', ');
  };

  const filteredChats = chats.filter(chat => {
    const name = getChatName(chat, user?.id);
    return name.toLowerCase().includes(searchQuery.toLowerCase());
  });

  const getChatInitial = (chat: Chat, currentUserId?: number) => {
    const name = getChatName(chat, currentUserId);
    return name.charAt(0).toUpperCase();
  };

  const truncateMessage = (text: string, maxLength: number = 40) => {
    if (!text) return '';
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
  };

  return (
    <div className="h-full flex flex-col bg-white dark:bg-gray-800 border-r border-gray-200 dark:border-gray-700">
      {/* Header */}
      <div className="p-4 border-b border-gray-200 dark:border-gray-700">
        <div className="flex items-center justify-between mb-4">
          <button
            onClick={onOpenProfile}
            className="p-2 -ml-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
          >
            <Menu className="w-6 h-6 text-gray-700 dark:text-gray-300" />
          </button>
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white">Chats</h2>
          <button
            onClick={onCreateChat}
            className="p-2 -mr-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors text-primary-600 dark:text-primary-400"
          >
            <Plus className="w-6 h-6" />
          </button>
        </div>

        {/* Search */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search chats..."
            className="w-full pl-10 pr-4 py-2 rounded-lg bg-gray-100 dark:bg-gray-700 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500"
          />
        </div>
      </div>

      {/* Chat List */}
      <div className="flex-1 overflow-y-auto">
        {filteredChats.length === 0 ? (
          <div className="h-full flex flex-col items-center justify-center text-gray-500 dark:text-gray-400">
            <MessageCircle className="w-12 h-12 mb-2 opacity-50" />
            <p>No chats yet</p>
            <p className="text-sm">Create a new chat to start messaging</p>
          </div>
        ) : (
          <div className="p-2 space-y-1">
            {filteredChats.map((chat, index) => (
              <motion.button
                key={chat.chatId}
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.05 }}
                onClick={() => onSelectChat(chat)}
                className={`w-full chat-item ${selectedChatId === chat.chatId ? 'chat-item-active' : ''}`}
              >
                {/* Avatar */}
                {chat.photoId ? (
                  <img
                    src={`http://localhost:1337/api/files?fileId=${chat.photoId}&chatId=${chat.chatId}&messageId=0`}
                    alt=""
                    className="chat-avatar object-cover"
                  />
                ) : (
                  <div className="chat-avatar">
                    {getChatInitial(chat, user?.id)}
                  </div>
                )}

                {/* Info */}
                <div className="flex-1 min-w-0 text-left">
                  <div className="flex items-center justify-between">
                    <h3 className="font-medium text-gray-900 dark:text-white truncate">
                      {getChatName(chat, user?.id)}
                    </h3>
                    {chat.lastMessage && (
                      <span className="text-xs text-gray-500 dark:text-gray-400 flex-shrink-0">
                        {formatDistanceToNow(chat.lastMessage.sendingTime * 1000)}
                      </span>
                    )}
                  </div>
                  <div className="flex items-center justify-between mt-0.5">
                    <p className="text-sm text-gray-500 dark:text-gray-400 truncate">
                      {chat.lastMessage ? truncateMessage(chat.lastMessage.messageText) : 'No messages yet'}
                    </p>
                    {chat.unreadMessages > 0 && (
                      <span className="ml-2 px-2 py-0.5 bg-primary-500 text-white text-xs font-medium rounded-full flex-shrink-0">
                        {chat.unreadMessages}
                      </span>
                    )}
                  </div>
                </div>
              </motion.button>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
