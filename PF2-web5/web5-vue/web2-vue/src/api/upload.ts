import request from '../utils/request';
import { getUserName } from '../utils/auth';

export function uploadFileWithProgress(projectAddress: string, file: File, onProgress: (percent: number) => void) {
  const username = getUserName();
  const formData = new FormData();
  formData.append('file', file);

  return request({
    url: `/file/upload/${projectAddress}`,
    method: 'post',
    params: { username },
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
    onUploadProgress: (progressEvent) => {
      if (progressEvent.total) {
        const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
        onProgress(percentCompleted);
      }
    }
  });
}
