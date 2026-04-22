import { apiPost } from './httpService';

export async function registerUser(userData) {
  const response = await apiPost('/api/users', userData);
  return response.json();
}