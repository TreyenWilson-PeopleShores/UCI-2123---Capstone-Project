import { apiPost } from './httpService';

export async function login(username, password) {
  const response = await apiPost('/auth/login', { username, password });
  return response.json();
}