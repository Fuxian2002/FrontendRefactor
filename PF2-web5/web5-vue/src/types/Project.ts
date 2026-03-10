import { Constraint } from "./Constraint";
import { ContextDiagram } from "./ContextDiagram";
import { CtrlNode } from './CtrlNode';
import { Interface } from "./Interface";
import { Line } from './Line';
import { Node } from './Node';
import { Machine } from "./Machine";
import { Phenomenon } from "./Phenomenon";
import { ProblemDiagram } from "./ProblemDiagram";
import { ProblemDomain } from "./ProblemDomain";
import { Reference } from "./Reference";
import { Requirement } from "./Requirement";
import { ScenarioGraph } from './ScenarioGraph';
import { SubProblemDiagram } from './SubProblemDiagram';
import { IntentDiagram } from "./IntentDiagram";
import { Dependence } from "./Dependence";
import { Trace } from "./Trace";
import { ExternalEntity } from "./ExternalEntity";
import { Intent } from "./Intent";

export class Project {
  title: string;
  intentDiagram: IntentDiagram;
  contextDiagram: ContextDiagram;
  problemDiagram: ProblemDiagram;
  scenarioGraphList: ScenarioGraph[];
  fullScenarioGraph: ScenarioGraph;
  newScenarioGraphList: ScenarioGraph[];
  subProblemDiagramList: SubProblemDiagram[];
  traceList: Trace[];
  dataDependenceList: Dependence[];
  controlDependenceList: Dependence[];

  initProject(project: Project) {
    this.init(project.title, project);
    if (project.intentDiagram == null)
      this.initIntentDiagram()
    else {
      this.intentDiagram.system = project.intentDiagram.system;
      this.intentDiagram = project.intentDiagram;
      if (this.intentDiagram.externalEntityList == null)
        this.intentDiagram.externalEntityList = new Array<ExternalEntity>();
      if (this.intentDiagram.intentList == null)
        this.intentDiagram.intentList = new Array<Intent>();
      if (this.intentDiagram.constraintList == null)
        this.intentDiagram.constraintList = new Array<Constraint>();
      if (this.intentDiagram.interfaceList == null)
        this.intentDiagram.interfaceList = new Array<Interface>();
      if (this.intentDiagram.referenceList == null)
        this.intentDiagram.referenceList = new Array<Reference>();
    }
    if (project.contextDiagram == null)
      this.initContexDiagram()
    else {
      this.contextDiagram.machine = project.contextDiagram.machine;
      this.contextDiagram = project.contextDiagram;
      if (this.contextDiagram.interfaceList == null)
        this.contextDiagram.interfaceList = new Array<Interface>();
      if (this.contextDiagram.problemDomainList == null)
        this.contextDiagram.problemDomainList = new Array<ProblemDomain>();
    }

    if (project.problemDiagram == null) {
      console.log("this.project.problemDiagram==null");
      this.initProblemDiagram();
    } else {
      this.problemDiagram = project.problemDiagram;
      if (this.problemDiagram.requirementList == null)
        this.problemDiagram.requirementList = new Array<Requirement>();
      if (this.problemDiagram.constraintList == null)
        this.problemDiagram.constraintList = new Array<Constraint>();
      if (this.problemDiagram.referenceList == null)
        this.problemDiagram.referenceList = new Array<Reference>();
    }
    this.problemDiagram.contextDiagram = this.contextDiagram;

    console.log(project.scenarioGraphList);
    if (typeof (project.scenarioGraphList) == "undefined" || project.scenarioGraphList == null || project.scenarioGraphList.length == 0) {
      console.log("this.project.scenarioGraphList.length == 0");
      this.initScenarioDiagram();
    } else {
      this.scenarioGraphList = new Array<ScenarioGraph>();
      for (var i = 0; i < project.scenarioGraphList.length; i++) {
        this.scenarioGraphList[i] = project.scenarioGraphList[i];
        console.log('this.scenarioGraphList[', i, ']:', this.scenarioGraphList[i]);
        if (this.scenarioGraphList[i].intNodeList == null)
          this.scenarioGraphList[i].intNodeList = new Array<Node>();
        if (this.scenarioGraphList[i].ctrlNodeList == null)
          this.scenarioGraphList[i].ctrlNodeList = new Array<CtrlNode>();
        if (this.scenarioGraphList[i].lineList == null)
          this.scenarioGraphList[i].lineList = new Array<Line>();
      }
    }

    if (project.subProblemDiagramList == null || project.subProblemDiagramList.length == 0) {
      console.log("this.project.subProblemDiagramList.length == 0");
      this.initSubProblemDiagram();
    } else {
      this.subProblemDiagramList = new Array<SubProblemDiagram>();
      for (var i = 0; i < project.subProblemDiagramList.length; i++) {
        this.subProblemDiagramList[i] = project.subProblemDiagramList[i];
        if (this.subProblemDiagramList[i].problemDomainList == null)
          this.subProblemDiagramList[i].problemDomainList = new Array<ProblemDomain>();
        if (this.subProblemDiagramList[i].interfaceList == null)
          this.subProblemDiagramList[i].interfaceList = new Array<Interface>();
        if (this.subProblemDiagramList[i].constraintList == null)
          this.subProblemDiagramList[i].constraintList = new Array<Constraint>();
        if (this.subProblemDiagramList[i].referenceList == null)
          this.subProblemDiagramList[i].referenceList = new Array<Reference>();
      }
    }

  }

