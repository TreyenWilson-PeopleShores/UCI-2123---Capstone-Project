import { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const LoginIcon = () => {
  const navigate = useNavigate();
  const { currentUser, logout, isAdmin } = useAuth();
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setIsDropdownOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleProfileClick = () => {
    setIsDropdownOpen(!isDropdownOpen);
  };

  const handleLogout = () => {
    logout();
    setIsDropdownOpen(false);
    navigate('/');
  };

  const handleLogin = () => {
    setIsDropdownOpen(false);
    navigate('/login');
  };

  const handleMyTickets = () => {
    setIsDropdownOpen(false);
    navigate('/my-tickets');
  };

  const displayLetter = currentUser ? currentUser.username.charAt(0).toUpperCase() : 'L';

  return (
    <div className="login-icon-container" ref={dropdownRef}>
      <button
        type="button"
        className="login-icon"
        onClick={handleProfileClick}
        aria-haspopup="menu"
        aria-label={currentUser ? `Profile: ${currentUser.username}` : 'Login'}
        aria-expanded={isDropdownOpen}
      >
        {displayLetter}
      </button>

      {isDropdownOpen && (
        <div className="profile-dropdown" role="menu">
          {currentUser ? (
            <>
              {/* Show Admin Manager link only for admin users */}
              {isAdmin && (
                <button
                  className="dropdown-item admin-manager-btn"
                  onClick={() => {
                    setIsDropdownOpen(false);
                    navigate('/admin-manager');
                  }}
                  role="menuitem"
                >
                  Admin Manager
                </button>
              )}
              {/* Show My Tickets link only for non-admin users */}
              {!isAdmin && (
                <button 
                  className="dropdown-item my-tickets-btn"
                  onClick={handleMyTickets}
                  role="menuitem"
                >
                  My Tickets
                </button>
              )}
              <button 
                className="dropdown-item logout-btn"
                onClick={handleLogout}
                role="menuitem"
              >
                Logout
              </button>
            </>
          ) : (
            <button 
              className="dropdown-item login-btn"
              onClick={handleLogin}
              role="menuitem"
            >
              Login
            </button>
          )}
        </div>
      )}
    </div>
  );
};

export default LoginIcon;