import { useState, useEffect } from 'react';
import Cal from '../components/Cal';

function EventsPage() {
  // State to store the events data
  const [events, setEvents] = useState([]);
  // State to track loading status
  const [loading, setLoading] = useState(true);
  // State to track any errors
  const [error, setError] = useState(null);

  // useEffect hook to fetch data when component mounts
  useEffect(() => {
    // Define async function inside useEffect
    const fetchEvents = async () => {
      try {
        setLoading(true);
        setError(null);
        
        // Make API call to backend with headers to accept JSON
        const response = await fetch('/api/events', {
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
          },
        });
        
        // Log response for debugging
        console.log('Response status:', response.status, response.statusText);
        
        // Check if response is ok
        if (!response.ok) {
          // Try to get error text for better debugging
          const errorText = await response.text();
          console.error('Error response:', errorText);
          throw new Error(`HTTP error! status: ${response.status} - ${response.statusText}`);
        }
        
        // Parse JSON response
        const responseData = await response.json();
        console.log('API Response:', responseData);
        
        // Handle paginated response structure
        // The API returns { content: [...] } according to the document
        const eventsData = responseData.content || responseData;
        
        // Update state with fetched events
        setEvents(Array.isArray(eventsData) ? eventsData : []);
      } catch (err) {
        // Handle any errors
        setError(err.message || 'Failed to fetch events');
        console.error('Error fetching events:', err);
      } finally {
        // Always set loading to false when done
        setLoading(false);
      }
    };

    // Call the async function
    fetchEvents();
    
    // Empty dependency array means this effect runs once on mount
  }, []);

  // Render loading state
  if (loading) {
    return (
      <div className="events-page">
        <h1>Events</h1>
        <div className="loading">Loading events...</div>
      </div>
    );
  }

  // Render error state
  if (error) {
    return (
      <div className="events-page">
        <h1>Events</h1>
        <div className="error">
          <p>Error: {error}</p>
          <p>Check if:</p>
          <ul>
            <li>Backend is running on http://localhost:8080</li>
            <li>CORS is configured on the backend</li>
            <li>The endpoint /api/events exists</li>
          </ul>
        </div>
        <button onClick={() => window.location.reload()}>Retry</button>
        <button onClick={() => {
          setLoading(true);
          setError(null);
          // Re-fetch events
          fetch('/api/events')
            .then(res => {
              console.log('Manual fetch status:', res.status);
              return res.text();
            })
            .then(text => {
              console.log('Manual fetch response (first 500 chars):', text.substring(0, 500));
              setError('Response preview: ' + text.substring(0, 200));
              setLoading(false);
            })
            .catch(err => {
              console.error('Manual fetch error:', err);
              setError('Manual fetch error: ' + err.message);
              setLoading(false);
            });
        }}>Debug Response</button>
      </div>
    );
  }

  // Render main content with events
  return (
    <div className="events-page">
      <h1>Events</h1>
      <p>Total events: {events.length}</p>
      
      {/* Pass events data to Cal component as props */}
      <Cal events={events} />
    </div>
  );
}

export default EventsPage;
