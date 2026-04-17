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
    <section className="cal-component" aria-label="Event calendar">
      <h2>Event Calendar</h2>
      
      <div className="calendar-navigation" role="navigation" aria-label="Calendar navigation">
        <button 
          className="nav-button"
          onClick={goToPreviousMonth}
          aria-label={`Go to previous month, ${monthNames[currentMonth === 0 ? 11 : currentMonth - 1]} ${currentMonth === 0 ? currentYear - 1 : currentYear}`}
          disabled={loading}
        >
          &larr;
        </button>
        
        <h3 className="current-month" id="current-month-heading">
          {monthNames[currentMonth]} {currentYear}
          {loading && ' (loading...)'}
        </h3>
        
        <button 
          className="nav-button"
          onClick={goToNextMonth}
          aria-label={`Go to next month, ${monthNames[currentMonth === 11 ? 0 : currentMonth + 1]} ${currentMonth === 11 ? currentYear + 1 : currentYear}`}
          disabled={loading}
        >
          &rarr;
        </button>
      </div>
      
      <div className="calendar-grid" role="grid" aria-labelledby="current-month-heading" aria-readonly="true">
        <div className="calendar-header" role="row">
          {dayNames.map(day => (
            <div key={day} className="day-header" role="columnheader" aria-label={day}>
              {day}
            </div>
          ))}
        </div>
        
        <div className="calendar-days" role="rowgroup">
          {calendarDays.map((day, index) => (
            <div 
              key={index}
              className={`calendar-day ${day.isCurrentMonth ? 'current-month' : 'other-month'} ${day.isToday ? 'today' : ''}`}
              role="gridcell"
              aria-label={day.isCurrentMonth ? `Day ${day.dayNumber} of ${monthNames[currentMonth]} ${currentYear}` : 'Empty day'}
              aria-selected={day.isToday ? 'true' : 'false'}
            >
              <div className="day-number">
                {day.isCurrentMonth ? day.dayNumber : ''}
              </div>
              
              <div className="day-events">
                {day.events.slice(0, 3).map((event, eventIndex) => {
                  // Determine event status class
                  let eventClass = "event-chip";
                  let statusText = "";
                  if (event.status === "SCHEDULED") {
                    eventClass += " event-chip-scheduled";
                    statusText = "Scheduled event: ";
                  } else if (event.status === "COMPLETED") {
                    eventClass += " event-chip-completed";
                    statusText = "Completed event: ";
                  } else if (event.status === "CANCELLED") {
                    eventClass += " event-chip-cancelled";
                    statusText = "Cancelled event: ";
                  }
                  
                  return (
                    <div 
                      key={eventIndex}
                      className={eventClass}
                      title={`${statusText}${event.title}`}
                      aria-label={`${statusText}${event.title}`}
                    >
                      {event.title.length > 15 ? event.title.substring(0, 12) + '...' : event.title}
                    </div>
                  );
                })}
                
                {day.events.length > 3 && (
                  <div 
                    className="event-chip event-chip-more"
                    title={`${day.events.length - 3} more events`}
                    aria-label={`${day.events.length - 3} more events on day ${day.dayNumber}`}
                  >
                    +{day.events.length - 3}
                  </div>
                )}
              </div>
              
              {day.events.length > 0 && (
                <div 
                  className={`event-dot ${day.events[0].status === "SCHEDULED" ? "event-dot-scheduled" : day.events[0].status === "COMPLETED" ? "event-dot-completed" : "event-dot-cancelled"}`}
                  aria-label={`Event status indicator: ${day.events[0].status}`}
                />
              )}
            </div>
          ))}
        </div>
      </div>
      
      <div className="calendar-summary" role="status" aria-live="polite">
        <p>
          {eventsInMonth} event{eventsInMonth !== 1 ? 's' : ''} in {monthNames[currentMonth]} {currentYear}
        </p>
      </div>
    </section>
  );
}

export default Cal;