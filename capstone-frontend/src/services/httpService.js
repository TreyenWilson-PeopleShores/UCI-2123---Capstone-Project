import { buildApiUrl, defaultJsonHeaders } from './apiConfig';

const normalizeHeaders = (headers = {}) => ({
  ...defaultJsonHeaders,
  ...headers,
});

const handleErrorResponse = async (response) => {
  let message = response.statusText || 'Request failed';
  try {
    const text = await response.text();
    if (text) {
      message = text;
    }
  } catch {
    // ignore parse errors
  }
  const error = new Error(message);
  error.status = response.status;
  throw error;
};

const apiFetch = async (path, options = {}) => {
  const url = buildApiUrl(path);
  const response = await fetch(url, {
    ...options,
    headers: normalizeHeaders(options.headers),
  });

  if (!response.ok) {
    await handleErrorResponse(response);
  }

  return response;
};

const apiGet = (path, options = {}) => apiFetch(path, { ...options, method: 'GET' });
const apiPost = (path, body, options = {}) => apiFetch(path, { ...options, method: 'POST', body: body != null ? JSON.stringify(body) : undefined });
const apiPut = (path, body, options = {}) => apiFetch(path, { ...options, method: 'PUT', body: body != null ? JSON.stringify(body) : undefined });

export { apiFetch, apiGet, apiPost, apiPut };