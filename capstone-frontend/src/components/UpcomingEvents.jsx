import { useState, useEffect } from 'react';
import '../styles/EventList.css';

/**
 * UpcomingEvents Component
 * 
 * A reusable component that displays upcoming events filtered by:
 * - status === "SCHEDULED"
 * - event.date > today
 * 
 * Features:
 * - Fetches events from existing /api/events endpoint
 * - Client-side filtering and sorting
 * - Displays 3-5 upcoming events
 * - Generic styling for reuse with other event lists
 */
const UpcomingEvents = ({ maxEvents = 5 }) => {
  const [events, setEvents] = useState([]);
  const [filteredEvents, setFilteredEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Fetch all events from the API
  useEffect(() => {
    const fetchAllEvents = async () => {
      try {
        setLoading(true);
        setError(null);
        
        // Since the existing API uses pagination, we need to fetch all pages
        let allEvents = [];
        let page = 0;
        let hasMorePages = true;
        
        // Get date range for the next 6 months to ensure we get enough events
        const today = new Date();
        const sixMonthsFromNow = new Date();
        sixMonthsFromNow.setMonth(today.getMonth() + 6);
        
        const formatDate = (date) => {
          return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
        };
        
        const startDate = formatDate(today);
        const endDate = formatDate(sixMonthsFromNow);
        
        while (hasMorePages) {
          const url = `/api/events/date?start=${startDate}&end=${endDate}&page=${page}`;
          
          const response = await fetch(url, {
            headers: {
              'Accept': 'application/json',
              'Content-Type': 'application/json',
            },
          });
          
          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
          }
          
          const responseData = await response.json();
          
          if (responseData.content && Array.isArray(responseData.content)) {
            allEvents = [...allEvents, ...responseData.content];
          }
          
          // Check if there are more pages
          hasMorePages = !responseData.last && page < responseData.totalPages - 1;
          page++;
        }
        
        setEvents(allEvents);
      } catch (err) {
        setError(err.message || 'Failed to fetch events');
        console.error('Error fetching events:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchAllEvents();
  }, []);

  // Filter and sort events
  useEffect(() => {
    if (events.length === 0) {
      setFilteredEvents([]);
      return;
    }

    const today = new Date();
    today.setHours(0, 0, 0, 0); // Start of today for accurate comparison

    const upcoming = events
      .filter(event => {
        // Filter for SCHEDULED events
        if (event.status !== 'SCHEDULED') return false;
        
        // Filter for future dates
        const eventDate = new Date(event.date);
        return eventDate > today;
      })
      .sort((a, b) => {
        // Sort by date ascending (soonest first)
        return new Date(a.date) - new Date(b.date);
      })
      .slice(0, maxEvents); // Limit to maxEvents

    setFilteredEvents(upcoming);
  }, [events, maxEvents]);

  // Format date to readable string with error handling
  const formatEventDate = (dateString) => {
    try {
      const date = new Date(dateString);
      if (isNaN(date.getTime())) {
        return 'Invalid date';
      }
      return date.toLocaleDateString('en-US', {
        weekday: 'short',
        month: 'short',
        day: 'numeric',
        year: 'numeric'
      });
    } catch (error) {
      console.error('Error formatting date:', error);
      return 'Date error';
    }
  };

  // Safely extract venue name from venue object
  const getVenueName = (venue) => {
    if (!venue) return '';
    
    // If venue is a string, return it
    if (typeof venue === 'string') return venue;
    
    // If venue is an object, try different property names
    if (typeof venue === 'object') {
      return venue.venue_name || venue.name || venue.title || '';
    }
    
    return '';
  };

  if (loading) {
    return (
      <div className="event-list-container">
        <h2 className="event-list-title">Upcoming Events</h2>
        <div className="event-loading">
          <p>Loading upcoming events...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="event-list-container">
        <h2 className="event-list-title">Upcoming Events</h2>
        <div className="event-error">
          <p>Error loading events: {error}</p>
        </div>
      </div>
    );
  }

  if (filteredEvents.length === 0) {
    return (
      <div className="event-list-container">
        <h2 className="event-list-title">Upcoming Events</h2>
        <div className="event-empty-state">
          <p>No upcoming events scheduled</p>
        </div>
      </div>
    );
  }

  return (
    <div className="event-list-container">
      <h2 className="event-list-title">Upcoming Events</h2>
      <div className="event-list">
        {filteredEvents.map(event => (
          <div key={event.id} className="event-item">
            <div className="event-item-header">
              <h3 className="event-name">{event.name}</h3>
              <span className="event-date">{formatEventDate(event.date)}</span>
            </div>
            <div className="event-details">
              {event.venue && (
                <div className="event-venue">
                  {getVenueName(event.venue)}
                </div>
              )}
              <span className="event-status scheduled">
                {event.status}
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default UpcomingEvents;