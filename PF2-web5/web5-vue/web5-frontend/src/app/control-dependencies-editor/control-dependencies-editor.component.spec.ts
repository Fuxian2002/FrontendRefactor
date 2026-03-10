import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ControlDependenciesEditorComponent } from './control-dependencies-editor.component';

describe('ControlDependenciesEditorComponent', () => {
  let component: ControlDependenciesEditorComponent;
  let fixture: ComponentFixture<ControlDependenciesEditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ControlDependenciesEditorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ControlDependenciesEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
