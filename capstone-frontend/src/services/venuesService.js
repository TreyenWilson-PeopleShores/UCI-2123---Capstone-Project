import { apiGet } from './httpService';

export async function getVenueById(venueId) {
  const response = await apiGet(`/api/venues/id/${venueId}`);
  return response.json();
}