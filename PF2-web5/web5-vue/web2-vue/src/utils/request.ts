import axios from 'axios';

const service = axios.create({
  baseURL: 'http://localhost:7085',
  timeout: 50000, // Increased timeout as some file operations might take time
});

service.interceptors.request.use(
  config => {
    return config;
  },
  error => {
    console.error(error);
    return Promise.reject(error);
  }
);

service.interceptors.response.use(
  response => {
    return response.data;
  },
  error => {
    console.error('Request Error:', error);
    return Promise.reject(error);
  }
);

export default service;
