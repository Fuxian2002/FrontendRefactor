import { defineStore } from 'pinia';
import type { Project } from '../types/Project';
import type { ScenarioGraph } from '../types/ScenarioGraph';
import type { SubProblemDiagram } from '../types/SubProblemDiagram';
import type { Phenomenon } from '../types/Phenomenon';

export const useProjectStore = defineStore('project', {
  state: () => ({
    project: null as Project | null,
    step: 0,
    problemDiagram: null as any, // Type appropriately if ProblemDiagram is complex
    phenomenon: null as Phenomenon | null,
    pOntShow: false,
    eOntShow: false,
    pNodesToDisplay: [] as string[],
    eNodesToDisplay: [] as string[],
    owlAdd: '',
    uploadOWL: false,
    ontName: '',
    eOntName: '',
    pOntName: '',
    spdList: [] as SubProblemDiagram[],
    scenarioList: [] as ScenarioGraph[],
    fullScenarioGraph: null as ScenarioGraph | null,
    newScenarioList: [] as ScenarioGraph[],
    reqPheList: [] as any[], // For References section
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
      
      // Deduplicate based on phenomenon_no
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
    setPOntShow(show: boolean) {
      this.pOntShow = show;
    },
    setEOntShow(show: boolean) {
      this.eOntShow = show;
    },
    setPNodesToDisplay(nodes: string[]) {
      this.pNodesToDisplay = nodes;
    },
    setENodesToDisplay(nodes: string[]) {
      this.eNodesToDisplay = nodes;
    },
    setOwlAdd(owlAdd: string) {
      this.owlAdd = owlAdd;
    },
    setUploadOWL(upload: boolean) {
      this.uploadOWL = upload;
    },
    setOntName(name: string) {
      this.ontName = name;
    },
    setEOntName(name: string) {
      this.eOntName = name;
    },
    setPOntName(name: string) {
      this.pOntName = name;
    },
    setSpdList(spdList: SubProblemDiagram[]) {
      this.spdList = spdList;
    },
    setScenarioList(scenarioList: ScenarioGraph[]) {
      this.scenarioList = scenarioList;
    },
    setFullScenarioGraph(graph: ScenarioGraph) {
      this.fullScenarioGraph = graph;
    },
    setNewScenarioList(list: ScenarioGraph[]) {
      this.newScenarioList = list;
    }
  }
});
