import { useEffect, useRef, useState } from 'react';
import { Users, X } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import type { Chat, MessageWithStatus } from '@/types';

interface MessageContextMenuProps {
  x: number;
  y: number;
  message: MessageWithStatus;
  chat: Chat;
  onClose: () => void;
}

export function MessageContextMenu({ x, y, message, chat, onClose }: MessageContextMenuProps) {
  const menuRef = useRef<HTMLDivElement>(null);
  const [showReadBy, setShowReadBy] = useState(false);

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        onClose();
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [onClose]);

  // Adjust position to keep menu on screen
  const adjustedX = Math.min(x, window.innerWidth - 200);
  const adjustedY = Math.min(y, window.innerHeight - 100);

  const getReadByUsers = () => {
    if (!message.usersViews || message.usersViews.length === 0) return [];
    return chat.participants.filter(p => message.usersViews?.includes(p.id));
  };

  return (
    <>
      <div
        ref={menuRef}
        style={{ left: adjustedX, top: adjustedY }}
        className="fixed z-50 bg-white dark:bg-gray-800 rounded-lg shadow-xl border border-gray-200 dark:border-gray-700 py-1 min-w-[180px]"
      >
        <button
          onClick={() => setShowReadBy(true)}
          className="w-full px-4 py-2 text-left text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 flex items-center gap-2 transition-colors"
        >
          <Users className="w-4 h-4" />
          View Read By
        </button>
      </div>

      {/* Read By Modal */}
      <AnimatePresence>
        {showReadBy && (
          <div className="fixed inset-0 z-[60] flex items-center justify-center p-4">
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setShowReadBy(false)}
              className="absolute inset-0 bg-black/50"
            />
            
            <motion.div
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.95 }}
              className="relative bg-white dark:bg-gray-800 rounded-2xl shadow-2xl w-full max-w-sm"
            >
              <div className="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
                <h3 className="text-lg font-semibold text-gray-900 dark:text-white">
                  Read By
                </h3>
                <button
                  onClick={() => setShowReadBy(false)}
                  className="p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
                >
                  <X className="w-5 h-5 text-gray-700 dark:text-gray-300" />
                </button>
              </div>

              <div className="p-4 max-h-64 overflow-y-auto">
                {getReadByUsers().length === 0 ? (
                  <p className="text-center text-gray-500 dark:text-gray-400 py-4">
                    No one has read this message yet
                  </p>
                ) : (
                  <div className="space-y-2">
                    {getReadByUsers().map(user => (
                      <div
                        key={user.id}
                        className="flex items-center gap-3 p-2 rounded-lg bg-gray-50 dark:bg-gray-700/50"
                      >
                        <div className="w-8 h-8 rounded-full bg-gradient-to-br from-primary-500 to-primary-600 flex items-center justify-center text-white text-sm font-medium">
                          {user.username.charAt(0).toUpperCase()}
                        </div>
                        <span className="text-gray-900 dark:text-white font-medium">
                          {user.username}
                        </span>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </>
  );
}
