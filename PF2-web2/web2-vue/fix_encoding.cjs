const fs = require('fs');
const path = require('path');

// Use relative path since we are running from web2-vue directory
const componentsPath = path.join(__dirname, 'src', 'components');

// 1. TopBar.vue Content
const topBarContent = `<template>
  <div class="group_1 flex-col">
    <div class="group_2 flex-row">
      <span class="text_1">Insight</span>
      <span class="text_2">????????????????????`??</span>
    </div>
    <div class="auth-buttons flex-row">
      <div class="auth-item flex-row">
        <img class="label_1" src="../assets/img/d40431bfb45d877dfb533659563e3882.png" />
        <span class="text_3">???</span>
      </div>
      <div class="auth-divider"></div>
      <div class="auth-item flex-row">
        <img class="label_2" src="../assets/img/93cd7b6954e7ac55e1ded824eb2e1b83.png" />
        <span class="text_4">???</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// TopBar logic
</script>

<style scoped>
/* Styles are handled by theme.css */
</style>`;

// 2. DrawingBoard.vue Content
const drawingBoardContent = `<template>
  <div class="box_4 flex-col">
    <!-- Toolbar / Action Buttons -->
    <div class="block_2 flex-row justify-between">
      
      <div class="text-wrapper_1 flex-col" @click="back">
        <span class="text_5">????</span>
      </div>

      <div class="text-wrapper_2 flex-col" @click="openProject">
        <span class="text_6">??</span>
      </div>

      <div class="text-wrapper_3 flex-col" @click="triggerUpload">
        <span class="text_7">???</span>
      </div>

      <div class="text-wrapper_4 flex-col" @click="saveProject">
        <span class="text_8">????</span>
      </div>

      <div class="text-wrapper_5 flex-col" @click="download">
        <span class="text_9">????</span>
      </div>

      <div class="text-wrapper_6 flex-col" @click="switchView">
        <span class="text_10">????</span>
      </div>

      <div class="text-wrapper_7 flex-col" @click="help">
        <span class="text_11">????</span>
      </div>
    </div>

    <!-- Tabs -->
    <div class="block_3 flex-row">
      <!-- Context Diagram -->
      <div class="section_2 flex-col" v-if="activeTab === 'Context Diagram'">
         <span class="text_12">???????????</span>
         <div class="box_5 flex-col"></div>
      </div>
      <span class="text_13" v-else @click="setActiveTab('Context Diagram')">???????????</span>

      <!-- Problem Diagram -->
      <div class="section_2 flex-col" v-if="activeTab === 'Problem Diagram'">
         <span class="text_12">???????</span>
         <div class="box_5 flex-col"></div>
      </div>
      <span class="text_14" v-else @click="setActiveTab('Problem Diagram')">???????</span>

      <!-- Scenario Graph -->
      <div class="section_2 flex-col" v-if="activeTab.includes('M') || activeTab === 'Scenario Graph'">
         <span class="text_12">???</span>
         <div class="box_5 flex-col"></div>
      </div>
      <span class="text_14" v-else @click="setActiveTab('Scenario Graph')">???</span>
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
const { initPapers } = useDrawGraph();

