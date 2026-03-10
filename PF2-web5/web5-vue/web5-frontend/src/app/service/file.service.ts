import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FileUploader } from 'ng2-file-upload';
import { Observable, Subject } from 'rxjs';
import { OntologyEntity } from '../entity/OntologyEntity';
import { Project } from '../entity/Project';
import { Util } from '../entity/Util';

@Injectable({
  providedIn: 'root'
})
export class FileService {
  constructor(
    private http: HttpClient,
  ) { }
  projectAddress; string;
  projectName: string;
  version: string;
  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };


  private newProEmit = new Subject<any>();
  newProEmmited$ = this.newProEmit.asObservable();
  newProject(isnew: boolean) {
    this.newProEmit.next(isnew);
  }

  private projectNameEmit = new Subject<any>();
  projectNameEmmited$ = this.projectNameEmit.asObservable();
  editProName(proName: string) {
    this.projectNameEmit.next(proName);
  }

  setProject(projectAddress: string): Observable<any> {
    let username = this.getUserName();
    this.projectAddress = projectAddress;
    const url = `http://localhost:7086/file/setProject/${projectAddress}?username=${username}`;
    console.log('projectAddress:', projectAddress);
    var res = this.http.post<any>(url, this.httpOptions);
    return res;
  }

  //上传文件
  uploadFile(uploader: FileUploader) {
    let username = this.getUserName();
    // 开始上传
    let url = `http://localhost:7086/file/upload/${this.projectAddress}?username=${username}`
    uploader.setOptions({ url: url });
    uploader.uploadAll();
    console.log(url);
  }
  //上传Owl文件
  uploadOwlFile(uploader: FileUploader, type, projectAddress) {
    let username = this.getUserName();
    // 开始上传
    let url = `http://localhost:7086/file/uploadOwl/${projectAddress}/${type}?username=${username}`
    uploader.setOptions({ url: url });
    uploader.uploadAll();
    console.log("uploadOwlFile", url);
  }

  //上传pf文件
  uploadpfFile(uploader: FileUploader) {
    let username = this.getUserName();
    // 开始上传
    let url = `http://localhost:7086/file/uploadpf/${this.projectAddress}?username=${username}`
    uploader.setOptions({ url: url });
    uploader.uploadAll();
    //console.log(url);
  }
  //将后端owl目录下owl文件移动到Project目录下
  moveOwl(type: string, projectAddress: string, owl: string, version) {
    let username = this.getUserName();
    this.projectAddress = projectAddress;
    const url = `http://localhost:7086/file/moveOwl/${type}/${projectAddress}/${owl}/${version}?username=${username}`;
    console.log(url);
    var res = this.http.post<any>(url, this.httpOptions);
    return res;
  }
  movePOwl(projectAddress: string, owl: string, version) {
    return this.moveOwl("powl", projectAddress, owl, version);
  }
  moveEOwl(projectAddress: string, owl: string, version: string) {
    return this.moveOwl("eowl", projectAddress, owl, version);
  }

  //读取项目信息
  getProject(projectAddress: string, version: string): Observable<Project> {
    let username = this.getUserName();
    const url = `http://localhost:7086/file/getProject/${projectAddress}/${username}/${version}`;
    let res = this.http.get<Project>(url);
    // console.log("projectAddress:", projectAddress);
    // console.log(url);
    //console.log("testttt");
    return res;
  }

  //保存项目
  saveProject(projectAddress: string, project: Project): Observable<boolean> {
    let username = this.getUserName();
    const url = `http://localhost:7086/file/saveProject/${projectAddress}/${username}`;
    // console.log(url)
    var res = this.http.post<boolean>(url, project, this.httpOptions);
    return res;
  }
  // saveProject(userName: string, projectName: string, version: string, project: Project): Observable<any>{
  // 	const url = `http://localhost:7086/file/saveProject?userName=${userName}&projectName=${projectName}&version=${version}`;
  // 	var res = this.http.post<any>(url, project, this.httpOptions);
  // 	return res;
  // }
  createVersion(projectAddress: string): Observable<boolean> {
    let username = this.getUserName();
    const url = `http://localhost:7086/file/createVersion/${projectAddress}/${username}`;
    return this.http.post<boolean>(url, this.httpOptions);
  }

  format(projectAddress: string, project: Project): Observable<boolean> {
    let username = this.getUserName();
    const url = `http://localhost:7086/file/format/${projectAddress}?username=${username}`;
    var res = this.http.post<boolean>(url, project, this.httpOptions);
    return res;
  }

  searchProject(): Observable<string[]> {
    let username = this.getUserName();
    const url = `http://localhost:7086/file/searchProject?username=${username}`;
    return this.http.get<string[]>(url);
  }

  searchVersion(project: string): Observable<string[]> {
    let username = this.getUserName();
    const url = `http://localhost:7086/file/searchVersion/${project}/${username}`;
    return this.http.get<string[]>(url);
  }
  searchOwlVersion(owl: string): Observable<string[]> {

    let username = this.getUserName()
    const url = `http://localhost:7086/file/searchOwlVersion/${owl}?username=${username}`;
    return this.http.get<string[]>(url);
  }

  getNodes(owlAdd, powlName, nodeName, type): Observable<string[]> {

    let username = this.getUserName()
    const url = `http://localhost:7086/file/getNodes/${owlAdd}/${powlName}/${nodeName}/${type}?username=${username}`;
    console.log(url)
    return this.http.get<string[]>(url);
  }
  searchOwl(type): Observable<string[]> {

    let username = this.getUserName();
    const url = `http://localhost:7086/file/searchOwl/${type}?username=${username}`;
    return this.http.get<string[]>(url);
  }
  getProblemDomains(owlAdd, owlName): Observable<OntologyEntity[]> {
    let username = this.getUserName();
    const url = `http://localhost:7086/file/getProblemDomains/${owlAdd}/${owlName}?username=${username}`;
    return this.http.get<OntologyEntity[]>(url);
  }
  //读取项目信息
  getPFProject(projectAddress: string): Observable<Project> {
    let username = this.getUserName();
    const url = `http://localhost:7086/file/xtextToXmi?username=${username}`;
    let res = this.http.post<Project>(url, projectAddress, this.httpOptions);
    return res;
  }
  //读取项目信息
  downloadProject(projectAddress: string): Observable<Object> {
    let username = this.getUserName();
    const url = `http://localhost:7086/file/download/${projectAddress}?username=${username}`;
    let res = this.http.post<Object>(url, projectAddress, this.httpOptions);
    return res;
  }
  getUserName() {
    let username = "";
    if (document.cookie != null && document.cookie != "") {
      let util = new Util();
      username = util.cookieToJson()['username'];
    }
    return username ? username : "test";
  }

  findOwl(userName: string): Observable<any>{
    const url = `http://localhost:7086/file/findOwl?userName=${userName}&projectName=${this.projectName}&version=${this.version}`;
    var res = this.http.get<any>(url);
    return res;
  }
}
