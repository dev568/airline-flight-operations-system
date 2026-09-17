import api from './api';

export const crewService = {
  getAllCrewMembers: () => api.get('/v1/crew-members'),
  getCrewMemberById: (id) => api.get(`/v1/crew-members/${id}`),
  createCrewMember: (data) => api.post('/v1/crew-members', data),
  updateCrewMember: (id, data) => api.put(`/v1/crew-members/${id}`, data),
  deleteCrewMember: (id) => api.delete(`/v1/crew-members/${id}`),
  searchCrewMembers: (params) => api.get('/v1/crew-members/search', { params }),
  getHealth: () => api.get('/actuator/health'),
};