// Actions
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
    link.setAttribute('download', \`\${address}.xml\`);
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

onMounted(() => {
  setTimeout(() => {
    initPapers();
  }, 100);
});
</script>

<style scoped>
/* Specific adjustments for internal layout */
.graph-content {
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.text_13, .text_14 {
  cursor: pointer;
}
</style>`;

// 3. RightBar.vue Content
const rightBarContent = `<template>
  <div class="box_6 flex-col right-bar">
    <!-- 1. Step Description Box -->
    <div class="step-box flex-col">
      <div class="step-title text_16">???????</div>
      <div class="step-content text_17">
        <div v-if="step === 1">
          ????1????????????
          <br>????1.1???????
          <br>????1.2??????????
          <br>????1.3????????????
          <br>????1.4???????????
        </div>
        <div v-else-if="step === 2">
          ????2??????????
          <br>????2.1?????????????????
          <br>????2.2??????????
          <br>????2.3????????????
        </div>
        <div v-else>
          ????{{ step }}?????????????????????
        </div>
      </div>
    </div>

    <!-- 2. Navigation Buttons -->
    <div class="nav-buttons flex-row justify-between">
      <div class="nav-btn text-wrapper_18" @click="back">
        <span class="text_30">?????</span>
      </div>
      <div class="nav-btn text-wrapper_19" @click="next">
        <span class="text_31">{{ step === 5 ? '???' : '?????' }}</span>
      </div>
    </div>

    <!-- 3. Accordion Sections -->
    <div class="accordion-container flex-col">
      
      <!-- Diagram Section -->
      <div class="accordion-item">
        <div class="accordion-header flex-row align-center" @click="toggleSection('diagrams')">
          <span class="accordion-icon">{{ sections.diagrams ? '-' : '+' }}</span>
          <span class="accordion-title">? (Diagram)</span>
        </div>
        <div class="accordion-content" v-show="sections.diagrams">
          <div class="list-item" :class="{ active: activeTab === 'Context Diagram' }" @click="selectDiagram('Context Diagram')">
            ??????? (Context Diagram)
          </div>
          <div class="list-item" :class="{ active: activeTab === 'Problem Diagram' }" @click="selectDiagram('Problem Diagram')">
            ????? (Problem Diagram)
          </div>
          <div v-for="sg in project?.scenarioGraphList" :key="sg.title" 
               class="list-item" 
               :class="{ active: activeTab === sg.title + 'M' }"
               @click="selectDiagram(sg.title + 'M')">
            {{ sg.title }}
          </div>
        </div>
      </div>

      <!-- Phenomenon Section -->
      <div class="accordion-item">
        <div class="accordion-header flex-row align-center" @click="toggleSection('phenomena')">
          <span class="accordion-icon">{{ sections.phenomena ? '-' : '+' }}</span>
          <span class="accordion-title">???? (Phenomenon)</span>
        </div>
        <div class="accordion-content" v-show="sections.phenomena">
          <table class="info-table">
            <thead>
              <tr>
                <th>???</th>
                <th>????</th>
                <th>????</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="phe in phenomena" :key="phe.phenomenon_no">
                <td>{{ phe.phenomenon_no }}</td>
                <td>{{ phe.phenomenon_name }}</td>
                <td>{{ phe.phenomenon_type }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Interface Section -->
      <div class="accordion-item">
        <div class="accordion-header flex-row align-center" @click="toggleSection('interfaces')">
          <span class="accordion-icon">{{ sections.interfaces ? '-' : '+' }}</span>
          <span class="accordion-title">??? (Interface)</span>
        </div>
        <div class="accordion-content" v-show="sections.interfaces">
           <div v-if="!project?.contextDiagram?.interfaceList?.length" class="empty-tip">??????????</div>
           <table class="info-table" v-else>
            <thead>
              <tr>
                <th>???</th>
                <th>????</th>
                <th>????</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in project.contextDiagram.interfaceList" :key="item.interface_no">
                <td>{{ item.interface_no }}</td>
                <td>{{ item.interface_name }}</td>
                <td>{{ item.interface_description }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Reference Section -->
      <div class="accordion-item">
        <div class="accordion-header flex-row align-center" @click="toggleSection('references')">
          <span class="accordion-icon">{{ sections.references ? '-' : '+' }}</span>
          <span class="accordion-title">???? (Reference)</span>
        </div>
        <div class="accordion-content" v-show="sections.references">
           <div v-if="!project?.problemDiagram?.referenceList?.length" class="empty-tip">????????????</div>
           <table class="info-table" v-else>
            <thead>
              <tr>
                <th>???</th>
                <th>????</th>
                <th>????</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in project.problemDiagram.referenceList" :key="item.reference_no">
                <td>{{ item.reference_no }}</td>
                <td>{{ item.reference_name }}</td>
                <td>{{ item.reference_description }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Other Information Section -->
      <div class="accordion-item">
        <div class="accordion-header flex-row align-center" @click="toggleSection('other')">
          <span class="accordion-icon">{{ sections.other ? '-' : '+' }}</span>
          <span class="accordion-title">??????? (Other)</span>
        </div>
        <div class="accordion-content" v-show="sections.other">
          <div class="info-row">
            <span class="label">????????</span>
            <span>{{ project?.title || '??????' }}</span>
          </div>
          <!-- Add more project info here -->
        </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue';
import { useProjectStore } from '../store/project';
import { useEditorStore } from '../store/editor';
import { storeToRefs } from 'pinia';
import { getPhenomenon } from '../api/project';
import type { Phenomenon } from '../types/Phenomenon';

const projectStore = useProjectStore();
const editorStore = useEditorStore();

const { project, step } = storeToRefs(projectStore);
const { activeTab } = storeToRefs(editorStore);

const sections = reactive({
  diagrams: true,
  phenomena: false,
  interfaces: false,
  references: false,
  other: false
});

const toggleSection = (section: keyof typeof sections) => {
  sections[section] = !sections[section];
};

const phenomena = ref<Phenomenon[]>([]);

const fetchPhenomenaData = async () => {
  if (project.value) {
    try {
      const res = await getPhenomenon(project.value);
      phenomena.value = res;
    } catch (e) {
      console.error('Failed to fetch phenomena:', e);
      // Fallback to local computation if API fails
      phenomena.value = projectStore.allPhenomena;
    }
  } else {
    phenomena.value = [];
  }
};

watch([project, step], fetchPhenomenaData, { immediate: true });

const back = () => {
  if (step.value > 1) {
    projectStore.setStep(step.value - 1);
  }
};

const next = () => {
  if (step.value < 5) { // Assuming max step 5
    projectStore.setStep(step.value + 1);
  }
};

const selectDiagram = (name: string) => {
  editorStore.setActiveTab(name);
};
</script>

<style scoped>
/* Container Style - Reusing Web1 box_6 style but adapted */
.right-bar {
  /* Position and Size */
  width: 20vw; /* Adjusted width */
  height: 85vh; /* Occupy most height */
  margin: 1vw;
  padding: 1vw;
  
  /* Glassmorphism / Visual Style from Web1 */
  background-color: rgba(255, 255, 255, 0.5);
  border: 2px solid rgba(255, 255, 255, 1);
  border-radius: 16px;
  overflow-y: auto;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

/* Step Description */
.step-box {
  margin-bottom: 1.5vw;
  padding: 0.8vw;
  background-color: rgba(255, 255, 255, 0.6);
  border-radius: 8px;
}

.step-title {
  font-size: 1.2vw;
  font-weight: bold;
  color: #333;
  margin-bottom: 0.5vw;
}

.step-content {
  font-size: 0.9vw;
  color: #555;
  line-height: 1.4;
}

/* Navigation Buttons */
.nav-buttons {
  margin-bottom: 1.5vw;
  gap: 1vw;
}

.nav-btn {
  flex: 1;
  height: 2.5vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #0B58F5; /* Primary Blue */
  border-radius: 6px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.nav-btn:hover {
  opacity: 0.9;
}

.nav-btn span {
  color: white;
  font-size: 1vw;
  font-weight: bold;
}

/* Accordion */
.accordion-container {
  gap: 0.5vw;
}

.accordion-item {
  background-color: rgba(255, 255, 255, 0.4);
  border-radius: 6px;
  overflow: hidden;
}

.accordion-header {
  padding: 0.8vw;
  background-color: rgba(11, 88, 245, 0.1);
  cursor: pointer;
  user-select: none;
}

.accordion-header:hover {
  background-color: rgba(11, 88, 245, 0.2);
}

.accordion-icon {
  margin-right: 0.5vw;
  font-weight: bold;
  color: #0B58F5;
}

.accordion-title {
  font-size: 1vw;
  color: #333;
  font-weight: 500;
}

.accordion-content {
  padding: 0.5vw;
  background-color: rgba(255, 255, 255, 0.3);
}

/* List Items */
.list-item {
  padding: 0.4vw 0.8vw;
  cursor: pointer;
  font-size: 0.9vw;
  color: #444;
  border-radius: 4px;
}

.list-item:hover {
  background-color: rgba(0, 0, 0, 0.05);
  color: #0B58F5;
}

.list-item.active {
  background-color: rgba(11, 88, 245, 0.1);
  color: #0B58F5;
  font-weight: bold;
}

/* Tables */
.info-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.8vw;
}

.info-table th {
  text-align: left;
  padding: 0.4vw;
  background-color: rgba(0, 0, 0, 0.03);
  color: #666;
  font-weight: normal;
}

.info-table td {
  padding: 0.4vw;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  color: #333;
}

.empty-tip {
  padding: 1vw;
  text-align: center;
  color: #999;
  font-size: 0.8vw;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 0.4vw 0;
  font-size: 0.9vw;
}

.info-row .label {
  color: #666;
}
</style>`;

try {
  fs.writeFileSync(path.join(componentsPath, 'TopBar.vue'), topBarContent, 'utf8');
  console.log('Successfully wrote TopBar.vue with UTF-8');
  
  fs.writeFileSync(path.join(componentsPath, 'DrawingBoard.vue'), drawingBoardContent, 'utf8');
  console.log('Successfully wrote DrawingBoard.vue with UTF-8');
  
  fs.writeFileSync(path.join(componentsPath, 'RightBar.vue'), rightBarContent, 'utf8');
  console.log('Successfully wrote RightBar.vue with UTF-8');
  
} catch (err) {
  console.error('Error writing files:', err);
}
