import { useState, useEffect, useCallback } from 'react';
import Cal from '../components/Cal';
import UpcomingEvents from '../components/UpcomingEvents';
import EventModal from '../components/EventModal';

function EventsPage() {
  // State to store the events data for current month
  const [events, setEvents] = useState([]);
  // State to track loading status
  const [loading, setLoading] = useState(true);
  // State to track any errors
  const [error, setError] = useState(null);
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
      setLoading(true);
      setError(null);
      
      const { start, end } = getMonthRange(date);
      console.log(`Fetching events for ${start} to ${end}`);
      
      let allEvents = [];
      let page = 0;
      let hasMorePages = true;
      
      // Fetch all pages for the month
      while (hasMorePages) {
        const url = `/api/events/date?start=${start}&end=${end}&page=${page}`;
        console.log(`Fetching page ${page}: ${url}`);
        
        const response = await fetch(url, {
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
          },
        });
        
        if (!response.ok) {
          const errorText = await response.text();
          console.error('Error response:', errorText);
          throw new Error(`HTTP error! status: ${response.status} - ${response.statusText}`);
        }
        
        const responseData = await response.json();
        console.log(`Page ${page} response:`, responseData);
        
        // Add events from this page
        if (responseData.content && Array.isArray(responseData.content)) {
          allEvents = [...allEvents, ...responseData.content];
        }
        
        // Check if there are more pages
        hasMorePages = !responseData.last && page < responseData.totalPages - 1;
        page++;
      }
      
      console.log(`Total events for month: ${allEvents.length}`);
      setEvents(allEvents);
    } catch (err) {
      setError(err.message || 'Failed to fetch events');
      console.error('Error fetching events:', err);
    } finally {
      setLoading(false);
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
    setEvents(prevEvents => 
      prevEvents.map(event => 
        event.id === eventId ? { ...event, status: newStatus } : event
      )
    );
    // Optionally, refetch all events to ensure consistency
    // fetchEventsForMonth(currentMonth);
  }, []);

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
  if (error) {
    return (
      <div className="events-page">
        <h1>Events</h1>
        <div className="error">
          <p>Error: {error}</p>
          <p>Check if:</p>
          <ul>
            <li>Backend is running on http://localhost:8080</li>
            <li>CORS is configured on the backend</li>
            <li>The endpoint /api/events/date exists</li>
          </ul>
        </div>
        <button onClick={() => window.location.reload()}>Retry</button>
        <button onClick={() => {
          setLoading(true);
          setError(null);
          // Re-fetch events for current month
          const { start, end } = getMonthRange(currentMonth);
          fetch(`/api/events/date?start=${start}&end=${end}&page=0`)
            .then(res => {
              console.log('Manual fetch status:', res.status);
              return res.text();
            })
            .then(text => {
              console.log('Manual fetch response (first 500 chars):', text.substring(0, 500));
              setError('Response preview: ' + text.substring(0, 200));
              setLoading(false);
            })
            .catch(err => {
              console.error('Manual fetch error:', err);
              setError('Manual fetch error: ' + err.message);
              setLoading(false);
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
        Showing events for {currentMonthName} {currentYear}: {events.length} event{events.length !== 1 ? 's' : ''}
        {loading && ' (loading...)'}
      </p>
      
      {/* Responsive Layout Container */}
      <div className="events-layout">
        {/* Upcoming Events Section - Sidebar on desktop, top on mobile */}
        <div className="upcoming-events-container">
          <UpcomingEvents 
            maxEvents={5} 
            onEventClick={handleEventClick}
            events={events}
          />
        </div>
        
        {/* Calendar Section - Main content area */}
        <div className="calendar-container">
          <Cal 
            events={events} 
            loading={loading} 
            currentMonth={currentMonth} 
            onMonthChange={handleMonthChange}
            onStatusChange={handleStatusChange}
            onTicketPurchased={handleTicketPurchased}
            onEventClick={handleEventClick}
          />
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
