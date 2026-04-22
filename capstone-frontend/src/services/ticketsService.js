import { apiGet, apiPost, apiPut } from './httpService';

export async function getTicketsByEventId(eventId, options = {}) {
  const {
    page = 0,
    size = 10,
    sortBy = 'id',
    ascending = true,
  } = options;

  const response = await apiGet(
    `/api/tickets/event/${eventId}?page=${page}&size=${size}&sortBy=${sortBy}&ascending=${ascending}`
  );
  return response.json();
}

export async function getTicketById(ticketId) {
  const response = await apiGet(`/api/tickets/id/${ticketId}`);
  return response.json();
}

export async function createTicketSale(ticketSale) {
  const response = await apiPost('/api/tickets-sold', ticketSale);
  return response.json();
}

export async function updateTicketSoldCount(ticketId, soldCount) {
  const response = await apiPut(`/api/tickets/id/${ticketId}/sold/${soldCount}`);
  return response.json().catch(() => null);
}

export async function getUserTicketSales(userId, options = {}) {
  const {
    page = 0,
    size = 20,
    sortBy = 'dateSold',
    ascending = false,
  } = options;

  const response = await apiGet(
    `/api/tickets-sold/user/${userId}?page=${page}&size=${size}&sortBy=${sortBy}&ascending=${ascending}`
  );
  return response.json();
}