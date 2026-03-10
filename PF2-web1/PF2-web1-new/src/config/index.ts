export const BACKEND_HOST = 'localhost';
export const BACKEND_PORT = 7084;

export const BACKEND_BASE_URL = `http://${BACKEND_HOST}:${BACKEND_PORT}`;
export const WS_BASE_URL = `ws://${BACKEND_HOST}:${BACKEND_PORT}`;

export const CONFIG = {
  backend: {
    baseUrl: BACKEND_BASE_URL,
    wsUrl: WS_BASE_URL,
    endpoints: {
      webSocket: '/webSocket',
      textLsp: '/TextLSP',
      searchProject: '/file/searchProject',
      getProject: '/file/getProject',
      getNotNullPf: '/file/getNotNullPf',
      saveProject: '/file/saveProject',
      download: '/file/download',
      setProject: '/file/setProject',
      upload: '/file/upload',
      checkCorrectContext: '/project/checkCorrectContext',
      checkCorrectProblem: '/project/checkCorrectProblem'
    }
  },
  external: {
    helpUrl: 'http://re4cps.org/help'
  }
};