  setDescription(line: any) {
    let name = line.getName();
    let pheList = line.phenomenonList || []; // Assuming getPhenomenonList corresponds to this
    // a:M!{on},P!{off}
    let s = "";
    s = s + name + ":";
    let desList: [string, string][] = [];
    for (let phe of pheList) {
      let flag = false;
      for (let des of desList) {
        if (phe.phenomenon_from == des[0]) {
          des.push(phe.phenomenon_name);
          flag = true;
          break;
        }
      }
      if (!flag) {
        desList.push([phe.phenomenon_from, phe.phenomenon_name]);
      }
    }
    // console.log(desList);
    for (let des of desList) {
      s += des[0] + "!{";
      for (let item of des.slice(1)) {
        s += item + ",";
      }
      s = s.slice(0, -1);
      s += "},";
    }
    s = s.slice(0, -1);
    line.setDescription(s);
    return s;
  }

  init(title: string, project: any) {
    this.title = title;
    this.initIntentDiagram();
    this.initContexDiagram();
    this.initProblemDiagram();
  }

  initIntentDiagram() {
    this.intentDiagram = new IntentDiagram();
    this.intentDiagram.title = "intentDiagram";
    this.intentDiagram.externalEntityList = new Array<ExternalEntity>();
    this.intentDiagram.intentList = new Array<Intent>();
    this.intentDiagram.interfaceList = new Array<Interface>();
    this.intentDiagram.constraintList = new Array<Constraint>();
    this.intentDiagram.referenceList = new Array<Reference>();
  }

  initContexDiagram() {
    this.contextDiagram = new ContextDiagram();
    this.contextDiagram.title = "contextDiagram";
    this.contextDiagram.problemDomainList = new Array<ProblemDomain>();
    this.contextDiagram.interfaceList = new Array<Interface>();
  }

  initProblemDiagram() {
    this.problemDiagram = new ProblemDiagram();
    this.problemDiagram.title = "problemDiagram";
    this.problemDiagram.requirementList = new Array<Requirement>();
    this.problemDiagram.constraintList = new Array<Constraint>();
    this.problemDiagram.referenceList = new Array<Reference>();
    this.problemDiagram.contextDiagram = this.contextDiagram;
  }

  initScenarioDiagram() {
    this.scenarioGraphList = new Array<ScenarioGraph>();
    if (!this.problemDiagram || !this.problemDiagram.requirementList) return;
    for (var i = 0; i < this.problemDiagram.requirementList.length; i++) {
      this.scenarioGraphList[i] = new ScenarioGraph();
      let req = this.problemDiagram.requirementList[i].requirement_context;
      let title = "SG" + this.problemDiagram.requirementList[i].requirement_no + "-" + req;
      title = title.replace(" ", "_");
      title = title.replace("\n", "_");
      this.scenarioGraphList[i].title = title;
      this.scenarioGraphList[i].intNodeList = new Array<Node>();
      this.scenarioGraphList[i].ctrlNodeList = new Array<CtrlNode>();
      this.scenarioGraphList[i].lineList = new Array<Line>();
    }
  }

