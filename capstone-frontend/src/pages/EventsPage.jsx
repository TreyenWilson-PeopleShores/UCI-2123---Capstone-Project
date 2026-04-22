import { useState, useEffect, useCallback, useReducer } from 'react';
import Cal from '../components/Cal';
import UpcomingEvents from '../components/UpcomingEvents';
import EventModal from '../components/EventModal';
import LoadingSpinner from '../components/LoadingSpinner';
import CalendarSkeleton from '../components/CalendarSkeleton';
import { getAllEventsByDateRange, getEventsByDateRange } from '../services/eventsService';

// Reducer for event loading state
const eventLoadingReducer = (state, action) => {
  switch (action.type) {
    case 'FETCH_START':
      return {
        ...state,
        loading: true,
        error: null
      };
    case 'FETCH_SUCCESS':
      return {
        ...state,
        loading: false,
        error: null,
        events: action.payload
      };
    case 'FETCH_ERROR':
      return {
        ...state,
        loading: false,
        error: action.payload
      };
    case 'UPDATE_EVENTS':
      return {
        ...state,
        events: action.payload
      };
    default:
      return state;
  }
};

// Initial state for event loading
const initialEventLoadingState = {
  events: [],
  loading: true,
  error: null
};

function EventsPage() {
  // State for event loading, error, and data using useReducer
  const [eventState, dispatch] = useReducer(eventLoadingReducer, initialEventLoadingState);
  // State to track current month (for fetching events by month)
  const [currentMonth, setCurrentMonth] = useState(new Date());
  // State for shared modal
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  // Function to get first and last day of a month
  const getMonthRange = (date) => {
    const year = date.getFullYear();
    const month = date.getMonth();
    
    // First day of the month
    const firstDay = new Date(year, month, 1);
    // Last day of the month
    const lastDay = new Date(year, month + 1, 0);
    
    // Format as YYYY-MM-DD
    const formatDate = (d) => {
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
    };
    
    return {
      start: formatDate(firstDay),
      end: formatDate(lastDay)
    };
  };

  // Function to fetch all events for a month with pagination
  const fetchEventsForMonth = useCallback(async (date) => {
    try {
      dispatch({ type: 'FETCH_START' });

      const { start, end } = getMonthRange(date);
      console.log(`Fetching events for ${start} to ${end}`);

      const { events: allEvents } = await getAllEventsByDateRange(start, end);
      console.log(`Total events for month: ${allEvents.length}`);
      dispatch({ type: 'FETCH_SUCCESS', payload: allEvents });
    } catch (err) {
      dispatch({ type: 'FETCH_ERROR', payload: err.message || 'Failed to fetch events' });
      console.error('Error fetching events:', err);
    }
  }, []);

  // Fetch events when currentMonth changes
  useEffect(() => {
    fetchEventsForMonth(currentMonth);
  }, [currentMonth, fetchEventsForMonth]);

  // Function to handle month change from calendar
  const handleMonthChange = useCallback((newDate) => {
    setCurrentMonth(newDate);
  }, []);

  // Function to handle status change from modal (admin status update)
  const handleStatusChange = useCallback((eventId, newStatus) => {
    // Update the events list with the new status
    const updatedEvents = eventState.events.map(event => 
      event.id === eventId ? { ...event, status: newStatus } : event
    );
    dispatch({ type: 'UPDATE_EVENTS', payload: updatedEvents });
    // Optionally, refetch all events to ensure consistency
    // fetchEventsForMonth(currentMonth);
  }, [eventState.events]);

  // Function to handle ticket purchase from modal
  const handleTicketPurchased = useCallback((eventId) => {
    console.log(`Ticket purchased for event ${eventId}, refreshing events...`);
    // Refresh events for the current month to get updated ticket counts
    fetchEventsForMonth(currentMonth);
  }, [currentMonth, fetchEventsForMonth]);

  // Function to handle event click from any component (Calendar or UpcomingEvents)
  const handleEventClick = useCallback((event) => {
    setSelectedEvent(event);
    setIsModalOpen(true);
  }, []);

  // Function to close modal
  const handleCloseModal = useCallback(() => {
    setIsModalOpen(false);
    setSelectedEvent(null);
  }, []);

  // Don't hide the entire page during loading - show loading state inline

  // Render error state
  if (eventState.error) {
    return (
      <div className="events-page">
        <h1>Events</h1>
        <div className="error">
          <p>Error: {eventState.error}</p>
          <p>Check if:</p>
          <ul>
            <li>Backend is running on http://localhost:8080</li>
            <li>CORS is configured on the backend</li>
            <li>The endpoint /api/events/date exists</li>
          </ul>
        </div>
        <button onClick={() => window.location.reload()}>Retry</button>
        <button onClick={() => {
          dispatch({ type: 'FETCH_START' });
          const { start, end } = getMonthRange(currentMonth);
          getEventsByDateRange(start, end, 0)
            .then((responseData) => {
              console.log('Manual fetch response:', responseData);
              const stringified = JSON.stringify(responseData);
              dispatch({ type: 'FETCH_ERROR', payload: `Response preview: ${stringified.substring(0, 200)}` });
            })
            .catch(err => {
              console.error('Manual fetch error:', err);
              dispatch({ type: 'FETCH_ERROR', payload: 'Manual fetch error: ' + err.message });
            });
        }}>Debug Response</button>
      </div>
    );
  }

  // Get current month name for display
  const currentMonthName = currentMonth.toLocaleString('default', { month: 'long' });
  const currentYear = currentMonth.getFullYear();

  // Render main content with events
  return (
    <div className="events-page">
      <h1>Events</h1>
      <p>
        Showing events for {currentMonthName} {currentYear}: {eventState.events.length} event{eventState.events.length !== 1 ? 's' : ''}
        {eventState.loading && (
          <span style={{ marginLeft: '8px' }}>
            <LoadingSpinner size="small" />
            <span style={{ marginLeft: '8px' }}>loading...</span>
          </span>
        )}
      </p>
      
      {/* Responsive Layout Container */}
      <div className="events-layout">
        {/* Upcoming Events Section - Sidebar on desktop, top on mobile */}
        <div className="upcoming-events-container">
          {eventState.loading ? (
            <div className="skeleton-card event-card-skeleton" style={{ marginBottom: '20px' }}>
              <div className="skeleton skeleton-image"></div>
              <div className="skeleton-content">
                <div className="skeleton skeleton-title"></div>
                <div className="skeleton skeleton-text"></div>
                <div className="skeleton skeleton-text short"></div>
                <div className="skeleton skeleton-button"></div>
              </div>
            </div>
          ) : (
            <UpcomingEvents 
              maxEvents={5} 
              onEventClick={handleEventClick}
              events={eventState.events}
            />
          )}
        </div>
        
        {/* Calendar Section - Main content area */}
        <div className="calendar-container">
          {eventState.loading ? (
            <CalendarSkeleton />
          ) : (
            <Cal 
              events={eventState.events} 
              loading={eventState.loading} 
              currentMonth={currentMonth} 
              onMonthChange={handleMonthChange}
              onStatusChange={handleStatusChange}
              onTicketPurchased={handleTicketPurchased}
              onEventClick={handleEventClick}
            />
          )}
        </div>
      </div>
      
      {/* Shared Event Modal */}
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

export default EventsPage;
