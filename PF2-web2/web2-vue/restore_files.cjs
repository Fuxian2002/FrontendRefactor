const fs = require('fs');
const path = require('path');

const componentsPath = path.join(__dirname, 'src', 'components');

// Helper to convert string to UTF-8 buffer
function writeUtf8(filePath, content) {
    fs.writeFileSync(filePath, content, { encoding: 'utf8' });
    console.log(`Restored ${path.basename(filePath)}`);
}

// 1. TopBar.vue
// ????????????????????`??
// ???
// ???
const topBarContent = `<template>
  <div class="group_1 flex-col">
    <div class="group_2 flex-row">
      <span class="text_1">Insight</span>
      <span class="text_2">\u667A\u80FD\u5316\u9700\u6C42\u5EFA\u6A21\u5206\u6790\u89C4\u7EA6\u4E00\u4F53\u5316\u5E73\u53F0</span>
    </div>
    <div class="auth-buttons flex-row">
      <div class="auth-item flex-row">
        <img class="label_1" src="../assets/img/d40431bfb45d877dfb533659563e3882.png" />
        <span class="text_3">\u767B\u5F55</span>
      </div>
      <div class="auth-divider"></div>
      <div class="auth-item flex-row">
        <img class="label_2" src="../assets/img/93cd7b6954e7ac55e1ded824eb2e1b83.png" />
        <span class="text_4">\u6CE8\u518C</span>
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

// 2. DrawingBoard.vue
// ????: \u8FD4\u56DE
// ??: \u6253\u5F00
// ???: \u4E0A\u4F20
// ????: \u4FDD\u5B58
// ????: \u4E0B\u8F7D
// ????: \u5207\u6362
// ????: \u5E2E\u52A9
// ???????????: \u4EFB\u52A1\u610F\u56FE\u793A\u610F\u56FE
// ???????: \u4E0A\u4E0B\u6587\u56FE
// ???: \u60C5\u666F\u56FE
const drawingBoardContent = `<template>
  <div class="box_4 flex-col">
    <!-- Toolbar / Action Buttons -->
    <div class="block_2 flex-row justify-between">
      
      <div class="text-wrapper_1 flex-col" @click="back">
        <span class="text_5">\u8FD4\u56DE</span>
      </div>

      <div class="text-wrapper_2 flex-col" @click="openProject">
        <span class="text_6">\u6253\u5F00</span>
      </div>

      <div class="text-wrapper_3 flex-col" @click="triggerUpload">
        <span class="text_7">\u4E0A\u4F20</span>
      </div>

      <div class="text-wrapper_4 flex-col" @click="saveProject">
        <span class="text_8">\u4FDD\u5B58</span>
      </div>

      <div class="text-wrapper_5 flex-col" @click="download">
        <span class="text_9">\u4E0B\u8F7D</span>
      </div>

      <div class="text-wrapper_6 flex-col" @click="switchView">
        <span class="text_10">\u5207\u6362</span>
      </div>

      <div class="text-wrapper_7 flex-col" @click="help">
        <span class="text_11">\u5E2E\u52A9</span>
      </div>
    </div>

    <!-- Tabs -->
    <div class="block_3 flex-row">
      <!-- Context Diagram -->
      <div class="section_2 flex-col" v-if="activeTab === 'Context Diagram'">
         <span class="text_12">\u4EFB\u52A1\u610F\u56FE\u793A\u610F\u56FE</span>
         <div class="box_5 flex-col"></div>
      </div>
      <span class="text_13" v-else @click="setActiveTab('Context Diagram')">\u4EFB\u52A1\u610F\u56FE\u793A\u610F\u56FE</span>

      <!-- Problem Diagram -->
      <div class="section_2 flex-col" v-if="activeTab === 'Problem Diagram'">
         <span class="text_12">\u4E0A\u4E0B\u6587\u56FE</span>
         <div class="box_5 flex-col"></div>
      </div>
      <span class="text_14" v-else @click="setActiveTab('Problem Diagram')">\u4E0A\u4E0B\u6587\u56FE</span>

      <!-- Scenario Graph -->
      <div class="section_2 flex-col" v-if="activeTab.includes('M') || activeTab === 'Scenario Graph'">
         <span class="text_12">\u60C5\u666F\u56FE</span>
         <div class="box_5 flex-col"></div>
      </div>
      <span class="text_14" v-else @click="setActiveTab('Scenario Graph')">\u60C5\u666F\u56FE</span>
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

