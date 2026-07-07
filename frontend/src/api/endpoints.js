import api from './client';

export const authApi = {
  login: (usernameOrEmail, password) =>
    api.post('/auth/login', { usernameOrEmail, password }).then((r) => r.data),
  register: (payload) => api.post('/auth/register', payload).then((r) => r.data),
  me: () => api.get('/users/me').then((r) => r.data),
  updateMe: (payload) => api.put('/users/me', payload).then((r) => r.data),
};

export const logsApi = {
  create: (payload) => api.post('/logs', payload).then((r) => r.data),
  history: () => api.get('/logs').then((r) => r.data),
};

export const dashboardApi = {
  get: () => api.get('/dashboard').then((r) => r.data),
  trends: () => api.get('/dashboard/trends').then((r) => r.data),
  prediction: () => api.get('/dashboard/prediction').then((r) => r.data),
};

export const recommendationsApi = {
  latest: (limit = 10) => api.get(`/recommendations?limit=${limit}`).then((r) => r.data),
};

export const avatarApi = {
  current: () => api.get('/avatar').then((r) => r.data),
  history: () => api.get('/avatar/history').then((r) => r.data),
};

export const challengesApi = {
  today: () => api.get('/challenges/today').then((r) => r.data),
  list: () => api.get('/challenges').then((r) => r.data),
  complete: (id) => api.post(`/challenges/${id}/complete`).then((r) => r.data),
};

export const achievementsApi = {
  list: () => api.get('/achievements').then((r) => r.data),
};

export const hbscApi = {
  comparison: () => api.get('/hbsc/comparison').then((r) => r.data),
};

export const chatApi = {
  send: (message, lang) => api.post('/chat', { message, lang }).then((r) => r.data),
};

export const notificationsApi = {
  list: () => api.get('/notifications').then((r) => r.data),
  unread: () => api.get('/notifications/unread-count').then((r) => r.data),
  markRead: (id) => api.post(`/notifications/${id}/read`).then((r) => r.data),
};

export const statisticsApi = {
  overview: () => api.get('/statistics').then((r) => r.data),
};
