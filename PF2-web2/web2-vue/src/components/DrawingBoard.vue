<template>
  <div class="box_4 flex-col">
    <!-- Toolbar / Action Buttons -->
    <div class="block_2 flex-row justify-between action-bar">
      
      <div class="action-btn flex-col" @click="back">
        <span class="action-text">&#36820;&#22238;</span>
      </div>

      <div class="action-btn flex-col" @click="openProject">
        <span class="action-text">&#25171;&#24320;</span>
      </div>

      <div class="action-btn flex-col" @click="triggerUpload">
        <span class="action-text">&#19978;&#20256;</span>
      </div>

      <div class="action-btn flex-col" @click="saveProject">
        <span class="action-text">&#20445;&#23384;</span>
      </div>

      <div class="action-btn flex-col" @click="download">
        <span class="action-text">&#19979;&#36733;</span>
      </div>

      <div class="action-btn flex-col" @click="switchView">
        <span class="action-text">&#20999;&#25442;</span>
      </div>

      <div class="action-btn flex-col" @click="help">
        <span class="action-text">&#24110;&#21161;</span>
      </div>
    </div>

    <!-- Tabs -->
    <div class="block_3 flex-row tabs-bar">
      <!-- Context Diagram -->
      <div class="tab-item flex-col" 
           :class="{ 'tab-active': activeTab === 'Context Diagram' }" 
           @click="setActiveTab('Context Diagram')">
          <span class="tab-text">&#20219;&#21153;&#24847;&#22270;&#31034;&#24847;&#22270;</span>
      </div>

      <!-- Problem Diagram -->
      <div class="tab-item flex-col" 
           :class="{ 'tab-active': activeTab === 'Problem Diagram' }" 
           @click="setActiveTab('Problem Diagram')">
          <span class="tab-text">&#19978;&#19979;&#25991;&#22270;</span>
      </div>

      <!-- Scenario Graph -->
      <div class="tab-item flex-col sg-tab" 
           :class="{ 'tab-active': activeTab.includes('M') || activeTab === 'Scenario Graph' }" 
           @click="toggleSgMenu">
          <span class="tab-text">&#24773;&#26223;&#22270;<span v-if="step >= 2" class="caret" :class="{ open: showSgMenu }"></span></span>
          <div v-if="showSgMenu && step >= 2" class="sg-dropdown">
            <div class="sg-item" v-for="sg in project?.scenarioGraphList" :key="sg.title" @click.stop="selectSg(sg.title)">
              {{ sg.title }}
            </div>
          </div>
      </div>
    </div>

    <!-- Canvas Area -->
    <div class="block_4 flex-col">
      <div id="content1" v-show="activeTab === 'Context Diagram'" class="graph-content"></div>
      <div id="content2" v-show="activeTab === 'Problem Diagram'" class="graph-content"></div>
      <div class="graph-container graph-content" v-for="sg in project?.scenarioGraphList" 
           :key="sg.title" 
           :id="sg.title + 'M'" 
           v-show="activeTab === sg.title + 'M'">
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from 'vue';
import $ from 'jquery';
import { useProjectStore } from '../store/project';
import { useEditorStore } from '../store/editor';
import { useUIStore } from '../store/ui';
import { useDrawGraph } from '../composables/useDrawGraph';
import { saveProject as apiSaveProject, downloadProject } from '../api/file';
import { storeToRefs } from 'pinia';

// Make jQuery available globally
if (typeof window !== 'undefined') {
  (window as any).$ = $;
  (window as any).jQuery = $;
}

const projectStore = useProjectStore();
const editorStore = useEditorStore();
const uiStore = useUIStore();
const { project, step } = storeToRefs(projectStore);
const { activeTab } = storeToRefs(editorStore);
const {
  initPapers,
  deleteSelection,
  refreshScenarioGraph,
  refreshContextDiagram,
  refreshProblemDiagram,
  ensureScenarioGraphPaper,
  ensureContextDiagramPaper,
  ensureProblemDiagramPaper,
} = useDrawGraph();

// Global Keydown for Delete
onMounted(() => {
  // Only init papers if a project is already loaded (e.g. hot-reload scenario)
  if (project.value) {
    setTimeout(() => {
      initPapers();
    }, 100);
  }

  window.addEventListener('keydown', (e) => {
      if (e.key === 'Delete' || e.key === 'Backspace') {
          deleteSelection();
      }
  });
});
const back = () => {
  // Logic for back action, maybe go to previous step or home
  console.log('Back clicked');
  // For now just alert or log
};

const triggerUpload = () => {
  uiStore.openPopup('UploadProject');
};

const openProject = () => {
  uiStore.openPopup('OpenProject');
};

const saveProject = async () => {
  if (!project.value) {
    alert('No project opened!');
    return;
  }
  const address = project.value.title || 'default';
  try {
    await apiSaveProject(address, project.value);
    alert('Project saved successfully!');
  } catch (error) {
    console.error('Save failed', error);
    alert('Save failed');
  }
};

