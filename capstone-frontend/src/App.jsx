import { Routes, Route } from 'react-router-dom';
import EventsPage from './pages/EventsPage';
import Login from './pages/Login';
import Register from './pages/Register';
import MyTickets from './pages/MyTickets';
import AdminManager from './pages/AdminManager';
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
            <Route path="/register" element={<Register />} />
            <Route path="/my-tickets" element={<MyTickets />} />
            <Route path="/admin-manager" element={<AdminManager />} />
          </Routes>
        </main>
      </div>
    </AuthProvider>
  );
}

export default App;