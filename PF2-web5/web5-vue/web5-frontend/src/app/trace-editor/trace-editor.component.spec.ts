import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TraceEditorComponent } from './trace-editor.component';

describe('TraceEditorComponent', () => {
  let component: TraceEditorComponent;
  let fixture: ComponentFixture<TraceEditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TraceEditorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TraceEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
