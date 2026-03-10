import { defineStore } from 'pinia';
import type { Project } from '../types/Project';
import type { ScenarioGraph } from '../types/ScenarioGraph';
import type { SubProblemDiagram } from '../types/SubProblemDiagram';
import type { Phenomenon } from '../types/Phenomenon';

export const useProjectStore = defineStore('project', {
  state: () => ({
    project: null as Project | null,
    step: 1,
    problemDiagram: null as any,
    phenomenon: null as Phenomenon | null,
    spdList: [] as SubProblemDiagram[],
    scenarioList: [] as ScenarioGraph[],
    reqPheList: [] as any[],
    traceList: [] as any[],
    dataDependenceList: [] as any[],
    controlDependenceList: [] as any[],
  }),
  getters: {
    allPhenomena: (state) => {
      if (!state.project) return [];
      const res: any[] = [];
      const getPhenomenon1 = (list: any[]) => {
        if (!list) return;
        for (let link of list) {
          if (link.phenomenonList) {
            for (let phe of link.phenomenonList) {
              res.push(phe);
            }
          }
        }
      };
      
      if (state.project.contextDiagram) {
        getPhenomenon1(state.project.contextDiagram.interfaceList);
      }
      if (state.project.problemDiagram) {
        getPhenomenon1(state.project.problemDiagram.referenceList);
        getPhenomenon1(state.project.problemDiagram.constraintList);
      }
      
      const uniqueRes: any[] = [];
      const seen = new Set();
      for (const item of res) {
        if (!seen.has(item.phenomenon_no)) {
          seen.add(item.phenomenon_no);
          uniqueRes.push(item);
        }
      }
      return uniqueRes;
    }
  },
  actions: {
    setProject(project: Project) {
      this.project = project;
    },
    setReqPheList(list: any[]) {
      this.reqPheList = list;
    },
    setStep(step: number) {
      this.step = step;
    },
    setProblemDiagram(problemDiagram: any) {
      this.problemDiagram = problemDiagram;
    },
    setPhenomenon(phenomenon: Phenomenon) {
      this.phenomenon = phenomenon;
    },
    setSpdList(spdList: SubProblemDiagram[]) {
      this.spdList = spdList;
    },
    setScenarioList(scenarioList: ScenarioGraph[]) {
      this.scenarioList = scenarioList;
    },
    setTraceList(list: any[]) {
      this.traceList = list;
    },
    setDataDependenceList(list: any[]) {
      this.dataDependenceList = list;
    },
    setControlDependenceList(list: any[]) {
      this.controlDependenceList = list;
    }
  }
});