  initSubProblemDiagram() {
    this.subProblemDiagramList = new Array<SubProblemDiagram>();
    if (!this.problemDiagram || !this.problemDiagram.requirementList) return;
    for (var i = 0; i < this.problemDiagram.requirementList.length; i++) {
      this.subProblemDiagramList[i] = new SubProblemDiagram();
      let req = this.problemDiagram.requirementList[i].requirement_context;
      let title = "SG" + this.problemDiagram.requirementList[i].requirement_no + "-" + req;
      title = title.replace(" ", "_");
      title = title.replace("\n", "_");
      this.subProblemDiagramList[i].title = title;
      this.subProblemDiagramList[i].machine = this.contextDiagram.machine;
      this.subProblemDiagramList[i].requirement = this.problemDiagram.requirementList[i];
      this.subProblemDiagramList[i].problemDomainList = new Array<ProblemDomain>();
      this.subProblemDiagramList[i].interfaceList = new Array<Interface>();
      this.subProblemDiagramList[i].constraintList = new Array<Constraint>();
      this.subProblemDiagramList[i].referenceList = new Array<Reference>();
    }
  }

  getTitle() {
    return this.title;
  }
  setTitle(title: string) {
    this.title = title;
  }

  // Machine
  getMachine(): Machine {
    return this.contextDiagram.machine;
  }

  addMachine(name: string, shortName: string, x: number, y: number, w: number, h: number) {
    this.contextDiagram.machine = new Machine();
    this.contextDiagram.machine.machine_name = name;
    this.contextDiagram.machine.machine_shortName = shortName;
    this.contextDiagram.machine.machine_x = x;
    this.contextDiagram.machine.machine_y = y;
    this.contextDiagram.machine.machine_w = w;
    this.contextDiagram.machine.machine_h = h;
    if (this.problemDiagram && this.problemDiagram.contextDiagram) {
        this.problemDiagram.contextDiagram.machine = this.contextDiagram.machine;
    }
    return this.contextDiagram.machine;
  }

  changeMachine(name: string, shortName: string) {
    this.contextDiagram.machine.machine_name = name;
    this.contextDiagram.machine.machine_shortName = shortName;
    if (this.problemDiagram && this.problemDiagram.contextDiagram && this.problemDiagram.contextDiagram.machine) {
        this.problemDiagram.contextDiagram.machine.machine_name = name;
        this.problemDiagram.contextDiagram.machine.machine_shortName = shortName;
    }
    // this.scenarioGraphList = this.getScenarioGraphList(); // getScenarioGraphList is not defined in the source, likely returns this.scenarioGraphList
    return this.contextDiagram && this.problemDiagram;
  }

  changeMachinePositionNew(name: string, position: { x: number, y: number }) {
    console.log("changeMachinePosition")
    console.log(this.getMachine().machine_name)
    if (name == this.getMachine().machine_name) {
      this.contextDiagram.machine.machine_x = position.x;
      this.contextDiagram.machine.machine_y = position.y;
      if (this.problemDiagram && this.problemDiagram.contextDiagram && this.problemDiagram.contextDiagram.machine) {
        this.problemDiagram.contextDiagram.machine.machine_x = position.x;
        this.problemDiagram.contextDiagram.machine.machine_y = position.y;
      }
      return this.contextDiagram.machine && this.problemDiagram && this.scenarioGraphList && this.subProblemDiagramList;
    }
  }

  setMachine(machine: Machine) {
    this.contextDiagram.machine = machine;
    if (this.problemDiagram && this.problemDiagram.contextDiagram) {
        this.problemDiagram.contextDiagram.machine = machine;
    }
  }
  
  getScenarioGraphList() {
      return this.scenarioGraphList;
  }
}
