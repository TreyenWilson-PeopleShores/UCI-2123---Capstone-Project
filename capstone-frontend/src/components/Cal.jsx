import { useMemo } from 'react';

function Cal({ events = [], loading = false, currentMonth: currentDate = new Date(), onMonthChange }) {
  const currentYear = currentDate.getFullYear();
  const currentMonth = currentDate.getMonth();
  
  // Month names
  const monthNames = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ];
  
  // Day names
  const dayNames = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
  
  // Parse event date to get year, month, day
  const parseEventDate = (dateValue) => {
    if (!dateValue) return null;
    
    try {
      const date = new Date(dateValue);
      if (isNaN(date.getTime())) return null;
      
      return {
        year: date.getFullYear(),
        month: date.getMonth(),
        day: date.getDate()
      };
    } catch (error) {
      return null;
    }
  };
  
  // Normalize events with parsed dates and titles
  const normalizedEvents = useMemo(() => {
    return events.map(event => ({
      ...event,
      parsedDate: parseEventDate(event.date),
      title: event.title || event.name || event.event_name || 'Event'
    }));
  }, [events]);
  
  // Generate calendar days for current month
  const calendarDays = useMemo(() => {
    const days = [];
    const today = new Date();
    const todayString = today.toDateString();
    
    // Get first day of month and its weekday (0 = Sunday)
    const firstDay = new Date(currentYear, currentMonth, 1);
    const firstDayWeekday = firstDay.getDay();
    
    // Get total days in month
    const lastDay = new Date(currentYear, currentMonth + 1, 0);
    const daysInMonth = lastDay.getDate();
    
    // Add empty cells for days before the first day of month
    for (let i = 0; i < firstDayWeekday; i++) {
      days.push({
        dayNumber: null,
        isCurrentMonth: false,
        isToday: false,
        events: []
      });
    }
    
    // Add days of current month
    for (let day = 1; day <= daysInMonth; day++) {
      const date = new Date(currentYear, currentMonth, day);
      const isToday = date.toDateString() === todayString;
      
      // Find events for this day
      const dayEvents = normalizedEvents.filter(event => {
        const parsed = event.parsedDate;
        return (
          parsed &&
          parsed.year === currentYear &&
          parsed.month === currentMonth &&
          parsed.day === day
        );
      });
      
      days.push({
        dayNumber: day,
        isCurrentMonth: true,
        isToday,
        events: dayEvents
      });
    }
    
    // Fill remaining cells to make 42 total (6 weeks)
    while (days.length < 42) {
      days.push({
        dayNumber: null,
        isCurrentMonth: false,
        isToday: false,
        events: []
      });
    }
    
    return days;
  }, [currentYear, currentMonth, normalizedEvents]);
  
  // Navigation functions
  const goToPreviousMonth = () => {
    if (!onMonthChange) return;
    const newDate = new Date(currentDate);
    newDate.setMonth(currentDate.getMonth() - 1);
    onMonthChange(newDate);
  };
  
  const goToNextMonth = () => {
    if (!onMonthChange) return;
    const newDate = new Date(currentDate);
    newDate.setMonth(currentDate.getMonth() + 1);
    onMonthChange(newDate);
  };
  
  // Count events in current month
  const eventsInMonth = normalizedEvents.filter(event => {
    const parsed = event.parsedDate;
    return (
      parsed &&
      parsed.year === currentYear &&
      parsed.month === currentMonth
    );
  }).length;
  
  return (
    <div className="cal-component">
      <h2>Event Calendar</h2>
      
      <div className="calendar-navigation">
        <button 
          className="nav-button"
          onClick={goToPreviousMonth}
          aria-label="Previous month"
          disabled={loading}
        >
          &larr;
        </button>
        
        <h3 className="current-month">
          {monthNames[currentMonth]} {currentYear}
          {loading && ' (loading...)'}
        </h3>
        
        <button 
          className="nav-button"
          onClick={goToNextMonth}
          aria-label="Next month"
          disabled={loading}
        >
          &rarr;
        </button>
      </div>
      
      <div className="calendar-grid">
        <div className="calendar-header">
          {dayNames.map(day => (
            <div key={day} className="day-header">
              {day}
            </div>
          ))}
        </div>
        
        <div className="calendar-days">
          {calendarDays.map((day, index) => (
            <div 
              key={index}
              className={`calendar-day ${day.isCurrentMonth ? 'current-month' : 'other-month'} ${day.isToday ? 'today' : ''}`}
            >
              <div className="day-number">
                {day.isCurrentMonth ? day.dayNumber : ''}
              </div>
              
              <div className="day-events">
                {day.events.slice(0, 3).map((event, eventIndex) => (
                  <div 
                    key={eventIndex}
                    className="event-chip"
                    title={event.title}
                  >
                    {event.title.length > 15 ? event.title.substring(0, 12) + '...' : event.title}
                  </div>
                ))}
                
                {day.events.length > 3 && (
                  <div 
                    className="event-chip event-chip-more"
                    title={`${day.events.length - 3} more events`}
                  >
                    +{day.events.length - 3}
                  </div>
                )}
              </div>
              
              {day.events.length > 0 && (
                <div className="event-dot" />
              )}
            </div>
          ))}
        </div>
      </div>
      
      <div className="calendar-summary">
        <p>
          {eventsInMonth} event{eventsInMonth !== 1 ? 's' : ''} in {monthNames[currentMonth]} {currentYear}
        </p>
      </div>
    </div>
  );
}

export default Cal;