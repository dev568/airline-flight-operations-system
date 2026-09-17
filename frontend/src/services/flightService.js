import api from './api';

export const flightService = {
  getAllFlights: () => api.get('/v1/flights'),
  getFlightById: (id) => api.get(`/v1/flights/${id}`),
  createFlight: (data) => api.post('/v1/flights', data),
  updateFlight: (id, data) => api.put(`/v1/flights/${id}`, data),
  deleteFlight: (id) => api.delete(`/v1/flights/${id}`),
  searchFlights: (params) => api.get('/v1/flights/search', { params }),
  getHealth: () => api.get('/actuator/health'),
};
