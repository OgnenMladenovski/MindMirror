import axios from 'axios';

// Relative by default so the Vite dev proxy (see vite.config.js) forwards to the
// backend. Set VITE_API_URL to an absolute origin only for non-proxied deploys.
const baseURL = (import.meta.env.VITE_API_URL || '') + '/api';

const api = axios.create({ baseURL });

export const TOKEN_KEY = 'mm_token';

api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY);
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response && err.response.status === 401) {
      localStorage.removeItem(TOKEN_KEY);
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = '/login';
      }
    }
    return Promise.reject(err);
  }
);

export default api;
