import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Phenomenon } from '../entity/Phenomenon';
import { Project } from '../entity/Project';
import { Util } from '../entity/Util';
import { RequirementPhenomenon } from '../entity/RequirementPhenomenon';
import { DrawGraphService } from '../service/draw-graph.service';
import { FileService } from '../service/file.service';
import { ProjectService } from '../service/project.service';
import { FileUploader } from 'ng2-file-upload';
import { VerficationService } from '../service/verfication.service';
import { CommunicationService } from "../service/communication.service";

import Swal from 'sweetalert2/dist/sweetalert2.js';

import {StrategyAdvice} from "../entity/StrategyAdvice";
import {TopbarComponent} from "../topbar/topbar.component";
import {el} from "@angular/platform-browser/testing/src/browser_util";
import {ProjectRecord} from "../entity/ProjectRecord";
import {CheckTraceBody} from "../entity/CheckTraceBody";
import {isNewline} from "codelyzer/angular/styles/cssLexer";
import {Trace} from "../entity/Trace";
import {Dependence} from "../entity/Dependence";
// import 'sweetalert2/src/sweetalert2.scss';
@Component({
  selector: 'app-rightbar',
  templateUrl: './rightbar.component.html',
  styleUrls: ['./rightbar.component.css']
})
export class RightbarComponent implements OnInit {
  projectAddress: string;
  project: Project;
  pheList: Phenomenon[];

  reqPheList: RequirementPhenomenon[];
  step = 2;

  util = new Util();

  open1 = false;
  open2 = false;
  open3 = false;
  open4 = false;
  open5 = false;
  strategyAdviceIdx = 0;
  problemIcon;
  scenarioIcon;
  tabList: any;
  type: string;
  owlAdd: string;
  eowlName: string;
  sameNameFlag: boolean;
  owlName: string;
  uploadOWL: boolean;

  setOpen1(): void {
    this.open1 = !this.open1;
  }
  setOpen2(): void {
    this.open2 = !this.open2;
  }
  setOpen3(): void {
    this.open3 = !this.open3;
  }
  setOpen4(): void {
    this.open4 = !this.open4;
  }
  setOpen5(): void {
    this.open5 = !this.open5;
  }
  @Output() errorsEvent = new EventEmitter<Error[]>();
  sendErrors(errors: Error[]) {
    this.errorsEvent.emit(errors);
  }

