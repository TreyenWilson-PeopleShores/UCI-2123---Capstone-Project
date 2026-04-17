import '../styles/StatusBadge.css';

/**
 * StatusBadge Component
 * 
 * A reusable component for displaying event status with consistent styling.
 * Can be used in UpcomingEvents, Calendar, EventModal, and future components.
 * 
 * @param {Object} props
 * @param {string} props.status - The status value (SCHEDULED, COMPLETED, CANCELLED)
 * @param {string} [props.className] - Additional CSS classes
 * @param {string} [props.size] - Size variant: 'sm' (small), 'md' (medium), 'lg' (large)
 */
const StatusBadge = ({ status, className = '', size = 'md' }) => {
  // Map status to display text
  const statusDisplay = {
    SCHEDULED: 'Scheduled',
    COMPLETED: 'Completed', 
    CANCELLED: 'Cancelled'
  };

  // Get display text
  const displayText = statusDisplay[status] || status || 'Unknown';

  // Determine CSS classes based on status and size
  const statusClass = status ? status.toLowerCase() : 'unknown';
  const sizeClass = `status-badge-${size}`;

  return (
    <span 
      className={`status-badge status-badge-${statusClass} ${sizeClass} ${className}`}
      aria-label={`Event status: ${displayText}`}
    >
      {displayText}
    </span>
  );
};

export default StatusBadge;