import { useState, useEffect, useCallback, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import Cal from '../components/Cal';
import EventModal from '../components/EventModal';
import { useAuth } from '../contexts/AuthContext';
import '../styles/AdminManager.css';

function AdminManager() {
  const { currentUser, isAdmin, loading } = useAuth();
  const navigate = useNavigate();
  const [events, setEvents] = useState([]);
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loadingEvents, setLoadingEvents] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!loading && (!currentUser || !isAdmin)) {
      navigate('/', { replace: true });
    }
  }, [currentUser, isAdmin, loading, navigate]);

  const getMonthRange = useCallback((date) => {
    const year = date.getFullYear();
    const month = date.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);

    const formatDate = (d) =>
      `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;

    return {
      start: formatDate(firstDay),
      end: formatDate(lastDay),
    };
  }, []);

  const fetchEventsForMonth = useCallback(async (date) => {
    setLoadingEvents(true);
    setError(null);

    try {
      const { start, end } = getMonthRange(date);
      let allEvents = [];
      let page = 0;
      let hasMorePages = true;

      while (hasMorePages) {
        const response = await fetch(`/api/events/date?start=${start}&end=${end}&page=${page}`, {
          headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
          },
        });

        if (!response.ok) {
          throw new Error(`Failed to fetch events: ${response.statusText}`);
        }

        const responseData = await response.json();
        const pageEvents = Array.isArray(responseData) ? responseData : responseData.content || [];
        allEvents = [...allEvents, ...pageEvents];

        hasMorePages = !responseData.last && page < (responseData.totalPages || 0) - 1;
        page += 1;
      }

      setEvents(allEvents);
    } catch (err) {
      setError(err.message || 'Unable to load events');
      console.error('AdminManager fetch error:', err);
    } finally {
      setLoadingEvents(false);
    }
  }, [getMonthRange]);

  useEffect(() => {
    if (isAdmin) {
      fetchEventsForMonth(currentMonth);
    }
  }, [currentMonth, fetchEventsForMonth, isAdmin]);

  const handleMonthChange = useCallback((newDate) => {
    setCurrentMonth(newDate);
  }, []);

  const handleEventClick = useCallback((event) => {
    setSelectedEvent(event);
    setIsModalOpen(true);
  }, []);

  const handleCloseModal = useCallback(() => {
    setIsModalOpen(false);
    setSelectedEvent(null);
  }, []);

  const handleStatusChange = useCallback((eventId, newStatus) => {
    setEvents((prev) => prev.map((event) =>
      event.id === eventId ? { ...event, status: newStatus } : event
    ));
  }, []);

  const handleTicketPurchased = useCallback((eventId) => {
    fetchEventsForMonth(currentMonth);
  }, [currentMonth, fetchEventsForMonth]);

  const soldOutEvents = useMemo(() => {
    return events.filter((event) => {
      const tickets = event?.tickets;
      return tickets && typeof tickets.sold === 'number' && typeof tickets.total === 'number' && tickets.sold === tickets.total;
    });
  }, [events]);

  const formatEventDate = (dateValue) => {
    if (!dateValue) return 'Unknown date';
    try {
      const parsed = new Date(dateValue);
      if (Number.isNaN(parsed.getTime())) return 'Unknown date';
      return parsed.toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric',
      });
    } catch {
      return 'Unknown date';
    }
  };

  const getVenueLabel = (event) => {
    return event?.venue?.name || event?.venue_name || event?.location || 'Venue unknown';
  };

  return (
    <div className="admin-manager-page">
      <button
        className="back-to-events-btn"
        onClick={() => navigate('/')}
        type="button"
        aria-label="Back to events"
      >
        ← Back to Events
      </button>

      <header className="admin-manager-header">
        <div>
          <h1>Admin Manager</h1>
          <p>Monitor event scheduling, status, and capacity from a single oversight view.</p>
        </div>
      </header>

      <div className="admin-manager-content">
        <section className="admin-manager-calendar-panel">
          <Cal
            events={events}
            loading={loadingEvents}
            currentMonth={currentMonth}
            onMonthChange={handleMonthChange}
            onStatusChange={handleStatusChange}
            onTicketPurchased={handleTicketPurchased}
            onEventClick={handleEventClick}
          />
        </section>

        <aside className="sold-out-panel">
          <div className="sold-out-card">
            <div className="sold-out-card-header">
              <h2>Sold Out Events</h2>
              <p className="sold-out-subtitle">Events with full capacity in the selected month.</p>
            </div>

            {loadingEvents ? (
              <p className="admin-info-text">Loading sold out events...</p>
            ) : error ? (
              <p className="admin-error-text">{error}</p>
            ) : soldOutEvents.length === 0 ? (
              <p className="admin-info-text">No sold out events in this month.</p>
            ) : (
              <ul className="sold-out-list">
                {soldOutEvents.map((event) => (
                  <li key={event.id}>
                    <button
                      type="button"
                      className="sold-out-item"
                      onClick={() => handleEventClick(event)}
                    >
                      <span className="sold-out-title">{event.title || event.name || 'Unnamed event'}</span>
                      <span className="sold-out-meta">{formatEventDate(event.date)} · {getVenueLabel(event)}</span>
                    </button>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </aside>
      </div>

      <EventModal
        event={selectedEvent}
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        onStatusChange={handleStatusChange}
        onTicketPurchased={handleTicketPurchased}
      />
    </div>
  );
}

export default AdminManager;
