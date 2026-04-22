import { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import StatusBadge from './StatusBadge';
import { getAllEventsByDateRange } from '../services/eventsService';
import '../styles/EventList.css';

/**
 * UpcomingEvents Component
 * 
 * A reusable component that displays upcoming events filtered by:
 * - status === "SCHEDULED"
 * - event.date > today
 * 
 * Features:
 * - Can use provided events prop or fetch its own data
 * - Client-side filtering and sorting
 * - Displays 3-5 upcoming events
 * - Generic styling for reuse with other event lists
 * - Clickable events that trigger modal
 */
const UpcomingEvents = ({ maxEvents = 5, onEventClick, events: providedEvents }) => {
  const [internalEvents, setInternalEvents] = useState([]);
  const [filteredEvents, setFilteredEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Determine if we should use provided events or fetch our own
  const shouldFetch = providedEvents === undefined;
  const events = shouldFetch ? internalEvents : providedEvents;

  // Fetch all events from the API (only if no events provided)
  useEffect(() => {
    if (!shouldFetch) {
      setLoading(false);
      return;
    }

    const fetchAllEvents = async () => {
      try {
        setLoading(true);
        setError(null);

        const today = new Date();
        const sixMonthsFromNow = new Date();
        sixMonthsFromNow.setMonth(today.getMonth() + 6);

        const formatDate = (date) => {
          return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
        };

        const startDate = formatDate(today);
        const endDate = formatDate(sixMonthsFromNow);
        const { events: allEvents } = await getAllEventsByDateRange(startDate, endDate);

        setInternalEvents(allEvents);
      } catch (err) {
        setError(err.message || 'Failed to fetch events');
        console.error('Error fetching events:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchAllEvents();
  }, [shouldFetch]);

  // Helper function to parse date without timezone issues
  const parseDate = (dateValue) => {
    if (!dateValue) return null;
    
    try {
      // Parse date string manually to avoid timezone issues
      // Expected format: YYYY-MM-DD or YYYY-MM-DDTHH:mm:ss
      const dateStr = String(dateValue);
      const dateMatch = dateStr.match(/^(\d{4})-(\d{2})-(\d{2})/);
      
      if (!dateMatch) {
        // Fallback to Date object if format doesn't match
        const date = new Date(dateValue);
        return isNaN(date.getTime()) ? null : date;
      }
      
      // Extract year, month, day from the string
      const year = parseInt(dateMatch[1], 10);
      const month = parseInt(dateMatch[2], 10) - 1; // Convert to 0-indexed
      const day = parseInt(dateMatch[3], 10);
      
      // Create date object with local timezone (midnight)
      return new Date(year, month, day);
    } catch {
      return null;
    }
  };

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
        const eventDate = parseDate(event.date);
        if (!eventDate) return false;
        
        return eventDate > today;
      })
      .sort((a, b) => {
        // Sort by date ascending (soonest first)
        const dateA = parseDate(a.date);
        const dateB = parseDate(b.date);
        
        if (!dateA || !dateB) return 0;
        return dateA - dateB;
      })
      .slice(0, maxEvents); // Limit to maxEvents

    setFilteredEvents(upcoming);
  }, [events, maxEvents]);

  // Format date to readable string with error handling
  const formatEventDate = (dateValue) => {
    const date = parseDate(dateValue);
    if (!date) return 'Unknown date';
    
    return date.toLocaleDateString('en-US', {
      weekday: 'short',
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    });
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
          <button
            key={event.id}
            type="button"
            className="event-item"
            onClick={() => onEventClick && onEventClick(event)}
            aria-label={(event.name || 'Upcoming event') + ' on ' + formatEventDate(event.date) + (event.venue ? ' at ' + getVenueName(event.venue) : '')}
          >
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
              <StatusBadge status={event.status} size="sm" />
            </div>
          </button>
        ))}
      </div>
    </div>
  );
};

UpcomingEvents.propTypes = {
  maxEvents: PropTypes.number,
  onEventClick: PropTypes.func,
  events: PropTypes.array
};

UpcomingEvents.defaultProps = {
  maxEvents: 5
};

export default UpcomingEvents;