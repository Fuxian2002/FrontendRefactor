import { Component } from '@angular/core';
import * as $ from "jquery";
import { FileUploader } from 'ng2-file-upload';
import { Error } from './entity/Error';
import { Project } from './entity/Project';
import { DrawGraphService } from './service/draw-graph.service';
import { FileService } from './service/file.service';
import { ProjectService } from './service/project.service';
import { CommunicationService } from "./service/communication.service";
import {Util} from "./entity/Util";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'PF2-web1';
  errors: Error[];
  res: boolean;
  strategyList: string[];
  projects: string[];
  versions: string[];

  owls: string[];
  owlVersions: string[];
  owl: string;
  owlVersion: string;

  pOwls: string[];
  pOwlVersions: string[];
  pOwl: string;
  pOwlVersion: string;

  project: string;
  openedProject: Project;
  projectAddress: string;
  interval;
  tabList: string[];

  getErrors(errors: Error[]) {
    this.res = false;
    this.errors = errors;
    console.log(this.errors)
    for(var i = 0; i < this.errors.length; i++){
      if(this.errors[i].errorList.length > 0){
        this.showErrors();
      }
      if(this.errors[i].title != null){
        this.showErrors();
      }
    }
  }

  showErrors(){
    var popBox = document.getElementById("popBox");
    var popLayer = document.getElementById("popLayer");
    popBox.style.display = "block";
    popLayer.style.display = "block";
  }

  getProjects(projects: string[]) {
    //console.log("appopen");
    this.projects = projects;
    //console.log(projects.length);
    if (projects.length == 0) {
      alert("There is no project! You need import a new project!");
    } else {
      var popLayer = document.getElementById("popLayer");
      popLayer.style.display = "block";
      var popBox = document.getElementById('OpenProject');
      popBox.style.display = "block";
    }
  }

  getOwls(owls: string[]) {
    this.owls = owls;
    if (owls.length == 0) {
      alert("There is no owl! You need upload a owls!");
    } else {
      var popLayer = document.getElementById("popLayer");
      popLayer.style.display = "block";
      var popBox = document.getElementById('OpenOwl');
      popBox.style.display = "block";
    }
  }

  getPOwls(pOwls: string[]) {
    console.log("getOwls");
    this.pOwls = pOwls;
    //console.log(projects.length);
    if (pOwls.length == 0) {
      alert("There is no owl! You need upload a owls!");
    } else {
      var popLayer = document.getElementById("popLayer");
      popLayer.style.display = "block";
      var popBox = document.getElementById('OpenPOwl');
      popBox.style.display = "block";
    }
  }

  search(project: string) {
    this.project = project;
    //console.log(project);
    this.fileService.searchVersion(project).subscribe(
      versions => {
        this.versions = versions;
      }
    )
  }
  searchOwlVersions(owl: string) {
    this.owl = owl;
    // console.log("searchOwlVersions");
    // this.fileService.searchOwlVersion(owl).subscribe(
    //   versions => {
    //     this.owlVersions = versions;
    //   }
    // )
  }

  searchPOwlVersions(pOwl: string) {
    this.pOwl = pOwl;
    // console.log("searchOwlVersions");
    // this.fileService.searchOwlVersion(owl).subscribe(
    //   versions => {
    //     this.owlVersions = versions;
    //   }
    // )
  }

  //chose project to open
  open() {
    // var form = document.getElementById('version');
    // console.log(form.textContent);
    var version = $("input[id='version']:checked").val()
    if(version == undefined || version == null || version == '') {
      version = 'undefined'
    }
    if (this.project != undefined) {
      // window.location.assign('/' + this.project)
      var that = this
      that.interval = setInterval(function () {
        clearInterval(that.interval)
        that.dg_service.getProject(that.project, version.toString());
        console.log('this.project')
        console.log(that.project)
        console.log('version')
        console.log(version)
        that.communicationService.projectname = that.project;
        that.communicationService.version = version.toString();
      }, 1500)
    }

    this.projectService.stepChange(1);
    this.showContextDiagram();
    this.close('OpenProject');
  }

  initBoard() {
    // console.log("new Project");
    var that = this;
    this.tabList = [];
    if (that.dg_service.project.scenarioGraphList) {
      for (let sg of that.dg_service.project.scenarioGraphList) {
        that.tabList.push(sg.title);
      }
    }
  }

  showContextDiagram(){
    document.getElementById("content1").style.display = 'block';
    document.getElementById("content2").style.display = 'none';
    this.initBoard();
    if (this.tabList) {
      for (var i = 0; i < this.tabList.length; i++) {
        const id = this.tabList[i] + 'M';
        document.getElementById(id).style.display = 'none';
      }
    }
  }

  showScenarioIcon() {
    let problemIcon = document.getElementById("problem-icon");
    let scenarioIcon = document.getElementById("scenario-icon");
    problemIcon.style.display = "none";
    scenarioIcon.style.display = "block";
  }

  //chose owl to open
  //将后台的owl文件放到当前项目的目录下，并获取问题领域
  openOwl() {
    if (this.owl != undefined) {
      var that = this
      this.owlAdd = this.dg_service.projectAddress
      this.projectService.sendOwlAdd(this.owlAdd)
      this.projectService.sendEOntName(this.owl + ".owl")
      that.fileService.moveEOwl(that.dg_service.projectAddress, that.owl, that.owlVersion).subscribe(
        res => {
          if (res) {
            that.fileService.getProblemDomains(that.owlAdd, that.owl).subscribe(
              nodes => {
                that.dg_service.ontologyEntities = nodes;
                that.dg_service.initProjectWithOntology(that.dg_service.project.title)
              })
            this.projectService.stepChange(1);
          } else {
            alert("there are some errors in this owl file. please upload a new owl file.")
          }
        }
      )
    }
    this.close('OpenOwl');
  }


  //chose owl to open
  //将后台的powl文件放到当前项目的目录下，并通知topbar，可以show
  openPOwl() {
    if (this.dg_service.projectAddress == null) {
      alert("please new a project")
      return
    }
    if (this.pOwl != undefined) {
      this.owlAdd = this.dg_service.projectAddress
      this.projectService.sendPOntName(this.pOwl + ".owl")
      this.fileService.movePOwl(this.dg_service.projectAddress, this.pOwl, this.pOwlVersion).subscribe(
        res => {
          if (!res) alert("move powl error!")
        }
      )
    }
    this.close('OpenPOwl');
  }

  close(id: string) {
    //console.log("close");
    document.getElementById(id).style.display = "none"
    document.getElementById("popLayer").style.display = "none"
  }

  getStrategy(errMsg: string) {
    this.strategyList = this.projectService.getStrategy(errMsg)
    document.getElementById('Strategy').style.display = "block"
  }

  pOntologyShow: boolean;
  eOntologyShow: boolean;
  nodes: string[];
  eNodes: string[];
  subnodes: string[];
  subnodes1: string[];
  clicknode = null;
  clicknode1 = null;
  owlAdd: string;
  powlName: string;
  eowlName: string;
  type: string;
  sameNameFlag: boolean;
  owlName: string;

  uploader: FileUploader = new FileUploader({
    method: "POST",
    itemAlias: "xmlFile",
    autoUpload: false,
  });
  searchPOntNodes(node: string) {
    this.clicknode = node;
    //console.log(node);
    this.fileService.getNodes(this.owlAdd, this.powlName, node, "powl").subscribe(
      subnodes => {
        this.subnodes = subnodes;
      }
    )
  }

  searchEOntNodes(node: string) {
    this.clicknode = node;
    //console.log(node+"searchEOntNodes");
    this.fileService.getNodes(this.owlAdd, this.eowlName, node, "eowl").subscribe(
      subnodes => {
        this.subnodes = subnodes;
      }
    )
  }

  searchEOntNodes1(node: string) {
    this.clicknode1 = node;
    //console.log(node+"searchEOntNodes1");
    let subscription = this.fileService.getNodes(this.owlAdd, this.eowlName, node, "eowl").subscribe(
      subnodes1 => {
        this.subnodes1 = subnodes1;
        subscription.unsubscribe();
      }
    )
  }
  username
  constructor(
    private projectService: ProjectService,
    private fileService: FileService,
    private dg_service: DrawGraphService,
    private communicationService: CommunicationService
  ) {
    // this.login();
    if (document.cookie != '') {
      console.log('document.cookie:', document.cookie);
      //this.username = JSON.parse(document.cookie)['username'];
      this.username = document.cookie.slice(document.cookie.indexOf('=') + 1);
      //this.username = jQuery.parseJSON(document.cookie)['username']
    }

    fileService.newProEmmited$.subscribe(
      res => {
        //console.log(res);
        if (res == true) {
          let popLayer = document.getElementById('popLayer');
          popLayer.style.display = "block";
          let ele = document.getElementById('projectPopBox');
          //console.log(ele);
          ele.style.display = "block";
        }

      })

    this.projectService.owlAddEmitted$.subscribe(
      owlAdd => {
        this.owlAdd = owlAdd;
      })
    this.projectService.ontNameAddEmitted$.subscribe(
      ontName => {
        this.powlName = ontName;
      })
    this.projectService.eOntNameAddEmitted$.subscribe(
      eOntName => {
        this.eowlName = eOntName;
      }
    )
    this.projectService.pOntNameAddEmitted$.subscribe(
      pOntName => {
        this.powlName = pOntName;
      }
    )
    this.pOntologyShow = false;
    this.eOntologyShow = false;
    this.projectService.pNodesToDisplayEmitted$.subscribe(
      nodes => {
        this.nodes = nodes;
        //console.log("now in app.component");
        for (let node of this.nodes) {
          //console.log(node);
        }
      }
    )
    this.projectService.eNodesToDisplayEmitted$.subscribe(
      nodes => {
        this.eNodes = nodes;
        //console.log("now in app.component");
        for (let node of this.eNodes) {
          //console.log(node);
        }
      }
    )
    this.projectService.pOntShowEmitted$.subscribe(
      on => {
        this.pOntologyShow = on;
        //console.log(this.pOntologyShow+"pOntologyShow is ok");
        if (this.pOntologyShow) {
          //console.log(this.pOntologyShow+"pOntologyShow is ok1");
          var popBoxP = document.getElementById('pOntShow');
          //console.log(popBoxP);
          popBoxP.style.display = "block";
        }
      }
    )
    this.projectService.eOntShowEmitted$.subscribe(
      on => {
        this.eOntologyShow = on;
        //console.log(this.eOntologyShow+"eOntologyShow is ok");
        if (this.eOntologyShow) {
          //console.log(this.eOntologyShow+"eOntologyShow is ok1");
          var popBoxE = document.getElementById('eOntShow');
          //console.log(popBoxE);
          popBoxE.style.display = "block";
        }
      })
  }

  //new project
  confirm() {
    let selectedDiv = document.getElementById('projectPopBox');
    let description = (selectedDiv.getElementsByClassName("description")[0] as any).value;
    if (description == "") {
      alert("The project title can't be null");
    } else if (description.indexOf(" ") != -1) {
      alert("The project title can't contains space!");
    }
    else {
      //判断是否有重名项目
      let flag = false
      this.fileService.searchProject().subscribe(
        projects => {
          for (let pro of projects) {
            if (pro == description) {
              alert(description + " already exist!")
              flag = true
              break
            }
          }
          if (!flag) {
            this.dg_service.register(description);
            this.dg_service.initProject(description);
            this.dg_service.initPapers();
            this.closePopEdit();
          }
        }
      )
    }
  }

  closeBox() {
    this.closePopEdit();
  }
  closePopEdit() {
    //let selectedType = this.dg_service.getElementType(this.dg_service.selectedElement);
    document.getElementById("projectPopBox").style.display = "none";
    document.getElementById("popLayer").style.display = "none";
  }

}
