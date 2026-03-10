/* 用于实现组件间的通信 */
import {Injectable} from "@angular/core";
import {Trace} from "../entity/Trace";
import {Dependence} from "../entity/Dependence";

@Injectable({
  providedIn: 'root'
})
export class CommunicationService {

  traces: Trace[];  // 追溯关系数组
  code: string;  // 追溯关系数据转换的字符串
  username: string;  // 当前用户名
  projectname: string;  // 当前项目名称
  version: string;  // 当前项目版本
  dataDependencies: Dependence[];  // 数据依赖关系数组
  controlDependencies: Dependence[];  // 控制依赖关系数组
  dataDependenciesCode: string;  // 数据依赖关系数据转换的字符串
  controlDependenciesCode: string;  // 控制依赖关系数据转换的字符串
  constructor() {

  }
  getProjectID() {
    return this.username + ',' + this.projectname;
  }

  tracesToStr(traces: Trace[]) {
    // 将追踪关系数组转换为字符串
    if(traces == undefined || traces == null) {
      this.code = '';
      return;
    }
    var ans = '';
    for(var i=0;i<traces.length;i+=1) {
      ans += traces[i].source + '  ' + traces[i].type + '  ';
      ans += '{';
      for(var j=0;j<traces[i].target.length-1;j++) {
        ans += traces[i].target[j] + ', ';
      }
      ans += traces[i].target[traces[i].target.length-1];
      ans += '}\n';
    }
    this.code = ans;
  }

  dataDependenciesToStr() {
    // 将数据依赖关系数组转换为字符串
    if(this.dataDependencies == undefined || this.dataDependencies == null) {
      this.dataDependenciesCode = '';
      return;
    }
    var ans = '';
    for(var i=0;i<this.dataDependencies.length;i+=1) {
      ans += this.dataDependencies[i].source + ' -> ' + this.dataDependencies[i].target + ', ';
      ans += 'because of the data: {';
      var count = 0;
      for(let data of this.dataDependencies[i].data) {
        count += 1;
        if(count < this.dataDependencies[i].data.length) ans += data + ',';
        else ans += data + '}\n';
      }
    }
    this.dataDependenciesCode = ans;
  }

  controlDependenciesToStr() {
    // 将控制依赖关系数组转换为字符串
    if(this.controlDependencies == undefined || this.controlDependencies == null) {
      this.controlDependenciesCode = '';
      return;
    }
    var ans = '';
    for(let i =0; i<this.controlDependencies.length; i+=1) {
      ans += this.controlDependencies[i].source + ' and ' + this.controlDependencies[i].target + ' maybe exist control dependence, ';
      ans += 'because of the device: {';
      var count = 0;
      for(let data of this.controlDependencies[i].data) {
        count += 1;
        if(count < this.controlDependencies[i].data.length) ans += data + ',';
        else ans += data + '}\n';
      }
    }
    this.controlDependenciesCode = ans;
  }
}
