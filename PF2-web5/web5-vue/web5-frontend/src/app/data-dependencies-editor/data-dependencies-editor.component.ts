import { Component, OnInit } from '@angular/core';
import {CommunicationService} from "../service/communication.service";

@Component({
  selector: 'app-data-dependencies-editor',
  templateUrl: './data-dependencies-editor.component.html',
  styleUrls: ['./data-dependencies-editor.component.css']
})
export class DataDependenciesEditorComponent implements OnInit {

  editorOptions = {theme: 'vs-write', language: 'javascript'};
  constructor(public communicationService: CommunicationService) { }

  ngOnInit() {
  }

}
