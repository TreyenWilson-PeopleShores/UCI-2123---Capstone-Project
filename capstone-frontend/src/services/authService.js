import { apiPost } from './httpService';

export async function login(username, password) {
  const response = await apiPost('/api/auth/login', { username, password });
  const authData = await response.json();
  
  // Store the JWT token in localStorage
  if (authData.accessToken) {
    localStorage.setItem('accessToken', authData.accessToken);
  }
  
  // Return both token and user info
  return authData;
}