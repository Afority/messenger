import { X, LogOut, Moon, Sun, User, AtSign } from 'lucide-react';
import { useAuth } from '@/context/AuthContext';
import { useTheme } from '@/context/ThemeContext';
import { motion, AnimatePresence } from 'framer-motion';

interface UserProfileProps {
  isOpen: boolean;
  onClose: () => void;
}

export function UserProfile({ isOpen, onClose }: UserProfileProps) {
  const { user, logout } = useAuth();
  const { theme, toggleTheme } = useTheme();

  return (
    <AnimatePresence>
      {isOpen && (
        <>
          {/* Backdrop */}
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
            className="fixed inset-0 bg-black/50 z-40"
          />

          {/* Sidebar */}
          <motion.div
            initial={{ x: '-100%' }}
            animate={{ x: 0 }}
            exit={{ x: '-100%' }}
            transition={{ type: 'spring', damping: 25, stiffness: 200 }}
            className="fixed left-0 top-0 bottom-0 w-80 bg-white dark:bg-gray-800 z-50 shadow-xl"
          >
            <div className="h-full flex flex-col">
              {/* Header */}
              <div className="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
                <h2 className="text-lg font-semibold text-gray-900 dark:text-white">Profile</h2>
                <button
                  onClick={onClose}
                  className="p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
                >
                  <X className="w-5 h-5 text-gray-700 dark:text-gray-300" />
                </button>
              </div>

              {/* Profile Content */}
              <div className="flex-1 overflow-y-auto p-4">
                {/* Avatar */}
                <div className="flex flex-col items-center mb-6">
                  <div className="w-24 h-24 rounded-full bg-gradient-to-br from-primary-500 to-primary-600 flex items-center justify-center text-white text-3xl font-bold mb-3">
                    {user?.username.charAt(0).toUpperCase()}
                  </div>
                  <h3 className="text-xl font-bold text-gray-900 dark:text-white">{user?.username}</h3>
                </div>

                {/* Info Cards */}
                <div className="space-y-3">
                  <div className="p-4 bg-gray-50 dark:bg-gray-700/50 rounded-xl">
                    <div className="flex items-center gap-3 mb-2">
                      <User className="w-5 h-5 text-primary-500" />
                      <span className="text-sm font-medium text-gray-500 dark:text-gray-400">Username</span>
                    </div>
                    <p className="text-gray-900 dark:text-white font-medium">{user?.username}</p>
                  </div>

                  <div className="p-4 bg-gray-50 dark:bg-gray-700/50 rounded-xl">
                    <div className="flex items-center gap-3 mb-2">
                      <AtSign className="w-5 h-5 text-primary-500" />
                      <span className="text-sm font-medium text-gray-500 dark:text-gray-400">Login</span>
                    </div>
                    <p className="text-gray-900 dark:text-white font-medium">{user?.login || 'Hidden'}</p>
                    <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                      Used only for sign in
                    </p>
                  </div>

                  {/* Theme Toggle */}
                  <button
                    onClick={toggleTheme}
                    className="w-full p-4 bg-gray-50 dark:bg-gray-700/50 rounded-xl flex items-center justify-between hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
                  >
                    <div className="flex items-center gap-3">
                      {theme === 'dark' ? (
                        <Moon className="w-5 h-5 text-primary-500" />
                      ) : (
                        <Sun className="w-5 h-5 text-primary-500" />
                      )}
                      <span className="font-medium text-gray-900 dark:text-white">
                        {theme === 'dark' ? 'Dark Mode' : 'Light Mode'}
                      </span>
                    </div>
                    <div className="relative w-11 h-6 bg-gray-300 dark:bg-primary-600 rounded-full">
                      <div
                        className={`absolute top-1 w-4 h-4 bg-white rounded-full transition-transform ${
                          theme === 'dark' ? 'translate-x-6' : 'translate-x-1'
                        }`}
                      />
                    </div>
                  </button>
                </div>
              </div>

              {/* Footer */}
              <div className="p-4 border-t border-gray-200 dark:border-gray-700">
                <button
                  onClick={logout}
                  className="w-full py-3 px-4 bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-300 font-medium rounded-xl hover:bg-red-200 dark:hover:bg-red-900/50 transition-colors flex items-center justify-center gap-2"
                >
                  <LogOut className="w-5 h-5" />
                  Sign Out
                </button>
              </div>
            </div>
          </motion.div>
        </>
      )}
    </AnimatePresence>
  );
}
