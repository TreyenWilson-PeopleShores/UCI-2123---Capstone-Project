import { createContext, useContext, useState, useEffect } from 'react';
import { login as loginWithApi } from '../services/authService';

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Load user from localStorage on mount
  useEffect(() => {
    const savedUser = localStorage.getItem('currentUser');
    const token = localStorage.getItem('accessToken');
    
    // If we have user but no token, clear user (need to re-login with JWT)
    if (savedUser && !token) {
      console.warn('User found but no JWT token. Clearing user data.');
      localStorage.removeItem('currentUser');
      setCurrentUser(null);
    } else if (savedUser && token) {
      try {
        setCurrentUser(JSON.parse(savedUser));
      } catch (e) {
        console.error('Failed to parse saved user:', e);
        localStorage.removeItem('currentUser');
        localStorage.removeItem('accessToken');
      }
    }
    setLoading(false);
  }, []);

  // Login: authenticate with backend
  const login = async (username, password) => {
    try {
      const authData = await loginWithApi(username, password);
      
      // Extract user info from response
      // The new JWT endpoint returns { accessToken, tokenType, user: { id, username, role } }
      const user = authData.user || authData; // Fallback to old format
      const userWithRole = {
        username: user.username,
        role: user.role || 'USER',
        id: user.id,
      };

      setCurrentUser(userWithRole);
      localStorage.setItem('currentUser', JSON.stringify(userWithRole));
      return true;
    } catch (error) {
      console.error('Login failed:', error);
      return false;
    }
  };

  // Logout: clear user and token
  const logout = () => {
    setCurrentUser(null);
    localStorage.removeItem('currentUser');
    localStorage.removeItem('accessToken');
  };

  const isAdmin = currentUser?.role === 'ADMIN';

  return (
    <AuthContext.Provider value={{ currentUser, login, logout, isAdmin, loading }}>
      {children}
    </AuthContext.Provider>
  );
}

// Hook to use auth context
export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
