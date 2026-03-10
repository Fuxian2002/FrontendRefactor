import request from '../utils/request';
import { getUserName } from '../utils/auth';
import type { Project } from '../types/Project';
import type { OntologyEntity } from '../types/OntologyEntity';

export function setProject(projectAddress: string) {
  const username = getUserName();
  return request({
    url: `/file/setProject/${projectAddress}`,
    method: 'post',
    params: { username }
  });
}

export function uploadFile(projectAddress: string, formData: FormData) {
  const username = getUserName();
  return request({
    url: `/file/upload/${projectAddress}`,
    method: 'post',
    params: { username },
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  });
}

export function uploadOwlFile(projectAddress: string, type: string, formData: FormData) {
  const username = getUserName();
  return request({
    url: `/file/uploadOwl/${projectAddress}/${type}`,
    method: 'post',
    params: { username },
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  });
}

export function uploadPfFile(projectAddress: string, formData: FormData) {
  const username = getUserName();
  return request({
    url: `/file/uploadpf/${projectAddress}`,
    method: 'post',
    params: { username },
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  });
}

export function moveOwl(type: string, projectAddress: string, owl: string, version: string) {
  const username = getUserName();
  return request({
    url: `/file/moveOwl/${type}/${projectAddress}/${owl}/${version}`,
    method: 'post',
    params: { username }
  });
}

export function movePOwl(projectAddress: string, owl: string, version: string) {
  return moveOwl("powl", projectAddress, owl, version);
}

export function moveEOwl(projectAddress: string, owl: string, version: string) {
  return moveOwl("eowl", projectAddress, owl, version);
}

export function getProject(projectName: string, ver: string) {
  const username = getUserName();
  return request({
    url: `/file/getProject/${projectName}/${ver}`,
    method: 'get',
    params: { username }
  });
}

export function saveProject(projectAddress: string, project: Project) {
  const username = getUserName();
  return request({
    url: `/file/saveProject/${projectAddress}`,
    method: 'post',
    params: { username },
    data: project
  });
}

export function formatProject(projectAddress: string, project: Project) {
  const username = getUserName();
  return request({
    url: `/file/format/${projectAddress}`,
    method: 'post',
    params: { username },
    data: project
  });
}

export function searchProject() {
  const username = getUserName();
  return request({
    url: `/file/searchProject`,
    method: 'get',
    params: { username }
  });
}

export function searchVersion(project: string) {
  const username = getUserName();
  return request({
    url: `/file/searchVersion/${project}`,
    method: 'get',
    params: { username }
  });
}

export function searchOwlVersion(owl: string) {
  const username = getUserName();
  return request({
    url: `/file/searchOwlVersion/${owl}`,
    method: 'get',
    params: { username }
  });
}

export function getNodes(owlAdd: string, powlName: string, nodeName: string, type: string) {
  const username = getUserName();
  return request({
    url: `/file/getNodes/${owlAdd}/${powlName}/${nodeName}/${type}`,
    method: 'get',
    params: { username }
  });
}

export function searchOwl(type: string) {
  const username = getUserName();
  return request({
    url: `/file/searchOwl/${type}`,
    method: 'get',
    params: { username }
  });
}

export function getProblemDomains(owlAdd: string, owlName: string) {
  const username = getUserName();
  return request({
    url: `/file/getProblemDomains/${owlAdd}/${owlName}`,
    method: 'get',
    params: { username }
  });
}

export function getPFProject(projectAddress: string) {
  const username = getUserName();
  return request({
    url: `/file/xtextToXmi`,
    method: 'post',
    params: { username },
    data: projectAddress, // Note: Angular code sent projectAddress as body
    headers: { 'Content-Type': 'application/json' }
  });
}

export function downloadProject(projectAddress: string) {
  const username = getUserName();
  return request({
    url: `/file/download/${projectAddress}`,
    method: 'post',
    params: { username },
    data: projectAddress,
    responseType: 'blob' // Assuming download returns a file
  });
}

export function findOwl(projectName: string, version: string) {
  const username = getUserName();
  return request({
    url: `/file/findOwl`,
    method: 'get',
    params: { username, projectName, version }
  });
}
