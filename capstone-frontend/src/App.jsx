import { Routes, Route } from 'react-router-dom';
import EventsPage from './pages/EventsPage';
import Login from './pages/Login';
import LoginIcon from './components/LoginIcon';
import { AuthProvider } from './contexts/AuthContext';

function App() {
  return (
    <AuthProvider>
      <div className="app">
        <header>
          
          <h1>Event Booking Application</h1>
          <LoginIcon />
        </header>
        <main>
          <Routes>
            
            <Route path="/" element={<EventsPage />} />
            <Route path="/login" element={<Login />} />
          </Routes>
        </main>
      </div>
    </AuthProvider>
  );
}

export default App;