import { AuthProvider } from './context/AuthContext';
import { ThemeProvider } from './context/ThemeContext';
import { WebSocketProvider } from './context/WebSocketContext';
import { Auth } from './pages/Auth';
import { Messenger } from './pages/Messenger';
import { useAuth } from './context/AuthContext';

function AppContent() {
  const { isAuthenticated } = useAuth();
  
  return isAuthenticated ? <Messenger /> : <Auth />;
}

function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <WebSocketProvider>
          <AppContent />
        </WebSocketProvider>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;
