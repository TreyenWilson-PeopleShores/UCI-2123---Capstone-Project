import { apiGet, apiPut } from './httpService';

export async function getEventsByDateRange(start, end, page = 0) {
  const response = await apiGet(`/api/events/date?start=${start}&end=${end}&page=${page}`);
  return response.json();
}

export async function getAllEventsByDateRange(start, end) {
  let allEvents = [];
  let page = 0;
  let hasMorePages = true;
  let pageData = null;

  while (hasMorePages) {
    pageData = await getEventsByDateRange(start, end, page);
    const pageEvents = Array.isArray(pageData) ? pageData : pageData.content || [];
    allEvents = [...allEvents, ...pageEvents];
    hasMorePages = !Array.isArray(pageData) && !pageData.last && page < (pageData.totalPages || 0) - 1;
    page += 1;
  }

  return { events: allEvents, meta: pageData };
}

export async function getEventById(eventId) {
  const response = await apiGet(`/api/events/id/${eventId}`);
  return response.json();
}

export async function updateEventStatus(eventId, status) {
  const response = await apiPut(`/api/events/id/${eventId}/${status}`);
  return response.json().catch(() => null);
}