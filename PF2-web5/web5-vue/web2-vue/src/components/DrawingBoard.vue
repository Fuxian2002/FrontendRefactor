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
      <div class="tab-item flex-col" 
           :class="{ 'tab-active': activeTab.includes('M') || activeTab === 'Scenario Graph' }"
           @click="setActiveTab('Scenario Graph')">
         <span class="tab-text">&#24773;&#26223;&#22270;</span>
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
const { initPapers, deleteSelection } = useDrawGraph();

// Global Keydown for Delete
onMounted(() => {
  setTimeout(() => {
    initPapers();
  }, 100);
  
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

const setActiveTab = (tab: string) => {
  if (tab === 'Scenario Graph') {
    // If selecting Scenario Graph generally, pick the first one or default
    if (project.value?.scenarioGraphList?.length) {
      editorStore.setActiveTab(project.value.scenarioGraphList[0].title + 'M');
    }
  } else {
    editorStore.setActiveTab(tab);
  }
};

// Watchers
watch(step, (newStep) => {
  if (newStep === 1) {
    editorStore.setActiveTab('Context Diagram'); 
  } else if (newStep === 2) {
    if (project.value?.scenarioGraphList?.length) {
      editorStore.setActiveTab(project.value.scenarioGraphList[0].title + 'M');
      nextTick(() => {
        initPapers();
      });
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
  margin-top: 0.5vw;
  background-color: transparent;
  border-radius: 20px;
  padding: 0.2vw;
  display: flex;
  gap: 0;
  position: relative;
  overflow: visible; /* Allow active tab to overflow if needed */
}

.tab-item {
  padding: 0.6vw 1.5vw;
  cursor: pointer;
  border-radius: 20px 20px 0 0; /* Rounded top */
  transition: all 0.3s;
  position: relative;
  margin-right: -10px; /* Overlap effect */
  z-index: 1;
  background-color: transparent;
}

.tab-active {
  background-color: #9abcf3; /* Active Blue Background */
  z-index: 10; /* Bring to front */
  box-shadow: 0 -2px 10px rgba(0,0,0,0.1);
}

/* Create the white curve effect for active tab using pseudo-element if desired, 
   or just keep the solid color block for now as per screenshot 2 styling 
   which shows a solid rounded shape */

.tab-text {
  font-size: 1vw;
  color: #fff;
  font-family: 'Microsoft YaHei', sans-serif;
  font-weight: bold;
}

.tab-item:not(.tab-active) .tab-text {
  color: #fff;
  opacity: 0.9;
  font-weight: normal;
}
</style>