// 3. RightBar.vue
// ???????: \u5EFA\u6A21\u6B65\u9AA4
// ????1????????????: \u6B65\u9AA41\uFF1A\u83B7\u53D6\u4EFB\u52A1\u610F\u56FE
// ????1.1???????: \u6B65\u9AA41.1\uFF1A\u6807\u8BC6\u7CFB\u7EDF
// ????1.2??????????: \u6B65\u9AA41.2\uFF1A\u8BC6\u522B\u5916\u90E8\u5B9E\u4F53
// ????1.3????????????: \u6B65\u9AA41.3\uFF1A\u8BC6\u522B\u4EFB\u52A1\u610F\u56FE
// ????1.4???????????: \u6B65\u9AA41.4\uFF1A\u8BC6\u522B\u5171\u4EAB\u73B0\u8C61
// ????2??????????: \u6B65\u9AA42\uFF1A\u8BC6\u522B\u7CFB\u7EDF\u8FB9\u754C
// ????2.1?????????????????: \u6B65\u9AA42.1\uFF1A\u8BC6\u522B\u8BBE\u5907\u5E76\u5EFA\u7ACB\u63A5\u53E3
// ????2.2??????????: \u6B65\u9AA42.2\uFF1A\u8BC6\u522B\u4EA4\u4E92\u5185\u5BB9
// ????2.3????????????: \u6B65\u9AA42.3\uFF1A\u68C0\u67E5\u4E0A\u4E0B\u6587\u56FE
// ????: \u6B65\u9AA4
// ?????????????????????: \uFF1A\u8BF7\u6839\u636E\u53F3\u4FA7\u5217\u8868\u7EE7\u7EED\u5EFA\u6A21
// ?????: \u4E0A\u4E00\u6B65
// ???: \u68C0\u67E5
// ?????: \u4E0B\u4E00\u6B65
// ? (Diagram): \u56FE (Diagram)
// ??????? (Context Diagram): \u4E0A\u4E0B\u6587\u56FE (Context Diagram)
// ????? (Problem Diagram): \u95EE\u9898\u56FE (Problem Diagram)
// ???? (Phenomenon): \u73B0\u8C61 (Phenomenon)
// ???: \u7F16\u53F7
// ????: \u540D\u79F0
// ????: \u7C7B\u578B
// ??? (Interface): \u63A5\u53E3 (Interface)
// ??????????: \u6682\u65E0\u63A5\u53E3\u6570\u636E
// ????: \u63CF\u8FF0
// ???? (Reference): \u5F15\u7528 (Reference)
// ????????????: \u6682\u65E0\u5F15\u7528\u6570\u636E
// ??????? (Other): \u5176\u4ED6\u4FE1\u606F (Other)
// ????????: \u9879\u76EE\u540D\u79F0\uFF1A
// ??????: \u672A\u8BBE\u7F6E

