import request from '../utils/request';
import type { Project } from '../types/Project';
import type { RequirementPhenomenon } from '../types/RequirementPhenomenon';
import type { Error } from '../types/Error';
import type { Phenomenon } from '../types/Phenomenon';
// import type { StrategyAdvice } from '../types/StrategyAdvice'; // Assuming this exists or I'll create it

// Helper to get headers if needed, but request.ts handles common headers
// const httpOptions = { headers: { 'Content-Type': 'application/json' } };

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

export function getBehIntList(project: Project, sgName: string) {
  return request({
    url: `/project/getBehIntList/${sgName}`,
    method: 'post',
    data: project
  });
}

export function getExpIntList(project: Project, sgName: string) {
  return request({
    url: `/project/getExpIntList/${sgName}`,
    method: 'post',
    data: project
  });
}

export function getIntList(project: Project, sgName: string) {
  return request({
    url: `/project/getIntList/${sgName}`,
    method: 'post',
    data: project
  });
}

export function getFullExpIntList(project: Project, sgName: string) {
  return request({
    url: `/project/getFullExpIntList/${sgName}`,
    method: 'post',
    data: project
  });
}

export function getScenarioGraph(project: Project, projectName: string, reqVersion: string) {
  return request({
    url: `/project/getScenarioGraph`,
    method: 'post',
    params: { projectName, reqVersion },
    data: project
  });
}

export function getFullScenarioGraph(project: Project) {
  return request({
    url: '/project/getFullScenarioGraph',
    method: 'post',
    data: project
  });
}

export function getBreakdownScenarioGraph(project: Project) {
  return request({
    url: '/project/getBreakdownScenarioGraph',
    method: 'post',
    data: project
  });
}

export function getConnIntList(project: Project, sgName: string) {
  return request({
    url: `/project/getConnIntList/${sgName}`,
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

export function ignoreStrategyAdvice(strategyAdvice: any) { // Type StrategyAdvice later
  return request({
    url: '/project/ignoreStrategyAdvice',
    method: 'post',
    data: strategyAdvice
  });
}

export function saveSpecification(project: Project, userName: string) {
  // Note: Angular code had port 8089 for this one specific call? 
  // "http://localhost:8089/project/saveSpecification?userName=${userName}"
  // If backend is consistent, it should be the same base URL. 
  // I'll use the base URL from request.ts (localhost:7085) for now, 
  // but if it's a different service, I might need a full URL.
  // The Angular code explicitly sets 8089.
  return request({
    url: 'http://localhost:8089/project/saveSpecification',
    method: 'post',
    params: { userName },
    data: project
  });
}
