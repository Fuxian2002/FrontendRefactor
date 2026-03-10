import { Component, OnInit } from '@angular/core';
import {CommunicationService} from "../service/communication.service";

@Component({
  selector: 'app-control-dependencies-editor',
  templateUrl: './control-dependencies-editor.component.html',
  styleUrls: ['./control-dependencies-editor.component.css']
})
export class ControlDependenciesEditorComponent implements OnInit {

  editorOptions = {theme: 'vs-write', language: 'javascript'};
  constructor(public communicationService: CommunicationService) { }

  ngOnInit() {
  }

}
