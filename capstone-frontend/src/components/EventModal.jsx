import { useEffect, useRef, useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import StatusBadge from './StatusBadge';

function EventModal({ event, isOpen, onClose, onStatusChange, onTicketPurchased }) {
  const { currentUser, isAdmin } = useAuth();
  const modalRef = useRef(null);
  const closeButtonRef = useRef(null);
  const [selectedStatus, setSelectedStatus] = useState(event?.status || 'SCHEDULED');
  const [isUpdating, setIsUpdating] = useState(false);
  const [isPurchasing, setIsPurchasing] = useState(false);
  const [purchaseMessage, setPurchaseMessage] = useState('');
  const [purchaseError, setPurchaseError] = useState('');
  const [ticketPrice, setTicketPrice] = useState(null);
  const [ticketLoading, setTicketLoading] = useState(false);
  const [ticketError, setTicketError] = useState('');

  // Update selectedStatus when event changes
  useEffect(() => {
    if (event) {
      setSelectedStatus(event.status);
    }
  }, [event]);

  // Fetch ticket price when event changes
  useEffect(() => {
    const fetchTicketPrice = async () => {
      if (!event || !event.id) {
        setTicketPrice(null);
        return;
      }

      setTicketLoading(true);
      setTicketError('');
      
      try {
        // Fetch ticket for the event
        const response = await fetch(
          `/api/tickets/event/${event.id}?page=0&size=1&sortBy=id&ascending=true`,
          {
            headers: {
              'Accept': 'application/json',
              'Content-Type': 'application/json',
            },
          }
        );
        
        if (!response.ok) {
          throw new Error(`Failed to fetch ticket: ${response.statusText}`);
        }
        
        const ticketData = await response.json();
        // Handle both response formats: content array or direct array
        const tickets = Array.isArray(ticketData) ? ticketData : (ticketData.content || []);
        
        if (tickets.length === 0) {
          setTicketPrice(null);
          setTicketError('No ticket information available');
        } else {
          const ticket = tickets[0];
          setTicketPrice(ticket.price);
        }
      } catch (error) {
        console.error('Error fetching ticket price:', error);
        setTicketError('Failed to load ticket price');
        setTicketPrice(null);
      } finally {
        setTicketLoading(false);
      }
    };

    fetchTicketPrice();
  }, [event]);

  // Handle Esc key press and focus management
  useEffect(() => {
    if (!isOpen) return;

    const dialogElement = modalRef.current;
    if (dialogElement) {
      // Show the dialog if it's not already shown
      if (!dialogElement.open) {
        dialogElement.showModal();
      }
      // Focus the close button for keyboard navigation
      if (closeButtonRef.current) {
        closeButtonRef.current.focus();
      }
    }

    // Handle close via dialog cancel event (Esc key)
    const handleCancel = (e) => {
      e.preventDefault();
      onClose();
    };

    // Handle backdrop click (click outside dialog)
    const handleBackdropClick = (e) => {
      if (dialogElement && e.target === dialogElement) {
        onClose();
      }
    };

    dialogElement?.addEventListener('cancel', handleCancel);
    dialogElement?.addEventListener('click', handleBackdropClick);
    return () => {
      dialogElement?.removeEventListener('cancel', handleCancel);
      dialogElement?.removeEventListener('click', handleBackdropClick);
      // Close dialog when modal closes
      if (dialogElement?.open) {
        dialogElement.close();
      }
    };
  }, [isOpen, onClose]);

  // Format date for display
  const formatDate = (dateValue) => {
    if (!dateValue) return 'Unknown date';
    try {
      // Parse date string manually to avoid timezone issues
      // Expected format: YYYY-MM-DD or YYYY-MM-DDTHH:mm:ss
      const dateStr = String(dateValue);
      const dateMatch = dateStr.match(/^(\d{4})-(\d{2})-(\d{2})/);
      
      if (!dateMatch) {
        // Fallback to Date object if format doesn't match
        const date = new Date(dateValue);
        if (isNaN(date.getTime())) return 'Invalid date';
        
        return date.toLocaleDateString('en-US', {
          weekday: 'long',
          year: 'numeric',
          month: 'long',
          day: 'numeric'
        });
      }
      
      // Extract year, month, day from the string
      const year = parseInt(dateMatch[1], 10);
      const month = parseInt(dateMatch[2], 10) - 1; // Convert to 0-indexed
      const day = parseInt(dateMatch[3], 10);
      
      // Create date object with local timezone (midnight)
      const date = new Date(year, month, day);
      
      return date.toLocaleDateString('en-US', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    } catch {
      return 'Invalid date';
    }
  };

  // Format price as currency
  const formatPrice = (price) => {
    if (price === null || price === undefined) return 'N/A';
    
    // Format as USD currency
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(price);
  };

  

  // Handle status change (admin only)
  const handleStatusChange = async (e) => {
    const newStatus = e.target.value;
    setSelectedStatus(newStatus);

    if (!isAdmin || !event || !event.id) return;

    setIsUpdating(true);
    try {
      const response = await fetch(
        `/api/events/id/${event.id}/${newStatus}`,
        {
          method: 'PUT',
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
          },
        }
      );

      if (!response.ok) {
        throw new Error(`Failed to update status: ${response.statusText}`);
      }

      // Callback to parent to update events
      if (onStatusChange) {
        onStatusChange(event.id, newStatus);
      }

      console.log(`Event ${event.id} status updated to ${newStatus}`);
    } catch (error) {
      console.error('Status update failed:', error);
      // Revert to previous status on error
      setSelectedStatus(event.status);
    } finally {
      setIsUpdating(false);
    }
  };

  // Handle ticket purchase
  const buyTicket = async () => {
    // Clear previous messages
    setPurchaseMessage('');
    setPurchaseError('');
    
    // Validate conditions
    if (!currentUser) {
      setPurchaseError('You must be logged in to purchase tickets');
      return;
    }
    
    if (event.status !== 'SCHEDULED') {
      setPurchaseError('Tickets can only be purchased for scheduled events');
      return;
    }
    
    setIsPurchasing(true);
    
    try {
      // Step 1: Fetch ticket for the event
      console.log(`Fetching ticket for event ID: ${event.id}`);
      const ticketResponse = await fetch(
        `/api/tickets/event/${event.id}?page=0&size=1&sortBy=id&ascending=true`,
        {
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
          },
        }
      );
      
      if (!ticketResponse.ok) {
        throw new Error(`Failed to fetch ticket: ${ticketResponse.statusText}`);
      }
      
      const ticketData = await ticketResponse.json();
      // Handle both response formats: content array or direct array
      const tickets = Array.isArray(ticketData) ? ticketData : (ticketData.content || []);
      
      if (tickets.length === 0) {
        throw new Error('No tickets found for this event');
      }
      
      const ticket = tickets[0];
      console.log('Ticket fetched:', {
        id: ticket.id,
        sold: ticket.sold,
        total_quantity: ticket.total_quantity
      });
      
      // Check if ticket is sold out
      if (ticket.sold >= ticket.total_quantity) {
        setPurchaseError('Sorry, this event is sold out');
        setIsPurchasing(false);
        return;
      }
      
      // Step 2: Create ticket sale record
      const today = new Date().toISOString().split('T')[0]; // YYYY-MM-DD
      const ticketSaleBody = {
        user_id: currentUser.id,
        ticket_id: ticket.id,
        date_sold: today
      };
      
      console.log('Creating ticket sale with body:', ticketSaleBody);
      console.log('POST /api/tickets-sold');
      
      const saleResponse = await fetch('/api/tickets-sold', {
        method: 'POST',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(ticketSaleBody)
      });
      
      console.log('Ticket sale response status:', saleResponse.status);
      
      if (!saleResponse.ok) {
        throw new Error(`Failed to create ticket sale: ${saleResponse.statusText}`);
      }
      
      // Step 3: Update ticket sold count
      const newSoldCount = ticket.sold + 1;
      console.log(`Updating ticket sold count to: ${newSoldCount}`);
      console.log(`PUT /api/tickets/id/${ticket.id}/sold/${newSoldCount}`);
      
      const updateResponse = await fetch(`/api/tickets/id/${ticket.id}/sold/${newSoldCount}`, {
        method: 'PUT',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
      });
      
      console.log('Ticket update response status:', updateResponse.status);
      
      if (!updateResponse.ok) {
        throw new Error(`Failed to update ticket sold count: ${updateResponse.statusText}`);
      }
      
      // Success!
      setPurchaseMessage('Ticket purchased successfully!');
      console.log('Ticket purchase completed successfully');
      
      // Notify parent about successful purchase
      if (onTicketPurchased && event.id) {
        onTicketPurchased(event.id);
      }
      
    } catch (error) {
      console.error('Ticket purchase failed:', error);
      setPurchaseError(error.message || 'Failed to purchase ticket. Please try again.');
    } finally {
      setIsPurchasing(false);
    }
  };

  if (!isOpen || !event) return null;
  
  // Safely get event name with multiple fallbacks
  const getEventName = () => {
    return event.title || event.name || event.event_name || 'Event Details';
  };
  
  // Safely get event name for display
  const getEventDisplayName = () => {
    return event.title || event.name || event.event_name || 'N/A';
  };
  
  // Safely get venue name
  const getVenueName = () => {
    if (!event.venue) return 'N/A';
    
    if (typeof event.venue === 'string') return event.venue;
    
    if (typeof event.venue === 'object') {
      return event.venue.venue_name || event.venue.name || event.venue.title || 'N/A';
    }
    
    return 'N/A';
  };

  return (
    <dialog 
      className="modal"
      ref={modalRef}
    >
      <div className="modal-content">
        {/* Header with close button */}
        <div className="modal-header">
          <h2 id="modal-title" className="modal-title">
            {getEventName()}
          </h2>
          <button
            ref={closeButtonRef}
            className="modal-close-btn"
            onClick={onClose}
            aria-label="Close event details modal"
            type="button"
          >
            ✕
          </button>
        </div>

        <div className="modal-body">
          {/* Event Name */}
          <div className="modal-field">
            <label htmlFor="event-name" className="modal-label">Event Name</label>
            <p id="event-name" className="modal-value">{getEventDisplayName()}</p>
          </div>

          {/* Event Date */}
          <div className="modal-field">
            <label htmlFor="event-date" className="modal-label">Date</label>
            <p id="event-date" className="modal-value">{formatDate(event.date)}</p>
          </div>

          {/* Event Venue */}
          <div className="modal-field">
            <label htmlFor="event-venue" className="modal-label">Venue</label>
            <p id="event-venue" className="modal-value">{getVenueName()}</p>
          </div>

          {/* Event Status - Role-based display */}
          <div className="modal-field">
            <label htmlFor="event-status" className="modal-label">
              Status
            </label>
            {isAdmin ? (
              <select
                id="event-status"
                className="modal-status-dropdown"
                value={selectedStatus}
                onChange={handleStatusChange}
                disabled={isUpdating}
                aria-label={`Event status dropdown for ${getEventName()}`}
              >
                <option value="SCHEDULED">Scheduled</option>
                <option value="COMPLETED">Completed</option>
                <option value="CANCELLED">Cancelled</option>
              </select>
            ) : (
              <div id="event-status" className="modal-value">
                <StatusBadge status={event.status} size="md" />
              </div>
            )}
          </div>

          {/* Ticket Price */}
          <div className="modal-field">
            <label htmlFor="ticket-price" className="modal-label">
              Ticket Price
            </label>
            <div id="ticket-price" className="modal-value">
              {ticketLoading ? (
                <span className="ticket-price-loading">Loading price...</span>
              ) : ticketError ? (
                <span className="ticket-price-error">{ticketError}</span>
              ) : (
                <span className="ticket-price">{formatPrice(ticketPrice)}</span>
              )}
            </div>
          </div>
        </div>

        {/* Footer with action button */}
        <div className="modal-footer">
          {event.status === 'SCHEDULED' && currentUser ? (
            <>
              <button 
                className="modal-btn modal-btn-primary"
                onClick={buyTicket}
                type="button"
                disabled={isPurchasing}
                aria-label={`Purchase ticket for ${getEventName()}`}
              >
                {isPurchasing ? 'Purchasing...' : 'Buy Ticket'}
              </button>
              {purchaseMessage && (
                <p className="purchase-success" style={{ color: 'green', marginTop: '10px' }}>
                  {purchaseMessage}
                </p>
              )}
              {purchaseError && (
                <p className="purchase-error" style={{ color: 'red', marginTop: '10px' }}>
                  {purchaseError}
                </p>
              )}
            </>
          ) : event.status === 'SCHEDULED' && !currentUser ? (
            <p className="login-prompt">
              Login required to buy tickets
            </p>
          ) : null}
          <button 
            className="modal-btn modal-btn-secondary"
            onClick={onClose}
            type="button"
            aria-label={`Close event details for ${getEventName()}`}
          >
            Close
          </button>
        </div>
      </div>
    </dialog>
  );
}

export default EventModal;