const rightBarContent = `<template>
  <div class="box_6 flex-col right-bar">
    <!-- 1. Step Description Box -->
    <div class="step-box flex-col">
      <div class="step-title text_16">\u5EFA\u6A21\u6B65\u9AA4</div>
      <div class="step-content text_17">
        <div v-if="step === 1">
          \u6B65\u9AA41\uFF1A\u83B7\u53D6\u4EFB\u52A1\u610F\u56FE
          <br>\u6B65\u9AA41.1\uFF1A\u6807\u8BC6\u7CFB\u7EDF
          <br>\u6B65\u9AA41.2\uFF1A\u8BC6\u522B\u5916\u90E8\u5B9E\u4F53
          <br>\u6B65\u9AA41.3\uFF1A\u8BC6\u522B\u4EFB\u52A1\u610F\u56FE
          <br>\u6B65\u9AA41.4\uFF1A\u8BC6\u522B\u5171\u4EAB\u73B0\u8C61
        </div>
        <div v-else-if="step === 2">
          \u6B65\u9AA42\uFF1A\u8BC6\u522B\u7CFB\u7EDF\u8FB9\u754C
          <br>\u6B65\u9AA42.1\uFF1A\u8BC6\u522B\u8BBE\u5907\u5E76\u5EFA\u7ACB\u63A5\u53E3
          <br>\u6B65\u9AA42.2\uFF1A\u8BC6\u522B\u4EA4\u4E92\u5185\u5BB9
          <br>\u6B65\u9AA42.3\uFF1A\u68C0\u67E5\u4E0A\u4E0B\u6587\u56FE
        </div>
        <div v-else>
          \u6B65\u9AA4{{ step }}\uFF1A\u8BF7\u6839\u636E\u53F3\u4FA7\u5217\u8868\u7EE7\u7EED\u5EFA\u6A21
        </div>
      </div>
    </div>

    <!-- 2. Navigation Buttons -->
    <div class="nav-buttons flex-row justify-between">
      <div class="nav-btn text-wrapper_18" @click="back">
        <span class="text_30">\u4E0A\u4E00\u6B65</span>
      </div>
      <div class="nav-btn text-wrapper_19" @click="next">
        <span class="text_31">{{ step === 5 ? '\u68C0\u67E5' : '\u4E0B\u4E00\u6B65' }}</span>
      </div>
    </div>

    <!-- 3. Accordion Sections -->
    <div class="accordion-container flex-col">
      
      <!-- Diagram Section -->
      <div class="accordion-item">
        <div class="accordion-header flex-row align-center" @click="toggleSection('diagrams')">
          <span class="accordion-icon">{{ sections.diagrams ? '-' : '+' }}</span>
          <span class="accordion-title">\u56FE (Diagram)</span>
        </div>
        <div class="accordion-content" v-show="sections.diagrams">
          <div class="list-item" :class="{ active: activeTab === 'Context Diagram' }" @click="selectDiagram('Context Diagram')">
            \u4E0A\u4E0B\u6587\u56FE (Context Diagram)
          </div>
          <div class="list-item" :class="{ active: activeTab === 'Problem Diagram' }" @click="selectDiagram('Problem Diagram')">
            \u95EE\u9898\u56FE (Problem Diagram)
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
          <span class="accordion-title">\u73B0\u8C61 (Phenomenon)</span>
        </div>
        <div class="accordion-content" v-show="sections.phenomena">
          <table class="info-table">
            <thead>
              <tr>
                <th>\u7F16\u53F7</th>
                <th>\u540D\u79F0</th>
                <th>\u7C7B\u578B</th>
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
          <span class="accordion-title">\u63A5\u53E3 (Interface)</span>
        </div>
        <div class="accordion-content" v-show="sections.interfaces">
           <div v-if="!project?.contextDiagram?.interfaceList?.length" class="empty-tip">\u6682\u65E0\u63A5\u53E3\u6570\u636E</div>
           <table class="info-table" v-else>
            <thead>
              <tr>
                <th>\u7F16\u53F7</th>
                <th>\u540D\u79F0</th>
                <th>\u63CF\u8FF0</th>
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
          <span class="accordion-title">\u5F15\u7528 (Reference)</span>
        </div>
        <div class="accordion-content" v-show="sections.references">
           <div v-if="!project?.problemDiagram?.referenceList?.length" class="empty-tip">\u6682\u65E0\u5F15\u7528\u6570\u636E</div>
           <table class="info-table" v-else>
            <thead>
              <tr>
                <th>\u7F16\u53F7</th>
                <th>\u540D\u79F0</th>
                <th>\u63CF\u8FF0</th>
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
          <span class="accordion-title">\u5176\u4ED6\u4FE1\u606F (Other)</span>
        </div>
        <div class="accordion-content" v-show="sections.other">
          <div class="info-row">
            <span class="label">\u9879\u76EE\u540D\u79F0\uFF1A</span>
            <span>{{ project?.title || '\u672A\u8BBE\u7F6E' }}</span>
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
  writeUtf8(path.join(componentsPath, 'TopBar.vue'), topBarContent);
  writeUtf8(path.join(componentsPath, 'DrawingBoard.vue'), drawingBoardContent);
  writeUtf8(path.join(componentsPath, 'RightBar.vue'), rightBarContent);
} catch (err) {
  console.error('Error writing files:', err);
}
