import request from '../utils/request';
import { getUserName } from '../utils/auth';
import type { Project } from '../types/Project';

// Note: Backend endpoint seems to use 'verfication' (typo) based on Angular code
export function toCCSL(project: Project) {
  const username = getUserName();
  return request({
    url: `/verfication/toCCSL`,
    method: 'post',
    params: { username },
    data: project
  });
}
