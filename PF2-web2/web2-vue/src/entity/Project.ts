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
import {IntentDiagram} from "./IntentDiagram";
import {Dependence} from "./Dependence";
import {Trace} from "./Trace";
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
    if(typeof(project.scenarioGraphList)=="undefined" || project.scenarioGraphList == null || project.scenarioGraphList.length == 0) {
      console.log("this.project.scenarioGraphList.length == 0");
      this.initScenarioDiagram();
    } else {
      this.scenarioGraphList = new Array<ScenarioGraph>();
      for (var i = 0; i < project.scenarioGraphList.length; i++) {
        this.scenarioGraphList[i] = project.scenarioGraphList[i];
        //this.scenarioGraphList.push(project.scenarioGraphList[i]);
        console.log('this.scenarioGraphList[' , i, ']:', this.scenarioGraphList[i]);
        if(this.scenarioGraphList[i].intNodeList == null)
          this.scenarioGraphList[i].intNodeList = new Array<Node>();
        if(this.scenarioGraphList[i].ctrlNodeList == null)
          this.scenarioGraphList[i].ctrlNodeList = new Array<CtrlNode>();
        if(this.scenarioGraphList[i].lineList == null)
          this.scenarioGraphList[i].lineList = new Array<Line>();
      }
    }

    if(project.subProblemDiagramList == null || project.subProblemDiagramList.length == 0) {
      console.log("this.project.subProblemDiagramList.length == 0");
      this.initSubProblemDiagram();
    } else {
      this.subProblemDiagramList = new Array<SubProblemDiagram>();
      for (var i = 0; i < project.subProblemDiagramList.length; i++) {
        this.subProblemDiagramList[i] = project.subProblemDiagramList[i];
        if(this.subProblemDiagramList[i].problemDomainList == null)
          this.subProblemDiagramList[i].problemDomainList = new Array<ProblemDomain>();
        if(this.subProblemDiagramList[i].interfaceList == null)
          this.subProblemDiagramList[i].interfaceList = new Array<Interface>();
        if(this.subProblemDiagramList[i].constraintList == null)
          this.subProblemDiagramList[i].constraintList = new Array<Constraint>();
        if(this.subProblemDiagramList[i].referenceList == null)
          this.subProblemDiagramList[i].referenceList = new Array<Reference>();
      }
    }

  }
  setDescription(line) {
    let name = line.getName();
    let pheList = line.getPhenomenonList();
    //a:M!{on},P!{off}
    let s = "";
    s = s + name + ":";
    let s1 = "";
    let s2 = "";
    let desList = [];
    for (let phe of pheList) {
      let flag = false;
      for (let des of desList) {
        if (phe.from == des[0]) {
          des.push(phe.name);
          flag = true;
          break;
        }
      }
      if (!flag) {
        desList.push([phe.from, phe.name]);
      }
    }
    //console.log(desList);
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

  init(title, project) {
    this.title = title;
    // console.log(project)
    this.initIntentDiagram();
    this.initContexDiagram();
    this.initProblemDiagram();
    // if(this.problemDiagram.requirementList.length > 0) {
    //   this.initScenarioDiagram();
    // }
    // if(this.problemDiagram.requirementList.length > 0) {
    //   this.initSubProblemDiagram();
    // }
    // this.initScenarioDiagram();
    // this.initSubProblemDiagram();
    // if (project.scenarioGraphList) {
		// 	for (var i = 0; i < project.scenarioGraphList.length; i++) {
    //     this.initScenarioDiagram(i);
    //   }
    // }
    // if (project.subProblemDiagramList) {
		// 	for (var i = 0; i < project.subProblemDiagramList.length; i++) {
    //     this.initSubProblemDiagramList(i);
    //   }
		// }
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
    // this.contextDiagram.machine = new Machine();
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
  // initScenarioDiagram(index) {
  //   this.scenarioGraphList[index] = new ScenarioGraph();
  //   let req = this.problemDiagram.requirementList[index].requirement_context;
  //   let title = "SG" + this.problemDiagram.requirementList[index].requirement_no + "-" + req;
  //   title = title.replace(" ", "_");
  //   title = title.replace("\n", "_");
  //   this.scenarioGraphList[index].title = title;
  //   this.scenarioGraphList[index].intNodeList = new Array<Node>();
  //   this.scenarioGraphList[index].ctrlNodeList = new Array<CtrlNode>();
  //   this.scenarioGraphList[index].lineList = new Array<Line>();
  // }
  initScenarioDiagram() {
    this.scenarioGraphList = new Array<ScenarioGraph>();
    if (!this.problemDiagram || !this.problemDiagram.requirementList) return;
    
    for(var i = 0; i < this.problemDiagram.requirementList.length; i++) {
      this.scenarioGraphList[i] = new ScenarioGraph();
      let req = this.problemDiagram.requirementList[i].requirement_context;
      // Format title: SG[No]-[Context]
      let title = "SG" + this.problemDiagram.requirementList[i].requirement_no + "-" + req;
      // title = title.replace(" ", "_"); // Don't replace spaces, match original req context
      // title = title.replace("\n", "_");
      this.scenarioGraphList[i].title = title;
      this.scenarioGraphList[i].requirement = req; // Explicitly set requirement
      this.scenarioGraphList[i].intNodeList = new Array<Node>();
      this.scenarioGraphList[i].ctrlNodeList = new Array<CtrlNode>();
      this.scenarioGraphList[i].lineList = new Array<Line>();
    }
  }
  // initSubProblemDiagramList(index) {
  //   this.subProblemDiagramList[index] = new SubProblemDiagram();
  //   let req = this.problemDiagram.requirementList[index].requirement_context;
  //   let title = "SG" + this.problemDiagram.requirementList[index].requirement_no + "-" + req;
  //   title = title.replace(" ", "_");
  //   title = title.replace("\n", "_");
  //   this.subProblemDiagramList[index].title = title;
  //   this.subProblemDiagramList[index].machine = this.contextDiagram.machine;
  //   this.subProblemDiagramList[index].requirement = this.problemDiagram.requirementList[index];
  //   this.subProblemDiagramList[index].problemDomainList = new Array<ProblemDomain>();
  //   this.subProblemDiagramList[index].interfaceList = new Array<Interface>();
  //   this.subProblemDiagramList[index].constraintList = new Array<Constraint>();
  //   this.subProblemDiagramList[index].referenceList = new Array<Reference>();
  // }
  initSubProblemDiagram() {
    this.subProblemDiagramList = new Array<SubProblemDiagram>();
    for(var i = 0; i < this.problemDiagram.requirementList.length; i++) {
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
  setTitle(title) {
    this.title = title;
  }
  //Machine
  getMachine(): Machine {
    return this.contextDiagram.machine;
  }
  addMachine(name, shortName, x, y, w, h) {
    this.contextDiagram.machine = new Machine();
    this.contextDiagram.machine.machine_name = name;
    this.contextDiagram.machine.machine_shortName = shortName;
    this.contextDiagram.machine.machine_x = x;
    this.contextDiagram.machine.machine_y = y;
    this.contextDiagram.machine.machine_w = w;
    this.contextDiagram.machine.machine_h = h;
    this.problemDiagram.contextDiagram.machine = this.contextDiagram.machine;
    return this.contextDiagram.machine;
  }
  changeMachine(name, shortName) {
    this.contextDiagram.machine.machine_name = name;
    this.contextDiagram.machine.machine_shortName = shortName;
    this.problemDiagram.contextDiagram.machine.machine_name = name;
    this.problemDiagram.contextDiagram.machine.machine_shortName = shortName;
    this.scenarioGraphList = this.getScenarioGraphList();
    return this.contextDiagram && this.problemDiagram;
  }
  changeMachinePositionNew(name, position) {
    console.log("changeMachinePosition")
    console.log(this.getMachine().machine_name)
    if (name == this.getMachine().machine_name) {
      this.contextDiagram.machine.machine_x = position.x;
      this.contextDiagram.machine.machine_y = position.y;
      this.problemDiagram.contextDiagram.machine.machine_x = position.x;
      this.problemDiagram.contextDiagram.machine.machine_y = position.y;
      return this.contextDiagram.machine && this.problemDiagram && this.scenarioGraphList && this.subProblemDiagramList;
    }
  }
  setMachine(machine) {
    this.contextDiagram.machine = machine;
    this.problemDiagram.contextDiagram.machine = machine;
  }

  //ProblemDomain
  getProblemDomainList() {
    return this.contextDiagram.problemDomainList;
  }
  addProblemDomain(no, name, shortName, type, property, x, y, w, h) {
    let problemDomain = new ProblemDomain();
    problemDomain.problemdomain_no = no;
    problemDomain.problemdomain_name = name;
    problemDomain.problemdomain_shortname = shortName;
    problemDomain.problemdomain_type = type;
    problemDomain.problemdomain_property = property;
    problemDomain.problemdomain_x = x;
    problemDomain.problemdomain_y = y;
    problemDomain.problemdomain_w = w;
    problemDomain.problemdomain_h = h;
    problemDomain.phes = new Array<Phenomenon>();
    this.contextDiagram.problemDomainList.push(problemDomain);
    //console.log(this.project.contextDiagram.problemDomainList);
    return problemDomain;
  }
  changeProblemDomain1(old, new1) {
    console.log("changeProblemDomain1")
    var i = 0;
    for (let item of this.getProblemDomainList()) {
      if (item.problemdomain_name == old.getName()) {
        this.getProblemDomainList()[i] = new1;
        console.log(this.problemDiagram);
        return;
      }
      i += 1;
    }
  }
  changeProblemDomain(
    old,
    description,
    shortName,
    domainType,
    physicalProperty
  ) {
    for (let item of this.getProblemDomainList()) {
      if (item.getName() == old.getName()) {
        item.setName(description);
        item.setShortName(shortName);
        item.setType(domainType);
        item.setProperty(physicalProperty);
        break;
      }
    }
  }
  changeProblemDomainPosition(name, position) {
    console.log("changeProblemDomainPosition")
    //let name = this.selectedElement.attr('root').title;
    for (let item of this.contextDiagram.problemDomainList) {
      if (item.problemdomain_name == name) {
        item.problemdomain_x = position.x;
        item.problemdomain_y = position.y;
        return true;
      }
    }
    return false;
  }

  //Requirement
  getRequirementList() {
    return this.problemDiagram.requirementList;
  }
  addRequirement(no, context, x, y, w, h) {
    let requirement = new Requirement();
    requirement.requirement_no = no;
    requirement.requirement_context = context;
    requirement.requirement_x = x;
    requirement.requirement_y = y;
    requirement.requirement_w = w;
    requirement.requirement_h = h;
    this.problemDiagram.requirementList.push(requirement);
    return requirement;
  }
  changeRequirement1(old, new1) {
    var i = 0;
    for (let item of this.getRequirementList()) {
      if (item.requirement_context == old.getName()) {
        this.getRequirementList()[i] = new1;
        return;
      }
      i++;
    }
  }
  changeRequirement(old, description) {
    for (let item of this.getRequirementList()) {
      if (item.getName() == old.getName()) {
        item.setName(description);
        item.setShortName(description);
        break;
      }
    }
  }
  changeRequirementPosition(name, position) {
    for (let item of this.getRequirementList()) {
      if (item.requirement_context == name) {
        item.requirement_x = position.x;
        item.requirement_y = position.y;
        return true;
      }
    }
    return false;
  }
  deleteRequirement(requirement: Requirement) {
    let name = requirement.requirement_context;
    let list = this.problemDiagram.requirementList;
    let i = list.length - 1;
    for (; i >= 0; i--) {
      let item = list[i];
      if (item.requirement_context == name) {
        list.splice(i, 1);
        break;
      }
      //console.log(item.requirement_context + '!=' + name);
    }
  }

  //link
  deleteRelatedLink(shortName) {
    console.log("deleteRelatedLink,shortName=" + shortName);
    let i = this.getReferenceList().length - 1;
    for (; i >= 0; i--) {
      let reference = this.problemDiagram.referenceList[i];
      if (
        reference.reference_from == shortName ||
        reference.reference_to == shortName
      ) {
        console.log(reference);
        let name = reference.reference_name;
        this.problemDiagram.referenceList.splice(i, 1);
      }
    }
    i = this.problemDiagram.constraintList.length - 1;
    for (; i >= 0; i--) {
      let constraint = this.problemDiagram.constraintList[i];
      if (
        constraint.constraint_from == shortName ||
        constraint.constraint_to == shortName
      ) {
        console.log(constraint);
        this.problemDiagram.constraintList.splice(i, 1);
      }
    }
    i = this.contextDiagram.interfaceList.length - 1;
    for (; i >= 0; i--) {
      let my_interface = this.contextDiagram.interfaceList[i];
      if (
        my_interface.interface_from == shortName ||
        my_interface.interface_to == shortName
      ) {
        console.log(my_interface);
        this.contextDiagram.interfaceList.splice(i, 1);
      }
    }
  }

  //Interface
  getInterfaceList() {
    return this.contextDiagram.interfaceList;
  }
  addInterface(int: Interface) {
    this.contextDiagram.interfaceList.push(int);
  }
  changeInterface(old, new1) {
    let i = 0;
    for (let item of this.getInterfaceList()) {
      if (item.interface_name == old.getName()) {
        this.getInterfaceList()[i] = new1;
        return;
      }
      i += 1;
    }
  }
  deleteInterface(int: Interface) {
    let no = int.interface_no;
    let list = this.contextDiagram.interfaceList;
    let i = 0;
    for (let item of list) {
      if (item.interface_no == no) {
        list.splice(i, 1);
        break;
      }
      i++;
    }
  }

  //Constraint
  getConstraintList() {
    return this.problemDiagram.constraintList;
  }

  addConstraint(con) {
    this.problemDiagram.constraintList.push(con);
  }
  changeConstraint(old, new1) {
    let i = 0;
    for (let item of this.getConstraintList()) {
      if (item.constraint_name == old.getName()) {
        this.getConstraintList()[i] = new1;
        return;
      }
      i += 1;
    }
  }
  deleteConstraint(con) {
    let no = con.constraint_no;
    let list = this.problemDiagram.constraintList;
    let i = 0;
    for (let item of list) {
      if (item.constraint_no == no) {
        list.splice(i, 1);
        break;
      }
      i++;
    }
  }

  //Reference
  getReferenceList() {
    return this.problemDiagram.referenceList;
  }
  addReference(ref) {
    this.problemDiagram.referenceList.push(ref);
  }
  changeReference(old, new1) {
    // let i = 0;
    // for (let item of this.getReferenceList()) {
    //   console.log(this.getReferenceList());
    //   console.log(old.getName())
    //   if (item.reference_name == old.getName()) {
    //     console.log(item)
    //     this.getReferenceList()[i] = new1;
    //     return;
    //   }
    // }
    for(var i = 0; i < this.getReferenceList().length; i++){
      var item = this.getReferenceList()[i];
      if (item.reference_name == old.getName()) {
        this.getReferenceList()[i] = new1;
        return;
      }
    }
  }
  getDescription(name, pheList) {
    //a:M!{on},P!{off}
    let s = "";
    s = s + name + ":";
    let s1 = "";
    let s2 = "";
    let desList = [];
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
    //console.log(desList);
    for (let des of desList) {
      s += des[0] + "!{";
      for (let item of des.slice(1)) {
        s += item + ",";
      }
      s = s.slice(0, -1);
      s += "},";
    }
    s = s.slice(0, -1);
    //console.log(s);
    return s;
  }
  deleteReference(ref: Reference) {
    let no = ref.reference_no;
    let list = this.problemDiagram.referenceList;
    let i = 0;
    for (let item of list) {
      if (item.reference_no == no) {
        list.splice(i, 1);
        break;
      }
      i++;
    }
  }
  getScenarioGraphList(){
    return this.scenarioGraphList;
  }
}
