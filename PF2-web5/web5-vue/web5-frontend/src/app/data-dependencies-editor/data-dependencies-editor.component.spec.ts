import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DataDependenciesEditorComponent } from './data-dependencies-editor.component';

describe('DataDependenciesEditorComponent', () => {
  let component: DataDependenciesEditorComponent;
  let fixture: ComponentFixture<DataDependenciesEditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DataDependenciesEditorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DataDependenciesEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
