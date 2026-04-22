import { apiPost } from './httpService';

export async function registerUser(userData) {
  // Use the JWT register endpoint instead of /api/users
  const response = await apiPost('/api/auth/register', userData);
  const authData = await response.json();
  
  // Store the JWT token in localStorage
  if (authData.accessToken) {
    localStorage.setItem('accessToken', authData.accessToken);
  }
  
  // Return user info from the response
  return authData.user || authData;
}