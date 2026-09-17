import api from './api';

export const operationService = {
  getAllOperations: () => api.get('/v1/flight-operations'),
  getOperationById: (id) => api.get(`/v1/flight-operations/${id}`),
  createOperation: (data) => api.post('/v1/flight-operations', data),
  updateOperation: (id, data) => api.put(`/v1/flight-operations/${id}`, data),
  deleteOperation: (id) => api.delete(`/v1/flight-operations/${id}`),
  searchOperations: (params) => api.get('/v1/flight-operations/search', { params }),
  getHealth: () => api.get('/actuator/health'),
};
