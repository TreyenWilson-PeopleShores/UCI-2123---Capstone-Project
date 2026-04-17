import { useEffect, useRef, useState } from 'react';
import { useAuth } from '../contexts/AuthContext';

function EventModal({ event, isOpen, onClose, onStatusChange }) {
  const { currentUser, isAdmin } = useAuth();
  const modalRef = useRef(null);
  const closeButtonRef = useRef(null);
  const [selectedStatus, setSelectedStatus] = useState(event?.status || 'SCHEDULED');
  const [isUpdating, setIsUpdating] = useState(false);

  // Update selectedStatus when event changes
  useEffect(() => {
    if (event) {
      setSelectedStatus(event.status);
    }
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
      const date = new Date(dateValue);
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

  // Map status to display value
  const statusDisplay = {
    SCHEDULED: 'Scheduled',
    COMPLETED: 'Completed',
    CANCELLED: 'Cancelled'
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

  if (!isOpen || !event) return null;

  const statusLabel = statusDisplay[event.status] || event.status || 'Unknown';

  return (
    <dialog 
      className="modal"
      ref={modalRef}
    >
      <div className="modal-content">
        {/* Header with close button */}
        <div className="modal-header">
          <h2 id="modal-title" className="modal-title">
            {event.title || event.name || 'Event Details'}
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
            <p id="event-name" className="modal-value">{event.title || event.name || 'N/A'}</p>
          </div>

          {/* Event Date */}
          <div className="modal-field">
            <label htmlFor="event-date" className="modal-label">Date</label>
            <p id="event-date" className="modal-value">{formatDate(event.date)}</p>
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
                aria-label={`Event status dropdown for ${event.title}`}
              >
                <option value="SCHEDULED">Scheduled</option>
                <option value="COMPLETED">Completed</option>
                <option value="CANCELLED">Cancelled</option>
              </select>
            ) : (
              <p 
                id="event-status"
                className="modal-value"
                aria-label={`Event status: ${statusLabel}`}
              >
                {statusLabel}
              </p>
            )}
          </div>
        </div>

        {/* Footer with action button */}
        <div className="modal-footer">
          {event.status === 'SCHEDULED' && currentUser ? (
            <button 
              className="modal-btn modal-btn-primary"
              onClick={onClose}
              type="button"
              aria-label={`Purchase ticket for ${event.title}`}
            >
              Buy Ticket
            </button>
          ) : event.status === 'SCHEDULED' && !currentUser ? (
            <p className="login-prompt">
              Login required to buy tickets
            </p>
          ) : null}
          <button 
            className="modal-btn modal-btn-secondary"
            onClick={onClose}
            type="button"
            aria-label={`Close event details for ${event.title}`}
          >
            Close
          </button>
        </div>
      </div>
    </dialog>
  );
}

export default EventModal;
