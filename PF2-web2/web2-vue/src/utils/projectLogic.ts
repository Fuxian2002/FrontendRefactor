import type { Project } from '../types/Project';
import type { ScenarioGraph } from '../types/ScenarioGraph';
import type { Node } from '../types/Node';
import type { Error } from '../types/Error';
import type { Phenomenon } from '../types/Phenomenon';

export function getPhenomenon(project: Project): Phenomenon[] {
  let res: Phenomenon[] = [];
  getPhenomenonHelper(res, project.contextDiagram.interfaceList);
  if (project.problemDiagram) {
    getPhenomenonHelper(res, project.problemDiagram.referenceList);
    getPhenomenonHelper(res, project.problemDiagram.constraintList);
  }
  
  // Remove duplicates
  for (let i = 0; i < res.length; i++) {
    for (let j = res.length - 1; j > i; j--) {
      if (res[j].phenomenon_no === res[i].phenomenon_no) {
        res.splice(j, 1);
      }
    }
  }
  return res;
}

function getPhenomenonHelper(res: Phenomenon[], linkList: any[]) {
  if (!linkList) return;
  for (let link of linkList) {
    if (link.phenomenonList) {
      for (let phe of link.phenomenonList) {
        res.push(phe);
      }
    }
  }
}

export function getRes(errList: Error[]): boolean {
  let res = true;
  for (let i = 0; i < errList.length; i++) {
    if (errList[i].errorList && errList[i].errorList.length > 0) {
      res = false;
      return res;
    }
  }
  return res;
}

export function getStrategy(errMsg: string): string[] {
  const type = errMsg.split(':')[0];
  const strategyList: string[] = [];
  switch (type) {
    case 'IntegrityError':
      strategyList.push("If this interaction is unnecessary, you can remove the phenomenon in the problem graph.");
      break;
    case 'SynTaxError':
      strategyList.push("Interaction has wrong relationships");
      strategyList.push("Behavior order lines can only connect behaviorInteractions,");
      strategyList.push("Expected order lines can only connect expectedInteractions,");
      strategyList.push("Synchrocity lines can only connect behaviorInteractions and behaviorInteractions,");
      strategyList.push("Behavior enable lines can only from behaviorInteractions to expectedInteractions,");
      strategyList.push("expected enable lines can only from expectedInteractions to behaviorInteractions,");
      break;
    case 'SemanticError':
      strategyList.push("Strategy:Introducing New Problem");
      strategyList.push("When a scenario sce has undetermined expected interaction intR, after "
        + "introducing a new domain d(i), the problem description p = {M,DS,IS,RS,SS}, "
        + "where RS = {req1, req2, ~, reqN}, SS = {sce1, sce2, ~, sceN}, can be "
        + "elaborated using the follwing steps:");
      strategyList.push("1. Introduce a domain d into DS; that is, DS=DS U(d). Then, two strategies can be adopted "
        + "for accomplishing the model building. One is introducing a biddable domain b(d) as the model builder, "
        + "which results in another new domain to be added in DS, i.e., DS=DS_[b(d). "
        + "The other is developing an automatic model builder whose functionality needs to be included "
        + "in the model building requirement.");
      strategyList.push("2. Identify new phenomena and interactions involving o(d) and other new domains. Add them into IS.");
      strategyList.push("3. Add a new requirement req(N+1)={building domain d} to RS, i.e., RS=RS U[req(n+1)]. "
        + "Then, there is a refinement relationship between req(i) and req(n+1) that is, refinement(req(i), req(n+1)).");
      strategyList.push("Construct scenario sce(n+1) for req(n+1), SS=SS U{sce(n+1)}. For each scenario involving intR, "
        + "update it so that intR can be determined uniformly.");
      break;
    case 'StateError':
      strategyList.push("Strategy:Introducing Model Domain");
      strategyList.push("When a model domain o(d)(o(d) represents the model domain of d) is introduced to separate the two concerns of the same domain, "
        + "the problem description p = {M,DS,IS,RS,SS},where RS = {req1,req2,??,reqN}, SS = {sce1,sce2,??,sceN}, can be elaborated using "
        + "the follwing steps:");
      strategyList.push("1. Introduce a domain o(d) into DS; that is, DS=DS U {o(d)}. Then, two strategies can be adopted for accomplishing the model "
        + "building. One is introducing a biddable domain b(d) as the model builder, which results in another new domain to be added in DS, "
        + "i.e., DS=DS U {b(d)}.The other is developing an automatic model builder whose functionality needs to be included in the model "
        + "building requirement.");
      strategyList.push("2. Identify new phenomena and interactions involving o(d) and other new domains. Add them into IS;delete interations involving d "
        + "in IS;IS=IS U {int[O(d)/d]}/int");
      strategyList.push("3. Split the original requirements into two separates sub-requirements,RS=RS U {req(n+1)}??where req(n+1) is for building o(d). "
        + "Suppose req(i) ?? RS is the requirement accomplishing original requirements using o(d).Therefore,there is a refinement relationship "
        + "between req(i) and req(n+1), i.e., refinement(req(i), req(n+1)).");
      strategyList.push("4.According to these two requirements sets,do the following:");
      strategyList.push("a)Construct a new scenario sce(n+1),which is for realizing requirement req(n+1);");
      strategyList.push("b)Update the scenaior set involving sce(i), using new interations to replace old interaction to accomplish the original functions "
        + "with o(d).");
      strategyList.push("Notice:");
      strategyList.push("1.IS=IS U {int[O(d)/d]}/int representing replacing d with o(d) in int. ");
      break;
  }
  return strategyList;
}

export function deleteNode(sg: ScenarioGraph, node: Node) {
  const type = node.node_type;
  let nodeList: Node[];
  const lineList = sg.lineList;

  if (type === 'BehInt' || type === 'ExpInt' || type === 'ConnInt') {
    nodeList = sg.intNodeList;
  } else {
    nodeList = sg.ctrlNodeList;
  }

  for (let i = 0; i < nodeList.length; i++) {
    if (nodeList[i].node_type === node.node_type &&
      nodeList[i].node_no === node.node_no) {
      nodeList.splice(i, 1);
      break;
    }
  }

  if (lineList != null) {
    for (let i = lineList.length - 1; i >= 0; i--) {
      if ((lineList[i].fromNode.node_type === node.node_type && lineList[i].fromNode.node_no === node.node_no)
        || (lineList[i].toNode.node_type === node.node_type && lineList[i].toNode.node_no === node.node_no)) {
        lineList.splice(i, 1);
      }
    }
  }
}

export function deleteLine(sg: ScenarioGraph, lineName: string) {
  const lineList = sg.lineList;
  for (let i = 0; i < lineList.length; i++) {
    if ((lineList[i].line_type + lineList[i].line_no) === lineName) {
      lineList.splice(i, 1);
      break; // Added break for efficiency
    }
  }
}
