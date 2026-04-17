import { useEffect, useRef } from 'react';

function EventModal({ event, isOpen, onClose, isAdmin = false }) {
  const modalRef = useRef(null);
  const closeButtonRef = useRef(null);

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
                defaultValue={event.status}
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
          <button 
            className="modal-btn modal-btn-primary"
            onClick={onClose}
            type="button"
            aria-label={`Purchase ticket for ${event.title}`}
          >
            Buy Ticket
          </button>
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