  back() {
    if (this.step == 0) {
      this.step = 5;
    }
    if (this.step != 1) {
      this.step = this.step - 1;
    }
    this.projectService.stepChange(this.step);
  }
  check() {
    //console.log(this.step);
    var userName = this.util.getQueryVariable('username');
    console.log('userName:', userName, '  this.step:', this.step);
    switch (this.step) {
      case 1:
        // 打开项目
        // 1:Read the problem diagram
        if(this.project.traceList == undefined || this.project.traceList == null || this.project.traceList.length == 0) {
          this.communicationService.traces = new Array();
          this.project.traceList = new Array();
          var that = this;
          this.projectService.initTrace(that.project).subscribe(initTrace => {
            for(var i=0;i<initTrace.length;i+=1) {
              that.project.traceList.push(initTrace[i]);
            }
          });
        }
        this.communicationService.traces = this.project.traceList;
        this.communicationService.tracesToStr(this.communicationService.traces);
        this.communicationService.dataDependencies = this.project.dataDependenceList;
        this.communicationService.dataDependenciesToStr();
        this.communicationService.controlDependencies = this.project.controlDependenceList;
        this.communicationService.controlDependenciesToStr();
        this.step = 2;
        this.projectService.stepChange(this.step);
        console.log('this.communicationService.code:', this.communicationService.code);
        console.log('this.communicationService.traces:', this.communicationService.traces);
        //console.log('this.communicationService.dataDependencies:', this.communicationService.dataDependencies);
        //console.log('this.communicationService.controlDependencies:', this.communicationService.controlDependencies);
        break;
      case 2:
        if(this.project.scenarioGraphList == undefined || this.project.scenarioGraphList == null || this.project.scenarioGraphList.length == 0) {
          alert('无情景图，无法进行问题解耦！');
          break;
        }
        this.projectService.ckeckStrategy(this.project).subscribe(
          advice => {
            console.log('advice:', advice);
            var htmlLabel = '';
            if(advice.length === 0) {
              const content = '没有解耦建议';
              htmlLabel = '<div style="text-align: center; font-size: 20px; margin-bottom: 10px">' + content + '</div>';
              Swal.fire({
                title: '策略检查',
                icon: 'info',
                showConfirmButton: true,
                confirmButtonText: '确定',
                html: htmlLabel,
                width: 1000,
              });
            }
            else {
              if (this.strategyAdviceIdx < advice.length) {
                const strategyDict = {
                  '策略1：独立设备操作分离模式': ['该策略主要针对设备操作之间不存在约束关系的情况，旨在将没有约束关系的需求解耦开。' +
                  '例如，下图中，“加电(Power-on)” 需求要求分别对两个设备Device1和Device2进行加电操作。在其情景图中' +
                  '这两个加电操作是并发关系，没有约束，因此可以将该需求拆分为两个子需求，即 “设备1加电” 和 “设备2加电”', 'assets/strategyExamples/strategy1.png'],

                  '策略2：设备操作融合和抽象模式': ['该策略主要针对多个需求要求软件控制器向同一设备发起多种不同操作的情况，旨在通过抽象，将与设备交互的需求单独独立出来。' +
                  '例如，下图中，"设备3加电(Device3 Power-on)”需求对Device3进行加电操作，“设备3断电(Device3 Power-off)”需求对Device3进行断电操作。' +
                  '针对这种情形，引入“设备3控制输出(D3 Control Output)”需求，以及“设备3指令(D3 Instruction)”数据存储领域，其中“设备3控制输出”要求将指令转换为信号，' +
                  '“设备3指令"领域用于存储当前要转换的指令。', 'assets/strategyExamples/strategy2.png'],

                  '策略3：数据生产消费分离模式': ['该策略依据生产者消费者模型，在数据的获取与使用之间增加 “数据存储” 这一设计领域，使得生产者产生的数据可以存储其中，在消费时可以从其中直接读取。' +
                  '例如，下图中，“设备4数据生产(Device4 Data Production)” 需求从设备4(Device4)采集数据，“设备4数据消费(Device4 Data Consumption)” 需求使用设备4数据，' +
                  '并根据设备4数据的内容来判断设备5(Device5)是否需要加电。对这种情况，可以增加 “设备4数据(D4 Data)” 数据存储领域，存储采集的数据。', 'assets/strategyExamples/strategy3.png'],

                  '策略4：复杂计算级联分解模式': ['该策略将可分步计算的数据处理需求按计算步骤进行分解，增加数据存储领域存放中间结果，将复杂计算需求分解为简单计算需求。' +
                  '例如，下图中，计算(Computing)需求要求从D6 Data和D7 Data取得数据，完成计算后将结果保存至计算结果(Computing Result)领域中。' +
                  '根据领域专家提供的信息，计算需求包括两个子功能:Subcomputing 1、Subcomputing 2，并且二者满足顺序关系。' +
                  '因此，将 Computing 需求分解为子需求Subcomputing 1与Subcomputing 2，并新增数据存储领域Subcomputing 1 Result用于存储Subcomputing 1的计算结果',
                    'assets/strategyExamples/strategy4.png'],

                  '策略5：控制-计算分离模式': ['该策略通过增加计算需求，将控制和计算分离。' +
                  '例如，下图中，设备9控制(Device9 Control)需求将从设备8(Device8)采集数据，根据采到的数据生成设备9(Device9)的控制信号用以控制设备9。' +
                  '若根据采到的数据生成设备控制信号的计算很复杂，则增加新的需求D9 Control Computing，专门负责计算，而将控制的需求留给Device9 Control 2，' +
                  '二者联合起来 完成原来的需求Device9 Control', 'assets/strategyExamples/strategy5.png']
                };
                htmlLabel = '<div style="text-align: left; font-size: 20px; margin-bottom: 10px">' + advice[this.strategyAdviceIdx].title + '</div>';
                for(var j=0;j<advice[this.strategyAdviceIdx].content.length;j++) {
                  htmlLabel += '<div style="text-align: left; font-size: 15px; margin-bottom: 10px">' + advice[this.strategyAdviceIdx].content[j] + '</div>';
                }
                const title = advice[this.strategyAdviceIdx].title, constStr = '策略简介';
                htmlLabel += '<div style="text-align: left; font-size: 20px; margin-bottom: 10px">' + constStr + '</div>';
                htmlLabel += '<div style="text-align: left; font-size: 15px; margin-bottom: 10px">' + strategyDict[title][0] + '</div>';
                htmlLabel += '<img style="width: 740px; height: 400px" src=' + '"' + strategyDict[title][1] + '"' + '/>';
                Swal.fire({
                  title: '策略检查',
                  icon: 'info',
                  showCancelButton: false,
                  showConfirmButton: true,
                  showDenyButton: true,
                  confirmButtonText: '接受建议',
                  denyButtonText: `忽略建议`,
                  cancelButtonText: '取消',
                  html: htmlLabel,
                  width: 1000,

                }).then((result) => {
                  if (result.isConfirmed) {
                    // this.fileService.saveProject(this.project.title, this.project).subscribe();
                    location.href = 'http://localhost:7071/qmap/?projectname=' + this.project.title;
                    const strategyIdMap = {'策略1：独立设备操作分离模式':'1', '策略2：设备操作融合和抽象模式':'2', '策略3：数据生产消费分离模式':'3', '策略4：复杂计算级联分解模式':'4', '策略5：控制-计算分离模式':'5'};
                    var projectRecord = new ProjectRecord();
                    projectRecord.id = this.communicationService.getProjectID();
                    projectRecord.strategyId = strategyIdMap[title];
                    projectRecord.project = this.project;
                    console.log('projectRecord:', projectRecord);
                    this.projectService.recordLastProject(projectRecord).subscribe(success => {
                      if(!success) {
                        alert('记录解耦前项目失败！');
                      }
                    });
                    this.strategyAdviceIdx += 1;

                  } else if (result.isDenied) {
                    // 向后端发送请求保存忽略了advice[this.strategyAdviceIdx]建议的信息
                    // this.projectService.ignoreStrategyAdvice(advice[this.strategyAdviceIdx]).subscribe();
                    this.strategyAdviceIdx += 1;
                  } else {
                    ;
                  }
                });
              }
            }

            if(this.strategyAdviceIdx >= advice.length) {
              this.step = 3;
              this.projectService.stepChange(this.step);
            }
          }
        );
        break;

      case 3:
        // 3.1:良构性检查
        var errorList;
        console.log('this.project:', this.project);
        this.projectService.checkWellFormed(this.project).subscribe(
          errors => {
            errorList = errors;
            this.sendErrors(errorList);
            if (this.projectService.getRes(errorList)) {
              this.step = 4;
            } else {
              alert("情景图非良构");
            }
            this.projectService.stepChange(this.step);
          }
        );
        break;

      case 4:
        // 3.2:投影
        this.projectService.getSubProblemDiagram(this.project).subscribe(
          project => {
            this.project = project;
            console.log(this.project);
            this.dg_service.project=project;
            // this.projectService.sendProject(project);
            this.projectService.spdChange(project.subProblemDiagramList);
          }
        );
        this.step = 5;
        this.projectService.stepChange(this.step);
        break;

      case 5:

        // 4: 获取追溯关系
        if(this.project == undefined || this.project == null) {
          alert('当前项目为空！');
          return;
        }
        if(this.project.scenarioGraphList == undefined || this.project.scenarioGraphList == null || this.project.scenarioGraphList.length == 0) {
          alert('当前项目无情景图');
          return;
        }
        var checkTraceBody = new CheckTraceBody();
        checkTraceBody.id = this.communicationService.getProjectID();
        checkTraceBody.project = this.project;
        console.log(checkTraceBody)
        this.projectService.checkTrace(checkTraceBody).subscribe(traces => {
          console.log('checkTrace返回的traces:', traces);
          if(this.communicationService.traces == undefined) {
            this.communicationService.traces = new Array();
            this.project.traceList = new Array();
          }
          for(var i=0;i<traces.length;i+=1) {
            var exist = false;
            for(var j=0;j<this.communicationService.traces.length;j++) {
              if(this.isSameTrace(this.communicationService.traces[j], traces[i])) {
                exist = true;
                break;
              }
            }
            if(!exist) {
              this.communicationService.traces.push(traces[i]);
              this.project.traceList.push(traces[i]);
            }
          }
          this.communicationService.tracesToStr(this.project.traceList);
        });
        this.step = 6;
        this.projectService.stepChange(this.step);

        break;

      case 6:
        // 5.1:获取数据依赖
        // if(this.project == undefined || this.project == null) {
        //   alert('当前项目为空！');
        //   return;
        // }
        // if(this.project.problemDiagram == undefined || this.project.problemDiagram == null) {
        //   alert('当前项目无问题图，无法分析数据依赖关系！');
        //   return;
        // }
        // this.projectService.analyzeDataDependencies(this.project).subscribe(dataDependencies => {
        //   if(this.communicationService.dataDependencies == undefined) {
        //     this.communicationService.dataDependencies = new Array();
        //     this.project.dataDependenceList = new Array();
        //   }
        //   for(var i=0;i<dataDependencies.length;i+=1) {
        //     var exist = false;
        //     for(var j=0;j<this.communicationService.dataDependencies.length;j+=1) {
        //       if(this.isSameDependence(this.communicationService.dataDependencies[j], dataDependencies[i])) {
        //         exist = true;
        //         break;
        //       }
        //     }
        //     if(!exist) {
        //       this.communicationService.dataDependencies.push(dataDependencies[i]);
        //       this.project.dataDependenceList.push(dataDependencies[i]);
        //     }
        //   }
        //   this.communicationService.dataDependenciesToStr();
        // });
      //   this.step = 7;
      //   this.projectService.stepChange(this.step);
      //   break;

      // case 7:
        // 5.2:获取控制依赖
        // if(this.project == undefined || this.project == null) {
        //   alert('当前项目为空！');
        //   return;
        // }
        // if(this.project.problemDiagram == undefined || this.project.problemDiagram == null) {
        //   alert('当前项目无问题图，无法分析控制依赖关系！');
        //   return;
        // }
        // this.projectService.analyzeControlDependencies(this.project).subscribe(controlDependencies => {
        //   if(this.communicationService.controlDependencies == undefined) {
        //     this.communicationService.controlDependencies = new Array();
        //     this.project.controlDependenceList = new Array();
        //   }
        //   for(var i=0;i<controlDependencies.length;i+=1) {
        //     var exist = false;
        //     for(var j=0;j<this.communicationService.controlDependencies.length;j+=1) {
        //       if(this.isSameDependence(this.communicationService.controlDependencies[j], controlDependencies[i])) {
        //         exist = true;
        //         break;
        //       }
        //     }
        //     if(!exist) {
        //       this.communicationService.controlDependencies.push(controlDependencies[i]);
        //       this.project.controlDependenceList.push(controlDependencies[i]);
        //     }
        //   }
        //   this.communicationService.controlDependenciesToStr();
        // });
        // break;
      // case 6:
        alert("当前已是最终步骤！")
        // this.step = 6;
        // this.projectService.stepChange(this.step);
        break;
    }
  }

