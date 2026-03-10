import { defineStore } from 'pinia';

export const useUIStore = defineStore('ui', {
  state: () => ({
    // View state for managing what is displayed (e.g., DrawingBoard, TraceEditor, etc.)
    currentView: 'projectDiagram' as 'projectDiagram' | 'traceText' | 'traceDiagram' | 'dataDependencies' | 'controlDependencies',

    // Component Choice Service State
    choice: {
      element: false,
      machine: false,
      domain: false,
      req: false,
      link: false,
      interface: false,
      reference: false,
      constraint: false,
      merge: false,
    },
    // Popups
    popups: {
      popBox: false,
      Strategy: false,
      OpenProject: false,
      OpenOwl: false,
      OpenPOwl: false,
    }
  }),
  actions: {
    setChoiceFalse() {
      this.choice.element = false;
      this.choice.domain = false;
      this.choice.machine = false;
      this.choice.req = false;
      this.choice.link = false;
      this.choice.interface = false;
      this.choice.reference = false;
      this.choice.constraint = false;
      this.choice.merge = false;
    },
    openPopup(name: keyof typeof this.popups) {
      this.popups[name] = true;
    },
    closePopup(name: keyof typeof this.popups) {
      this.popups[name] = false;
    },
    setCurrentView(view: typeof this.currentView) {
      this.currentView = view;
    }
  }
});
