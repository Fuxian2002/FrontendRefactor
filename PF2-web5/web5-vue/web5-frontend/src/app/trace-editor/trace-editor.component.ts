import { Component, OnInit } from '@angular/core';
import { CommunicationService } from "../service/communication.service";
import { Trace } from "../entity/Trace";

@Component({
  selector: 'app-trace-editor',
  templateUrl: './trace-editor.component.html',
  styleUrls: ['./trace-editor.component.css']
})
export class TraceEditorComponent implements OnInit {

  editorOptions = {theme: 'vs-write', language: 'javascript'};

  constructor(public communicationService: CommunicationService) {
    // this.code = this.tracesToStr(communicationService.traces);
  }


  ngOnInit() {
  }

}
