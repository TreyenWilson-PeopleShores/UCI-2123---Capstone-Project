const API_BASE_URL = import.meta.env.VITE_API_BASE_URL?.trim() || '';

const buildApiUrl = (path) => {
  if (!path) return API_BASE_URL;
  if (/^https?:\/\//i.test(path)) {
    return path;
  }

  const normalizedBase = API_BASE_URL.replace(/\/+$|^\s+|\s+$/g, '');
  const normalizedPath = path.startsWith('/') ? path : `/${path}`;

  return normalizedBase ? `${normalizedBase}${normalizedPath}` : normalizedPath;
};

const defaultJsonHeaders = {
  Accept: 'application/json',
  'Content-Type': 'application/json',
};

export { buildApiUrl, defaultJsonHeaders };