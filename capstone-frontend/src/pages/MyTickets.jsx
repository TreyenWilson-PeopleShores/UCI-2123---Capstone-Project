import { useState, useEffect, useCallback } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import EventModal from '../components/EventModal';
import '../styles/MyTickets.css';

function MyTickets() {
  const navigate = useNavigate();
  const { currentUser, isAdmin } = useAuth();
  
  // State for user's purchased tickets
  const [ticketPurchases, setTicketPurchases] = useState([]);
  // State for events data (mapped from tickets)
  const [events, setEvents] = useState([]);
  // State for loading status
  const [loading, setLoading] = useState(true);
  // State for any errors
  const [error, setError] = useState(null);
  // State for modal
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  // State for tickets owned per event
  const [ticketsOwnedMap, setTicketsOwnedMap] = useState({});
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 20;

  // Check authentication and redirect if needed
  useEffect(() => {
    if (!currentUser) {
      navigate('/login');
      return;
    }
    
    if (isAdmin) {
      setError('Admins do not have a My Tickets page');
      setLoading(false);
      return;
    }
  }, [currentUser, isAdmin, navigate]);

  // Fetch user's purchased tickets
  const fetchUserTickets = useCallback(async () => {
    if (!currentUser || isAdmin) return;

    try {
      setLoading(true);
      setError(null);
      
      const response = await fetch(
        `/api/tickets-sold/user/${currentUser.id}?page=${page}&size=${pageSize}&sortBy=dateSold&ascending=false`,
        {
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
          },
        }
      );
      
      if (!response.ok) {
        throw new Error(`Failed to fetch user tickets: ${response.statusText}`);
      }
      
      const data = await response.json();
      const purchases = data.content || data;
      setTicketPurchases(purchases);
      setTotalPages(data.totalPages ?? 0);
      
      console.log(`Fetched ${purchases.length} ticket purchases for user ${currentUser.id}`);
      await processTicketPurchases(purchases);
      
    } catch (err) {
      setError(err.message || 'Failed to load your tickets');
      console.error('Error fetching user tickets:', err);
    } finally {
      setLoading(false);
    }
  }, [currentUser, isAdmin, page]);

  // Process ticket purchases to get event details and count tickets per event
  const processTicketPurchases = useCallback(async (purchases) => {
    if (!purchases || purchases.length === 0) {
      setEvents([]);
      setTicketsOwnedMap({});
      return;
    }

    try {
      const ticketCounts = {};
      purchases.forEach((purchase) => {
        const ticketId = purchase.ticketId || purchase.ticket_id;
        if (!ticketId) return;
        ticketCounts[ticketId] = (ticketCounts[ticketId] || 0) + 1;
      });

      const ticketCache = {};
      const ticketFetches = Object.keys(ticketCounts).map(async (ticketId) => {
        try {
          const ticketResponse = await fetch(`/api/tickets/id/${ticketId}`, {
            headers: {
              'Accept': 'application/json',
              'Content-Type': 'application/json',
            },
          });

          if (!ticketResponse.ok) {
            console.error(`Failed to fetch ticket ${ticketId}: ${ticketResponse.statusText}`);
            return;
          }

          const ticket = await ticketResponse.json();
          ticketCache[ticketId] = ticket;
        } catch (err) {
          console.error(`Error fetching ticket ${ticketId}:`, err);
        }
      });

      await Promise.all(ticketFetches);

      const eventCache = {};
      const venueCache = {};
      const eventIds = Array.from(
        new Set(
          Object.values(ticketCache)
            .map((ticket) => ticket && (ticket.eventId || ticket.event_id || ticket.event?.id))
            .filter(Boolean)
        )
      );

      const eventFetches = eventIds.map(async (eventId) => {
        try {
          const eventResponse = await fetch(`/api/events/id/${eventId}`, {
            headers: {
              'Accept': 'application/json',
              'Content-Type': 'application/json',
            },
          });

          if (!eventResponse.ok) {
            console.error(`Failed to fetch event ${eventId}: ${eventResponse.statusText}`);
            return;
          }

          const event = await eventResponse.json();
          eventCache[eventId] = event;
          console.log(`Event ${eventId} fetched:`, {
            id: event.id,
            name: event.event_name || event.name || event.title,
            venue: event.venue,
            venueKeys: event.venue && Object.keys(event.venue),
            venue_id: event.venue_id,
          });

          // If event has venue_id but no venue object, fetch venue details
          const venueId = event.venue_id || event.venueId;
          if (venueId && !event.venue) {
            try {
              const venueResponse = await fetch(`/api/venues/id/${venueId}`, {
                headers: {
                  'Accept': 'application/json',
                  'Content-Type': 'application/json',
                },
              });
              if (venueResponse.ok) {
                const venue = await venueResponse.json();
                venueCache[venueId] = venue;
                console.log(`Venue ${venueId} fetched:`, venue);
              }
            } catch (err) {
              console.error(`Error fetching venue ${venueId}:`, err);
            }
          }
        } catch (err) {
          console.error(`Error fetching event ${eventId}:`, err);
        }
      });

      await Promise.all(eventFetches);

      const eventGroups = {};

      purchases.forEach((purchase) => {
        const ticketId = purchase.ticketId || purchase.ticket_id;
        const ticket = ticketCache[ticketId];
        if (!ticket) return;

        const eventId = ticket.eventId || ticket.event_id || ticket.event?.id;
        if (!eventId) return;

        const ticketEvent = ticket.event;
        const cachedEvent = eventCache[eventId];
        let event = cachedEvent
          ? {
              ...cachedEvent,
              ...(ticketEvent && !cachedEvent.venue ? { venue: ticketEvent.venue } : {}),
            }
          : ticketEvent;

        if (!event) return;

        // If event still lacks venue but has venue_id, try to attach venue from venueCache
        if (!event.venue && (event.venue_id || event.venueId)) {
          const venueId = event.venue_id || event.venueId;
          const venue = venueCache[venueId];
          if (venue) {
            event = { ...event, venue };
          }
        }

        console.log(`Processing event ${eventId}:`, {
          eventId,
          hasVenue: !!event.venue,
          venueType: typeof event.venue,
          venueKeys: event.venue && Object.keys(event.venue),
          venueName: event.venue?.venue_name || event.venue?.name || event.venue?.title,
          venue_id: event.venue_id,
        });

        const purchaseDate = purchase.dateSold || purchase.date_sold || purchase.date || '';
        if (!eventGroups[eventId]) {
          eventGroups[eventId] = {
            event,
            ticketsOwned: 0,
            latestSold: purchaseDate || '',
          };
        }

        eventGroups[eventId].ticketsOwned += 1;

        if (purchaseDate) {
          const existing = eventGroups[eventId].latestSold;
          if (!existing || new Date(purchaseDate) > new Date(existing)) {
            eventGroups[eventId].latestSold = purchaseDate;
          }
        }
      });

      const sortedEvents = Object.values(eventGroups)
        .sort((a, b) => new Date(b.latestSold) - new Date(a.latestSold))
        .map((group) => group.event);

      const ticketsMap = {};
      Object.values(eventGroups).forEach((group) => {
        const eventId = group.event.id || group.event.eventId || group.event.event_id;
        if (eventId !== undefined) {
          ticketsMap[eventId] = group.ticketsOwned;
        }
      });

      setEvents(sortedEvents);
      setTicketsOwnedMap(ticketsMap);
      
      console.log(`Processed ${sortedEvents.length} events with tickets`);
    } catch (err) {
      console.error('Error processing ticket purchases:', err);
      setError('Failed to process ticket information');
    }
  }, []);

  // Fetch tickets when component mounts or user changes
  useEffect(() => {
    if (currentUser && !isAdmin) {
      fetchUserTickets();
    }
  }, [currentUser, isAdmin, fetchUserTickets]);

  // Handle event click to open modal
  const handleEventClick = useCallback((event) => {
    setSelectedEvent(event);
    setIsModalOpen(true);
  }, []);

  // Handle modal close
  const handleCloseModal = useCallback(() => {
    setIsModalOpen(false);
    setSelectedEvent(null);
  }, []);

  // Format date for display
  const formatDate = (dateValue) => {
    if (!dateValue) return 'Unknown date';
    try {
      const dateStr = String(dateValue);
      const dateMatch = dateStr.match(/^(\d{4})-(\d{2})-(\d{2})/);
      
      if (!dateMatch) {
        const date = new Date(dateValue);
        if (isNaN(date.getTime())) return 'Invalid date';
        
        return date.toLocaleDateString('en-US', {
          weekday: 'short',
          year: 'numeric',
          month: 'short',
          day: 'numeric'
        });
      }
      
      const year = parseInt(dateMatch[1], 10);
      const month = parseInt(dateMatch[2], 10) - 1;
      const day = parseInt(dateMatch[3], 10);
      
      const date = new Date(year, month, day);
      
      return date.toLocaleDateString('en-US', {
        weekday: 'short',
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    } catch {
      return 'Invalid date';
    }
  };

  // Safely get event name
  const getEventName = (event) => {
    return event.title || event.name || event.event_name || 'Event';
  };

  // Safely get venue name
  const getVenueName = (event) => {
    if (!event) return 'N/A';

    console.log('getVenueName called with event:', {
      id: event.id,
      venue: event.venue,
      venueKeys: event.venue && Object.keys(event.venue),
    });

    const topLevelVenue = event.venueName || event.venue_name || event.location || event.location_name;
    if (topLevelVenue) return topLevelVenue;

    const venue = event.venue;
    if (!venue) return 'N/A';
    if (typeof venue === 'string') return venue;
    if (typeof venue === 'object') {
      const name = venue.venue_name || venue.name || venue.title || venue.venueName || venue.location || venue.location_name;
      console.log('Extracted venue name:', name);
      return name || 'N/A';
    }

    return 'N/A';
  };

  // Render loading state
  if (loading) {
    return (
      <div className="my-tickets-page">
        <div className="my-tickets-header">
          <h1>My Tickets</h1>
          <Link to="/" className="back-to-events-btn">Back to Events</Link>
        </div>
        <div className="loading-container">
          <p>Loading your tickets...</p>
        </div>
      </div>
    );
  }

  // Render error state
  if (error) {
    return (
      <div className="my-tickets-page">
        <div className="my-tickets-header">
          <h1>My Tickets</h1>
          <Link to="/" className="back-to-events-btn">Back to Events</Link>
        </div>
        <div className="error-container">
          <p className="error-message">{error}</p>
          {!isAdmin && (
            <button 
              className="retry-btn"
              onClick={fetchUserTickets}
            >
              Try Again
            </button>
          )}
        </div>
      </div>
    );
  }

  // Render empty state
  if (events.length === 0) {
    return (
      <div className="my-tickets-page">
        <div className="my-tickets-header">
          <h1>My Tickets</h1>
          <Link to="/" className="back-to-events-btn">Back to Events</Link>
        </div>
        <div className="empty-state">
          <p>You haven't purchased any tickets yet.</p>
          <Link to="/" className="browse-events-btn">Browse Events</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="my-tickets-page">
      <div className="my-tickets-header">
        <h1>My Tickets</h1>
        <Link to="/" className="back-to-events-btn">Back to Events</Link>
      </div>
      
      <div className="tickets-summary">
        <p className="summary-text">
          Showing <strong>{events.length}</strong> event{events.length !== 1 ? 's' : ''} on this page.
        </p>
      </div>
      
      <div className="pagination-controls">
        <button
          type="button"
          className="pagination-button"
          onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
          disabled={page <= 0}
        >
          Previous
        </button>
        <span className="pagination-info">
          Page {page + 1} of {totalPages || 1}
        </span>
        <button
          type="button"
          className="pagination-button"
          onClick={() => setPage((prev) => Math.min(prev + 1, Math.max(totalPages - 1, 0)))}
          disabled={totalPages <= 1 || page >= totalPages - 1}
        >
          Next
        </button>
      </div>

      <div className="tickets-list">
        {events.map(event => (
          <div 
            key={event.id} 
            className="ticket-card"
            onClick={() => handleEventClick(event)}
            role="button"
            tabIndex={0}
            onKeyDown={(e) => {
              if (e.key === 'Enter' || e.key === ' ') {
                handleEventClick(event);
              }
            }}
            aria-label={`View details for ${getEventName(event)}`}
          >
            <div className="ticket-card-header">
              <h3 className="event-name">{getEventName(event)}</h3>
              <div className="tickets-owned-badge">
                Tickets Owned: <span className="ticket-count">{ticketsOwnedMap[event.id] || 0}</span>
              </div>
            </div>
            
            <div className="ticket-card-details">
              <div className="detail-row">
                <span className="detail-label">Date:</span>
                <span className="detail-value">{formatDate(event.date)}</span>
              </div>
              
              <div className="detail-row">
                <span className="detail-label">Venue:</span>
                <span className="detail-value">{getVenueName(event)}</span>
              </div>
            </div>
            
            <div className="ticket-card-footer">
              <span className="view-details-text">Click to view event details</span>
            </div>
          </div>
        ))}
      </div>
      
      {/* Shared Event Modal */}
      <EventModal
        event={selectedEvent}
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        ticketsOwned={selectedEvent ? ticketsOwnedMap[selectedEvent.id] : undefined}
      />
    </div>
  );
}

export default MyTickets;