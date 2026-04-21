import { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Load user from localStorage on mount
  useEffect(() => {
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
      try {
        setCurrentUser(JSON.parse(savedUser));
      } catch (e) {
        console.error('Failed to parse saved user:', e);
        localStorage.removeItem('currentUser');
      }
    }
    setLoading(false);
  }, []);

  // Login: authenticate with backend
  const login = async (username, password) => {
    try {
      const response = await fetch(
        `/auth/login`,
        {
          method: 'POST',
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ username, password }),
        }
      );

      if (!response.ok) {
        if (response.status === 401) {
          throw new Error('Invalid username or password');
        }
        throw new Error('Login failed');
      }

      const user = await response.json();

      // Store user with role
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

  // Logout: clear user
  const logout = () => {
    setCurrentUser(null);
    localStorage.removeItem('currentUser');
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
