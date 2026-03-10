import axios from 'axios';

const service = axios.create({
  baseURL: 'http://localhost:7086',
  timeout: 50000,
});

service.interceptors.request.use(
  config => {
    console.log(`[REQUEST] ${config.method?.toUpperCase()} ${config.url}`, config.data);
    return config;
  },
  error => {
    console.error('[REQUEST ERROR]', error);
    return Promise.reject(error);
  }
);

service.interceptors.response.use(
  response => {
    console.log(`[RESPONSE] ${response.status} ${response.config.url}`, response.data);
    return response.data;
  },
  error => {
    console.error('[RESPONSE ERROR]', error.response?.status, error.response?.config?.url, error.response?.data);
    return Promise.reject(error);
  }
);

export default service;
