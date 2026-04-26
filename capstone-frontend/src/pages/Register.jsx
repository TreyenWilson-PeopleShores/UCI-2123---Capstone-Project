import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { registerUser } from '../services/usersService';

const Register = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    setError('');
    setSuccessMessage('');
    setLoading(true);

    // Basic validation
    if (!username.trim()) {
      setError('Please enter a username');
      setLoading(false);
      return;
    }

    if (!password.trim()) {
      setError('Please enter a password');
      setLoading(false);
      return;
    }

    try {
      await registerUser({
        username: username.trim(),
        password: password.trim(),
        role: 'USER',
      });

      setSuccessMessage('Registration successful! Redirecting to login...');
      // Redirect to login after 2 seconds
      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } catch (err) {
      const errorMessage = err.message || 'Network error. Please check your connection and try again.';
      if (errorMessage.includes('400')) {
        setError('Username may already exist or validation failed');
      } else {
        setError(errorMessage);
      }
      console.error('Registration error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleBackToEvents = () => {
    navigate('/');
  };

  return (
    <div className="login-page">
      <div className="login-container">
        <button 
          className="back-to-events-btn" 
          onClick={handleBackToEvents}
          type="button"
          aria-label="Back to events"
        >
          ← Back to Events
        </button>

        <h2>Register</h2>
        <form className="login-form" onSubmit={handleRegister}>
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              type="text"
              id="username"
              name="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              disabled={loading}
              placeholder="Choose a username"
            />
          </div>
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              name="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              disabled={loading}
              placeholder="Choose a password"
            />
          </div>
          {error && <p className="error-message">{error}</p>}
          {successMessage && <p className="success-message">{successMessage}</p>}
          <button 
            type="submit" 
            className="login-btn"
            disabled={loading}
          >
            {loading ? 'Registering...' : 'Register'}
          </button>
        </form>
        
        <p className="register-text">
          Already have an account? <Link to="/login">Login here</Link>
        </p>
      </div>
    </div>
  );
};

export default Register;