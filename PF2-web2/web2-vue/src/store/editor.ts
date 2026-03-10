import { defineStore } from 'pinia';

export const useEditorStore = defineStore('editor', {
  state: () => ({
    tool: null as string | null, // 'Domain', 'Machine', 'Req', 'Interface', 'Reference', 'Constraint', etc.
    activeCategory: null as string | null, // 'element', 'link'
    activeTab: 'Context Diagram' as string,
  }),
  actions: {
    selectTool(tool: string | null) {
      this.tool = tool;
      console.log('Selected tool:', tool);
    },
    clearTool() {
      this.tool = null;
    },
    setActiveTab(tab: string) {
      this.activeTab = tab;
    }
  }
});
