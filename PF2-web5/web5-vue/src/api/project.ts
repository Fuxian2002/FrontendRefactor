import request from '../utils/request';
import type { Project } from '../types/Project';

export function getPhenomenon(project: Project) {
  return request({
    url: '/project/getPhenomenon',
    method: 'post',
    data: project
  });
}

export function getReference(project: Project) {
  return request({
    url: '/project/getReference',
    method: 'post',
    data: project
  });
}

export function checkCorrectness(project: Project) {
  return request({
    url: '/project/checkCorrectness',
    method: 'post',
    data: project
  });
}

export function checkCorrectContext(project: Project) {
  return request({
    url: '/project/checkCorrectContext',
    method: 'post',
    data: project
  });
}

export function checkCorrectProblem(project: Project) {
  return request({
    url: '/project/checkCorrectProblem',
    method: 'post',
    data: project
  });
}

export function checkWellFormed(project: Project) {
  return request({
    url: '/project/checkWellFormed',
    method: 'post',
    data: project
  });
}

export function getSubProblemDiagram(project: Project) {
  return request({
    url: '/project/getSubProblemDiagram',
    method: 'post',
    data: project
  });
}

export function checkStrategy(project: Project) {
  return request({
    url: '/project/ckeckStrategy',
    method: 'post',
    data: project
  });
}

export function initTrace(project: Project) {
  return request({
    url: '/project/initTrace',
    method: 'post',
    data: project
  });
}

export function checkTrace(body: any) {
  return request({
    url: '/project/checkTrace',
    method: 'post',
    data: body
  });
}

export function analyzeDataDependencies(project: Project) {
  return request({
    url: '/project/analyzeDataDependencies',
    method: 'post',
    data: project
  });
}

export function analyzeControlDependencies(project: Project) {
  return request({
    url: '/project/analyzeControlDependencies',
    method: 'post',
    data: project
  });
}

export function recordLastProject(projectRecord: any) {
    return request({
        url: '/project/recordLastProject',
        method: 'post',
        data: projectRecord
    });
}
