import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { FileUploadModule } from 'ng2-file-upload';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DrawingboardComponent } from './drawingboard/drawingboard.component';
import { LeftbarComponent } from './leftbar/leftbar.component';
import { RightbarComponent } from './rightbar/rightbar.component';
import { TopbarComponent } from './topbar/topbar.component';
import {Project} from './entity/Project';
import { TraceEditorComponent } from './trace-editor/trace-editor.component';
import { MonacoEditorModule } from "@materia-ui/ngx-monaco-editor";
import { DataDependenciesEditorComponent } from './data-dependencies-editor/data-dependencies-editor.component';
import { ControlDependenciesEditorComponent } from './control-dependencies-editor/control-dependencies-editor.component';
// import {MonacoConfig} from "./trace-editor/monaco-config";
// import { ServiceWorkerModule } from "@angular/service-worker";


@NgModule({
  declarations: [
    AppComponent,
    TopbarComponent,
    LeftbarComponent,
    RightbarComponent,
    DrawingboardComponent,
    TraceEditorComponent,
    DataDependenciesEditorComponent,
    ControlDependenciesEditorComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    CommonModule,
    FileUploadModule,
    AppRoutingModule,
    MonacoEditorModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
