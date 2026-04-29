import { useState } from 'react';
import { X, Search, User, Users } from 'lucide-react';
import { useWebSocket } from '@/context/WebSocketContext';
import { motion } from 'framer-motion';

interface CreateChatModalProps {
  onClose: () => void;
}

export function CreateChatModal({ onClose }: CreateChatModalProps) {
  const [mode, setMode] = useState<'private' | 'group'>('private');
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<{ id: number; username: string }[]>([]);
  const [selectedUsers, setSelectedUsers] = useState<{ id: number; username: string }[]>([]);
  const [groupName, setGroupName] = useState('');
  const [groupDescription, setGroupDescription] = useState('');
  const [isSearching, setIsSearching] = useState(false);
  const [isCreating, setIsCreating] = useState(false);
  const { searchUsers, createPrivateChat, createGroupChat } = useWebSocket();

  const handleSearch = async () => {
    if (!searchQuery.trim()) return;
    setIsSearching(true);
    try {
      const results = await searchUsers(searchQuery);
      setSearchResults(results.filter(r => !selectedUsers.find(s => s.id === r.id)));
    } finally {
      setIsSearching(false);
    }
  };

  const handleSelectUser = (user: { id: number; username: string }) => {
    if (mode === 'private') {
      setSelectedUsers([user]);
    } else {
      setSelectedUsers([...selectedUsers, user]);
      setSearchResults(searchResults.filter(r => r.id !== user.id));
    }
  };

  const handleRemoveUser = (userId: number) => {
    setSelectedUsers(selectedUsers.filter(u => u.id !== userId));
  };

  const handleCreate = async () => {
    if (selectedUsers.length === 0) return;
    
    setIsCreating(true);
    try {
      if (mode === 'private') {
        await createPrivateChat(selectedUsers[0].id);
      } else {
        await createGroupChat(
          selectedUsers.map(u => u.id),
          groupName || 'New Group',
          groupDescription,
          null
        );
      }
      onClose();
    } catch (error) {
      console.error('Failed to create chat:', error);
    } finally {
      setIsCreating(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        onClick={onClose}
        className="absolute inset-0 bg-black/50"
      />
      
      <motion.div
        initial={{ opacity: 0, scale: 0.95, y: 20 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        className="relative bg-white dark:bg-gray-800 rounded-2xl shadow-2xl w-full max-w-md max-h-[80vh] flex flex-col"
      >
        {/* Header */}
        <div className="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white">New Chat</h2>
          <button
            onClick={onClose}
            className="p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
          >
            <X className="w-5 h-5 text-gray-700 dark:text-gray-300" />
          </button>
        </div>

        {/* Mode Toggle */}
        <div className="p-4 border-b border-gray-200 dark:border-gray-700">
          <div className="flex p-1 bg-gray-100 dark:bg-gray-700 rounded-xl">
            <button
              onClick={() => {
                setMode('private');
                setSelectedUsers([]);
              }}
              className={`flex-1 py-2 px-4 rounded-lg text-sm font-medium transition-all ${
                mode === 'private'
                  ? 'bg-white dark:bg-gray-600 text-primary-600 dark:text-primary-400 shadow-sm'
                  : 'text-gray-600 dark:text-gray-400'
              }`}
            >
              <div className="flex items-center justify-center gap-2">
                <User className="w-4 h-4" />
                Private
              </div>
            </button>
            <button
              onClick={() => {
                setMode('group');
                setSelectedUsers([]);
              }}
              className={`flex-1 py-2 px-4 rounded-lg text-sm font-medium transition-all ${
                mode === 'group'
                  ? 'bg-white dark:bg-gray-600 text-primary-600 dark:text-primary-400 shadow-sm'
                  : 'text-gray-600 dark:text-gray-400'
              }`}
            >
              <div className="flex items-center justify-center gap-2">
                <Users className="w-4 h-4" />
                Group
              </div>
            </button>
          </div>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto p-4">
          {/* Group Fields */}
          {mode === 'group' && (
            <div className="space-y-3 mb-4">
              <input
                type="text"
                value={groupName}
                onChange={(e) => setGroupName(e.target.value)}
                placeholder="Group name (optional)"
                className="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
              <input
                type="text"
                value={groupDescription}
                onChange={(e) => setGroupDescription(e.target.value)}
                placeholder="Description (optional)"
                className="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
            </div>
          )}

          {/* Search */}
          <div className="relative mb-4">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
              placeholder="Search users..."
              className="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
            <button
              onClick={handleSearch}
              disabled={isSearching || !searchQuery.trim()}
              className="absolute right-2 top-1/2 -translate-y-1/2 px-3 py-1 bg-primary-600 text-white text-sm rounded-md disabled:opacity-50"
            >
              {isSearching ? '...' : 'Search'}
            </button>
          </div>

          {/* Selected Users */}
          {selectedUsers.length > 0 && (
            <div className="mb-4">
              <p className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Selected ({selectedUsers.length})
              </p>
              <div className="flex flex-wrap gap-2">
                {selectedUsers.map(user => (
                  <div
                    key={user.id}
                    className="flex items-center gap-1 px-3 py-1 bg-primary-100 dark:bg-primary-900/30 text-primary-700 dark:text-primary-300 rounded-full text-sm"
                  >
                    {user.username}
                    <button
                      onClick={() => handleRemoveUser(user.id)}
                      className="ml-1 hover:text-primary-900"
                    >
                      ×
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Search Results */}
          {searchResults.length > 0 && (
            <div className="space-y-1">
              <p className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Results</p>
              {searchResults.map(user => (
                <button
                  key={user.id}
                  onClick={() => handleSelectUser(user)}
                  className="w-full flex items-center gap-3 p-3 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors text-left"
                >
                  <div className="w-10 h-10 rounded-full bg-gradient-to-br from-primary-500 to-primary-600 flex items-center justify-center text-white font-medium">
                    {user.username.charAt(0).toUpperCase()}
                  </div>
                  <span className="font-medium text-gray-900 dark:text-white">{user.username}</span>
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="p-4 border-t border-gray-200 dark:border-gray-700">
          <button
            onClick={handleCreate}
            disabled={selectedUsers.length === 0 || isCreating}
            className="w-full py-3 px-4 bg-primary-600 hover:bg-primary-700 text-white font-semibold rounded-xl disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {isCreating
              ? 'Creating...'
              : mode === 'private'
              ? 'Start Chat'
              : 'Create Group'}
          </button>
        </div>
      </motion.div>
    </div>
  );
}
