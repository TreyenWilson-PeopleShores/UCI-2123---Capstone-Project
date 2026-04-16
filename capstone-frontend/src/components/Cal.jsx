function Cal({ events }) {
  // If no events are passed or events array is empty
  if (!events || events.length === 0) {
    return (
      <div className="cal-component">
        <h2>Event Calendar</h2>
        <p>No events available.</p>
      </div>
    );
  }

  return (
    <div className="cal-component">
      <h2>Event Calendar</h2>
      <p>Displaying {events.length} events:</p>
      
      {/* Simple list of events */}
      <ul className="events-list">
        {events.map((event, index) => (
          <li key={event.id || index} className="event-item">
            <div className="event-name">
              <strong>{event.event_name || event.name || `Event ${index + 1}`}</strong>
            </div>
            <div className="event-date">
              Date: {event.date || 'Date not specified'}
            </div>
            <div className="event-status">
              Status: {event.status || 'Unknown'}
            </div>
            <div className="event-venue">
              Venue: {event.venue?.venue_name || 'Venue not specified'}
            </div>
            <div className="event-spots">
              Total Spots: {event.total_spots || 0}
            </div>
            {/* You can add more event fields here as needed */}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default Cal;