  showScenarioIcon() {
    let problemIcon = document.getElementById("problem-icon");
    let scenarioIcon = document.getElementById("scenario-icon");
    problemIcon.style.display = "none";
    scenarioIcon.style.display = "block";
  }

  showProblemIcon() {
    let problemIcon = document.getElementById("problem-icon");
    let scenarioIcon = document.getElementById("scenario-icon");
    problemIcon.style.display = "block";
    scenarioIcon.style.display = "none";
  }

  initBoard() {
    // console.log("new Project");
    this.tabList = [];
    if (this.project.scenarioGraphList) {
      for (let sg of this.project.scenarioGraphList) {
        this.tabList.push(sg.title);
      }
    }
    if (this.project.subProblemDiagramList) {
      for (let spd of this.project.subProblemDiagramList) {
        this.tabList.push(spd.title)
      }
    }
  }

  showContectDiagram() {
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

  showProblemDiagram() {
    document.getElementById("content1").style.display = 'none';
    document.getElementById("content2").style.display = 'block';
    this.initBoard();
    if (this.tabList) {
      for (var i = 0; i < this.tabList.length; i++) {
        const id = this.tabList[i] + 'M';
        document.getElementById(id).style.display = 'none';
      }
    }
  }

  showScenarioDiagram() {
    document.getElementById("content1").style.display = 'none';
    document.getElementById("content2").style.display = 'none';
    this.initBoard();
    if (this.tabList) {
      for (var i = 0; i < this.tabList.length; i++) {
        const id = this.tabList[i] + 'M';
        document.getElementById(id).style.display = 'block';
      }
    }
  }

  isSameTrace(trace1: Trace, trace2: Trace) {
    if(trace1.source != trace2.source) return false;
    if(trace1.type != trace2.type) return false;
    if(trace1.target.length != trace2.target.length) return false;
    for(var i=0;i<trace1.target.length;i+=1) {
      if(trace1.target[i] != trace2.target[i]) return false;
    }
    return true;
  }

  isSameDependence(dependence1: Dependence, dependence2: Dependence) {
    if(dependence1.source != dependence2.source) return false;
    if(dependence1.target != dependence2.target) return false;
    if(dependence1.data.length != dependence2.data.length) return false;
    for(var i=0;i<dependence1.data.length;i+=1) {
      if(dependence1.data[i] != dependence2.data[i]) return false;
    }
    return true;
  }

  constructor(
    private projectService: ProjectService,
    private fileService: FileService,
    private dg_service: DrawGraphService,
    private verficationService: VerficationService,
    private communicationService: CommunicationService
  ) {
    projectService.stepEmmited$.subscribe(
      step => {
        this.step = step;
      }
    )
    projectService.changeEmitted$.subscribe(
      project => {
        this.project = project;
        this.pheList = this.projectService.getPhenomenon(project);
        // this.reqPheList = this.projectService.getReference(project);
        // 用于显示页面右边的Reference信息
        this.projectService.getReference(project).subscribe(
          reqPheList => {
            this.reqPheList = reqPheList
          }
        )
      });

    fileService.newProEmmited$.subscribe(
      res => {
        //console.log(res);
        if (res == true) {
          this.step = 1;
          this.projectService.stepChange(this.step);

        }
      }
    )
  }



  ngOnInit() {
  }
}
