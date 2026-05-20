import axios from 'axios';

export const authApi = axios.create({ baseURL: '/auth' });
export const familyApi = axios.create({ baseURL: '/family' });
export const calendarApi = axios.create({ baseURL: '/calendar' });

export function saveUser(user) {
  localStorage.setItem('user', JSON.stringify(user));
}

export function getCurrentUser() {
  return JSON.parse(localStorage.getItem('user') || 'null');
}

export function clearUser() {
  localStorage.removeItem('user');
  localStorage.removeItem('familyId');
}

export function getFamilyId() {
  return localStorage.getItem('familyId');
}

export function saveFamilyId(familyId) {
  localStorage.setItem('familyId', String(familyId));
}
