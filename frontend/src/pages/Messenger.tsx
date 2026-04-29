import { useState, useEffect } from 'react';
import { ChatList } from '@/components/ChatList';
import { ChatView } from '@/components/ChatView';
import { UserProfile } from '@/components/UserProfile';
import { CreateChatModal } from '@/components/CreateChatModal';
import { useWebSocket } from '@/context/WebSocketContext';
import { useAuth } from '@/context/AuthContext';
import type { Chat } from '@/types';
import { AnimatePresence, motion } from 'framer-motion';
import { X, AlertCircle } from 'lucide-react';

export function Messenger() {
  const [selectedChat, setSelectedChat] = useState<Chat | null>(null);
  const [showProfile, setShowProfile] = useState(false);
  const [showCreateChat, setShowCreateChat] = useState(false);
  const [sidebarWidth, setSidebarWidth] = useState(320);
  const [isMobile, setIsMobile] = useState(false);
  const [showMobileChat, setShowMobileChat] = useState(false);
  const { isConnected, chats, lastError, clearError } = useWebSocket();
  const { user } = useAuth();

  // Clear error after 5 seconds
  useEffect(() => {
    if (lastError) {
      const timer = setTimeout(() => {
        clearError();
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [lastError, clearError]);

  useEffect(() => {
    const checkMobile = () => {
      setIsMobile(window.innerWidth < 768);
    };
    
    checkMobile();
    window.addEventListener('resize', checkMobile);
    return () => window.removeEventListener('resize', checkMobile);
  }, []);

  // Sync selectedChat with updated chats data to prevent stale object
  useEffect(() => {
    if (selectedChat) {
      const updatedChat = chats.find(c => c.chatId === selectedChat.chatId);
      // Always update to get fresh data (unread count, last message, etc.)
      if (updatedChat) {
        setSelectedChat(updatedChat);
      }
    }
  }, [chats]);

  const handleChatSelect = (chat: Chat) => {
    setSelectedChat(chat);
    if (isMobile) {
      setShowMobileChat(true);
    }
  };

  const handleBackToList = () => {
    setShowMobileChat(false);
    setSelectedChat(null);
  };

  return (
    <div className="h-screen flex overflow-hidden bg-gray-100 dark:bg-gray-900">
      {/* Connection Status */}
      {!isConnected && (
        <div className="fixed top-0 left-0 right-0 z-50 bg-yellow-500 text-white text-center py-2 text-sm font-medium">
          Connecting...
        </div>
      )}

      {/* Error Notification */}
      <AnimatePresence>
        {lastError && (
          <motion.div
            initial={{ opacity: 0, y: -50 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -50 }}
            className="fixed top-4 left-1/2 -translate-x-1/2 z-[60] max-w-md w-full mx-4"
          >
            <div className="bg-red-500 text-white px-4 py-3 rounded-lg shadow-lg flex items-center gap-3">
              <AlertCircle className="w-5 h-5 flex-shrink-0" />
              <p className="flex-1 text-sm">{lastError}</p>
              <button
                onClick={clearError}
                className="p-1 hover:bg-red-600 rounded transition-colors"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* User Profile Sidebar */}
      <UserProfile isOpen={showProfile} onClose={() => setShowProfile(false)} />

      {/* Create Chat Modal */}
      <AnimatePresence>
        {showCreateChat && (
          <CreateChatModal onClose={() => setShowCreateChat(false)} />
        )}
      </AnimatePresence>

      {isMobile ? (
        // Mobile Layout
        <div className="w-full h-full">
          {showMobileChat && selectedChat ? (
            <ChatView
              chat={selectedChat}
              onBack={handleBackToList}
              currentUserId={user?.id || 0}
            />
          ) : (
            <ChatList
              chats={chats}
              selectedChatId={selectedChat?.chatId}
              onSelectChat={handleChatSelect}
              onOpenProfile={() => setShowProfile(true)}
              onCreateChat={() => setShowCreateChat(true)}
            />
          )}
        </div>
      ) : (
        // Desktop Layout
        <>
          {/* Chat List Sidebar */}
          <div
            style={{ width: sidebarWidth }}
            className="flex-shrink-0 h-full"
          >
            <ChatList
              chats={chats}
              selectedChatId={selectedChat?.chatId}
              onSelectChat={handleChatSelect}
              onOpenProfile={() => setShowProfile(true)}
              onCreateChat={() => setShowCreateChat(true)}
            />
          </div>

          {/* Resize Handle */}
          <div
            className="w-1 bg-gray-300 dark:bg-gray-700 cursor-col-resize hover:bg-primary-500 dark:hover:bg-primary-500 transition-colors"
            onMouseDown={(e) => {
              const startX = e.clientX;
              const startWidth = sidebarWidth;
              
              const handleMouseMove = (e: MouseEvent) => {
                const newWidth = Math.max(250, Math.min(500, startWidth + e.clientX - startX));
                setSidebarWidth(newWidth);
              };
              
              const handleMouseUp = () => {
                document.removeEventListener('mousemove', handleMouseMove);
                document.removeEventListener('mouseup', handleMouseUp);
              };
              
              document.addEventListener('mousemove', handleMouseMove);
              document.addEventListener('mouseup', handleMouseUp);
            }}
          />

          {/* Chat View */}
          <div className="flex-1 h-full">
            {selectedChat ? (
              <ChatView
                chat={selectedChat}
                currentUserId={user?.id || 0}
              />
            ) : (
              <div className="h-full flex items-center justify-center">
                <div className="text-center">
                  <div className="w-20 h-20 rounded-full bg-gray-200 dark:bg-gray-700 flex items-center justify-center mx-auto mb-4">
                    <span className="text-4xl">💬</span>
                  </div>
                  <p className="text-gray-500 dark:text-gray-400 text-lg">
                    Select a chat to start messaging
                  </p>
                </div>
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
}