const download = async () => {
  if (!project.value) return;
  const address = project.value.title || 'default';
  try {
    const response = await downloadProject(address);
    const url = window.URL.createObjectURL(new Blob([response as any]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `${address}.xml`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  } catch (error) {
    console.error('Download failed', error);
  }
};

const help = () => {
  window.open('http://re4cps.org/help', '_blank');
};

const switchView = () => {
  if (activeTab.value === 'Context Diagram') {
    setActiveTab('Problem Diagram');
  } else if (activeTab.value === 'Problem Diagram') {
    setActiveTab('Scenario Graph');
  } else {
    setActiveTab('Context Diagram');
  }
};

const showSgMenu = ref(false);
const toggleSgMenu = () => {
  if (step.value >= 2) {
    showSgMenu.value = !showSgMenu.value;
  } else {
    setActiveTab('Scenario Graph');
  }
};
const selectSg = (title: string) => {
  editorStore.setActiveTab(title + 'M');
  nextTick(() => {
    ensureScenarioGraphPaper(title);
  });
  showSgMenu.value = false;
};
const setActiveTab = (tab: string) => {
  if (tab === 'Scenario Graph') {
    if (project.value?.scenarioGraphList?.length) {
      const title = project.value.scenarioGraphList[0].title;
      editorStore.setActiveTab(title + 'M');
      nextTick(() => {
        ensureScenarioGraphPaper(title);
      });
    }
    return;
  }
  editorStore.setActiveTab(tab);
  // After Vue applies v-show, ensure the paper is created/updated for the now-visible tab
  nextTick(() => {
    if (tab === 'Context Diagram') {
      ensureContextDiagramPaper();
    } else if (tab === 'Problem Diagram') {
      ensureProblemDiagramPaper();
    } else if (tab.endsWith('M')) {
      ensureScenarioGraphPaper(tab.substring(0, tab.length - 1));
    }
  });
};

// Watchers
watch(step, (newStep) => {
  if (newStep === 1) {
    setActiveTab('Context Diagram');
  } else if (newStep === 2) {
    if (project.value?.scenarioGraphList?.length) {
      const title = project.value.scenarioGraphList[0].title;
      setActiveTab(title + 'M');
    }
  }
});

watch(project, (newProject) => {
  if (newProject) {
    nextTick(() => {
      initPapers();
    });
  }
});
</script>

<style scoped>
/* Specific adjustments for internal layout */
.box_4 {
  overflow: hidden !important; /* Force hidden overflow to prevent layout expansion */
}

.graph-content {
  width: 100%;
  height: 100%;
  overflow: auto; /* Allow scrolling if content is large */
  position: relative;
}

/* Action Bar Styles (Top Buttons) */
.action-bar {
  margin: 1vw 0;
  padding: 0 1vw;
  gap: 0.8vw;
}

.action-btn {
  background-color: #6eaefb; /* Default Blue */
  border-radius: 6px;
  height: 2.2vw;
  width: 5vw;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.2s;
  color: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.action-btn:hover {
  background-color: #40a9ff;
  transform: translateY(-1px);
}

.action-text {
  font-size: 0.9vw;
  font-family: 'Microsoft YaHei', sans-serif;
}

/* Tabs Bar Styles (Bottom Tabs) */
.tabs-bar {
    background-color: rgba(148, 172, 208, 1);
    border-radius: 16px;
    padding: 5px 6px 3px 6px; /* Reduced bottom padding */
    height: auto;
    margin-top: 0.5vw;
    display: flex;
    align-items: center;
    gap: 10px;
    width: fit-content; /* Adjust width to content */
    min-height: 2.6vw;
}

.tab-item {
    padding: 0.5vw 1.2vw;
    border-radius: 12px 12px 4px 4px; /* Top rounded, bottom slightly rounded */
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    /* Default state */
    background-color: transparent;
    color: rgba(255, 255, 255, 0.9);
}

.tab-active {
    background-color: #ffffff;
    color: rgba(148, 172, 208, 1); /* Contrast text color */
    font-weight: bold;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08); /* Softer shadow */
    /* transform: translateY(-2px); Removed lift to stay closer to bottom */
}

.tab-text {
    font-size: 1.1vw;
    font-family: 'Microsoft YaHei', sans-serif;
    white-space: nowrap;
    color: inherit; /* Inherit from parent */
}

.tab-active .tab-text {
    color: rgba(148, 172, 208, 1);
}
.sg-tab {
  position: relative;
}
.sg-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  background: #ffffff;
  color: #284B77;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(40, 75, 119, 0.18);
  margin-top: 6px;
  min-width: 14vw;
  z-index: 1000;
  border: 1px solid rgba(148, 172, 208, 0.3);
  padding: 0.4vw 0;
}
.sg-dropdown::before {
  content: "";
  position: absolute;
  top: -8px;
  left: 14px;
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-bottom: 8px solid #ffffff;
  filter: drop-shadow(0 -1px 1px rgba(40, 75, 119, 0.08));
}
.sg-item {
  padding: 0.7vw 1.1vw;
  cursor: pointer;
  font-size: 0.95vw;
  transition: background 0.2s, color 0.2s;
}
.sg-item:hover {
  background: linear-gradient(90deg, #eaf2ff 0%, #f6fbff 100%);
  color: #1c3b5f;
}
.caret {
  margin-left: 0.4vw;
  display: inline-block;
  width: 0;
  height: 0;
  border-left: 0.35vw solid transparent;
  border-right: 0.35vw solid transparent;
  border-top: 0.35vw solid #6eaefb;
  transform: rotate(0deg);
  transition: transform 0.2s ease, border-top-color 0.2s ease;
}
.caret.open {
  transform: rotate(180deg);
  border-top-color: #40a9ff;
}
</style>
