import '../styles/Skeleton.css';

function CalendarSkeleton() {
  return (
    <div className="skeleton-calendar">
      <div className="skeleton-calendar-header">
        <div className="skeleton skeleton-title"></div>
        <div className="skeleton-calendar-nav">
          <div className="skeleton skeleton-button"></div>
          <div className="skeleton skeleton-button"></div>
        </div>
      </div>
      <div className="skeleton-calendar-grid">
        {Array.from({ length: 7 }, (_, index) => (
          <div key={`header-${index}`} className="skeleton-calendar-day-header">
            <div className="skeleton skeleton-text"></div>
          </div>
        ))}
        {Array.from({ length: 42 }, (_, index) => (
          <div key={`day-${index}`} className="skeleton-calendar-day">
            <div className="skeleton skeleton-day-number"></div>
            <div className="skeleton-calendar-events">
              <div className="skeleton skeleton-event"></div>
              <div className="skeleton skeleton-event"></div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default CalendarSkeleton;