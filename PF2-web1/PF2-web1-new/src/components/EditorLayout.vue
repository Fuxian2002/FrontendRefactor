﻿<template>
  <div class="page flex-col">
    <div class="section_1 flex-row">
      <!-- Header / Group 1 -->
      <div class="group_1 flex-col">
        <div class="group_2 flex-row">
          <span class="text_1">Insight</span>
          <span class="text_2">智能化需求建模分析规约一体化平台</span>
        </div>
        <div class="auth-buttons flex-row">
           <div class="auth-item flex-row">
             <!-- <img class="label_1" referrerpolicy="no-referrer" src="@/assets/img/5f68c6faf8ad95c5bb08a7c63f2dbe4d.png" /> -->
             <span class="text_3">登录</span>
           </div>
           <div class="auth-divider"></div>
           <div class="auth-item flex-row">
              <!-- <img class="label_2" referrerpolicy="no-referrer" src="@/assets/img/da1885c817f1df3d1f0549cdbf5927f8.png" /> -->
              <span class="text_4">注册</span>
           </div>
        </div>
      </div>
      
      <!-- Main Content / Group 4 -->
      <div class="group_4 flex-row">
        
        <!-- Left Sidebar -->
        <Sidebar @tool-select="onToolSelect" :active-tool="activeTool" />

        <!-- Center / Box 4 -->
        <div class="box_4 flex-col">
          <!-- Top Toolbar -->
          <div class="block_2 flex-row justify-between">
            <div class="text-wrapper_1 flex-col" @click="openCreateModal"><span class="text_5">新建</span></div>
            <div class="text-wrapper_2 flex-col" @click="triggerUpload"><span class="text_6">上传</span></div>
            <div class="text-wrapper_3 flex-col" @click="openProjectModal"><span class="text_7">打开</span></div>
            <div class="text-wrapper_4 flex-col" @click="saveProject"><span class="text_8">保存</span></div>
            <div class="text-wrapper_5 flex-col" @click="downloadProject"><span class="text_9">下载</span></div>
            <div class="text-wrapper_6 flex-col" @click="switchView"><span class="text_10">切换</span></div>
            <div class="text-wrapper_7 flex-col" @click="openHelp"><span class="text_11">帮助</span></div>
          </div>
          <!-- Diagram Tabs -->
          <div class="block_3 flex-row">
            <div class="section_2 flex-col">
              <span class="text_12">任务意图示意图</span>
              <div class="box_5 flex-col"></div>
            </div>
            <span class="text_13">上下文图</span>
            <span class="text_14">问题图</span>
          </div>
          <!-- Diagram Area -->
          <div class="block_4 flex-col" @drop="onDrop" @dragover="onDragOver">
             <div v-if="viewMode === 'diagram'" class="vue-flow__container">
               <VueFlow v-model="store.elements" 
                        :node-types="nodeTypes"
                        :default-viewport="{ zoom: 1 }" 
                        :min-zoom="0.2" 
                        :max-zoom="4"
                        @pane-ready="onPaneReady"
                        @node-click="onNodeClick"
                        @edge-click="onEdgeClick"
                        @pane-click="onPaneClick">
                  <Background pattern-color="#aaa" :gap="8" />
                  <Controls />
                  <MiniMap />
               </VueFlow>
             </div>
             <div v-else class="text-editor-placeholder">
               <p>Text Editor Mode (Placeholder)</p>
               <!-- Integrate Monaco Editor here later -->
             </div>
          </div>
        </div>

        <!-- Right Sidebar -->
        <PropertyEditor :selected-node="selectedNode" @close="selectedNode = null" />
      </div>
    </div>

    <!-- Create Project Modal -->
    <div v-if="showCreateModal" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <span class="modal-title">Create New Project</span>
          <button class="close-btn" @click="closeCreateModal">×</button>
        </div>
        <div class="modal-body">
          <label>Project Name:</label>
          <input v-model="newProjectName" type="text" placeholder="Enter project name" />
        </div>
        <div class="modal-footer">
          <button @click="confirmCreateProject">Confirm</button>
          <button @click="closeCreateModal">Cancel</button>
        </div>
      </div>
    </div>

    <!-- Open Project Modal -->
    <div v-if="showOpenModal" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <span class="modal-title">Open Project</span>
          <button class="close-btn" @click="closeOpenModal">×</button>
        </div>
        <div class="modal-body">
          <div v-if="loadingProjects">Loading projects...</div>
          <div v-else-if="projectList.length === 0">No projects found.</div>
          <div v-else class="project-list">
            <div v-for="project in projectList" :key="project" 
                 class="project-item" 
                 :class="{ selected: selectedProject === project }"
                 @click="selectedProject = project">
              {{ project }}
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="confirmOpenProject" :disabled="!selectedProject">Open</button>
          <button @click="closeOpenModal">Cancel</button>
        </div>
      </div>
    </div>
    <!-- Upload Input -->
    <input type="file" ref="fileInput" style="display: none" @change="handleFileUpload" />

    <!-- System Editor Modal -->
    <SystemEditorModal 
        v-model:visible="showSystemModal"
        @confirm="handleSystemConfirm"
    />

    <!-- Problem Domain Editor Modal -->
    <ProblemDomainEditorModal
        v-model:visible="showProblemDomainModal"
        @confirm="handleProblemDomainConfirm"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, markRaw } from 'vue';
import { VueFlow } from '@vue-flow/core';
import { Background } from '@vue-flow/background';
import { Controls } from '@vue-flow/controls';
import { MiniMap } from '@vue-flow/minimap';
import '@vue-flow/core/dist/style.css';
import '@vue-flow/controls/dist/style.css';
import '@vue-flow/minimap/dist/style.css';
import { useLspStore } from '@/stores/lspStore';
import Sidebar from './Sidebar.vue';
import PropertyEditor from './PropertyEditor.vue';
import SystemNode from './nodes/SystemNode.vue';
import MachineNode from './nodes/MachineNode.vue';
import ProblemDomainNode from './nodes/ProblemDomainNode.vue';
import RequirementNode from './nodes/RequirementNode.vue';
import SystemEditorModal from './modals/SystemEditorModal.vue';
import ProblemDomainEditorModal from './modals/ProblemDomainEditorModal.vue';
import axios from 'axios';
import { CONFIG } from '@/config';

import '@/assets/index.response.css'; // Import layout styles

const store = useLspStore();

const nodeTypes = {
  system: markRaw(SystemNode) as any,
  machine: markRaw(MachineNode) as any,
  problemDomain: markRaw(ProblemDomainNode) as any,
  requirement: markRaw(RequirementNode) as any,
};

const selectedNode = ref<any>(null);
const vueFlowInstance = ref<any>(null);
const activeTool = ref<string | null>(null);
const connectionSource = ref<any>(null);

import { showToast } from 'vant';

// Tool Selection
const onToolSelect = (tool: string) => {
  if (tool === 'interface' || tool === 'constraint') {
      if (activeTool.value === tool) {
          activeTool.value = null;
          connectionSource.value = null;
          showToast('Connection Mode Deactivated.');
      } else {
          activeTool.value = tool;
          connectionSource.value = null;
          showToast(`Connection Mode Active (${tool}): Click source node, then target node.`);
      }
  } else {
      activeTool.value = null;
      connectionSource.value = null;
  }
};

const onNodeClick = (event: any) => {
  if (activeTool.value === 'interface' || activeTool.value === 'constraint') {
      if (!connectionSource.value) {
          connectionSource.value = event.node;
          showToast(`Selected source: ${event.node.label || event.node.id}`);
      } else {
          if (connectionSource.value.id === event.node.id) {
              showToast('Cannot connect a node to itself.');
              return;
          }
          
          if (activeTool.value === 'interface') {
              store.addInterface(connectionSource.value, event.node);
              showToast(`Interface connected.`);
          } else if (activeTool.value === 'constraint') {
              store.addConstraint(connectionSource.value, event.node);
              showToast(`Constraint connected.`);
          }
          
          // Reset selection but keep tool active for more connections
          connectionSource.value = null;
      }
  } else {
      selectedNode.value = event.node;
  }
};

const onEdgeClick = (event: any) => {
    selectedNode.value = event.edge;
};

const onPaneClick = () => {
  selectedNode.value = null;
};

// View Mode
const viewMode = ref<'diagram' | 'text'>('diagram');

const switchView = () => {
  viewMode.value = viewMode.value === 'diagram' ? 'text' : 'diagram';
};

// Help
const openHelp = () => {
  window.open(CONFIG.external.helpUrl, '_blank');
};

// Save & Download
const saveProject = async () => {
  await store.saveProject();
};

const downloadProject = async () => {
  await store.downloadProject();
};

// Upload
const fileInput = ref<HTMLInputElement | null>(null);

const triggerUpload = () => {
  fileInput.value?.click();
};

const handleFileUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement;
  const file = target.files?.item(0);
  
  if (file) {
    const projectName = file.name.split('.')[0] || 'Untitled';
    
    const formData = new FormData();
    formData.append('xmlFile', file); 
    
    try {
        const username = document.cookie.match(/username=([^;]+)/)?.[1] || 'test';
        await axios.post(`${CONFIG.backend.baseUrl}${CONFIG.backend.endpoints.setProject}/${projectName}?username=${username}`);
        
        await axios.post(`${CONFIG.backend.baseUrl}${CONFIG.backend.endpoints.upload}/${projectName}?username=${username}`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        
        alert('Project uploaded successfully!');
        
        await store.openProject(projectName);
        
    } catch (e) {
        console.error('Upload failed', e);
        alert('Upload failed');
    }
  }
};

// Create Project Modal State
const showCreateModal = ref(false);
const newProjectName = ref('');

const openCreateModal = () => {
  showCreateModal.value = true;
  newProjectName.value = '';
};

const closeCreateModal = () => {
  showCreateModal.value = false;
};

const confirmCreateProject = async () => {
  if (!newProjectName.value.trim()) {
    alert("The project title can't be null");
    return;
  }
  if (newProjectName.value.includes(' ')) {
    alert("The project title can't contains space!");
    return;
  }

  try {
    const success = await store.createNewProject(newProjectName.value);
    if (success) {
      closeCreateModal();
    }
  } catch (error) {
    console.error('Failed to create project:', error);
    alert('Failed to create project');
  }
};

// Open Project Modal State
const showOpenModal = ref(false);
const projectList = ref<string[]>([]);
const selectedProject = ref<string | null>(null);
const loadingProjects = ref(false);

const openProjectModal = async () => {
  showOpenModal.value = true;
  selectedProject.value = null;
  loadingProjects.value = true;
  projectList.value = await store.getProjectList();
  loadingProjects.value = false;
};

const closeOpenModal = () => {
  showOpenModal.value = false;
};

const confirmOpenProject = async () => {
  if (!selectedProject.value) return;
  
  try {
    const success = await store.openProject(selectedProject.value);
    if (success) {
      closeOpenModal();
    }
  } catch (error) {
    console.error('Failed to open project:', error);
    alert('Failed to open project');
  }
};

// Keyboard Handling for Deletion
import { onBeforeUnmount } from 'vue';

const handleKeyDown = (event: KeyboardEvent) => {
  if (event.key === 'Delete' || event.key === 'Backspace') {
    // Check if we are focusing on an input field (e.g. project name modal), if so, don't delete node
    if (event.target instanceof HTMLInputElement || event.target instanceof HTMLTextAreaElement) {
        return;
    }

    if (selectedNode.value) {
      // Check if it's a node or an edge
      const id = selectedNode.value.id;
      if (id) {
        store.deleteElement(id);
        selectedNode.value = null; // Clear selection
        showToast('Element deleted');
      }
    }
  }
};

onMounted(() => {
  store.connect();
  window.addEventListener('keydown', handleKeyDown);
});

onBeforeUnmount(() => {
  window.removeEventListener('keydown', handleKeyDown);
});

const onPaneReady = (instance: any) => {
  vueFlowInstance.value = instance;
};

// System Modal State
const showSystemModal = ref(false);
const pendingSystemPos = ref<{x: number, y: number} | null>(null);

const showProblemDomainModal = ref(false);
const pendingProblemDomainPos = ref<{x: number, y: number} | null>(null);

const handleSystemConfirm = (data: { description: string, shortName: string }) => {
    if (pendingSystemPos.value) {
        store.addNodeToProject('system', pendingSystemPos.value.x, pendingSystemPos.value.y, {
            name: data.description,
            shortName: data.shortName
        });
        pendingSystemPos.value = null;
    }
};

const handleProblemDomainConfirm = (data: { description: string, shortName: string, domainType: string, property: string }) => {
    if (pendingProblemDomainPos.value) {
        store.addNodeToProject('problemDomain', pendingProblemDomainPos.value.x, pendingProblemDomainPos.value.y, {
            name: data.description,
            shortName: data.shortName,
            domainType: data.domainType,
            property: data.property
        });
        pendingProblemDomainPos.value = null;
    }
};

const onDrop = (event: DragEvent) => {
  event.preventDefault();
  const type = event.dataTransfer?.getData('application/vueflow');
  if (!type) return;

  if (vueFlowInstance.value) {
    const bounds = (event.currentTarget as HTMLElement).getBoundingClientRect();
    const position = vueFlowInstance.value.project({
      x: event.clientX - bounds.left,
      y: event.clientY - bounds.top,
    });

    if (type === 'system') {
        pendingSystemPos.value = position;
        showSystemModal.value = true;
    } else {
        store.addNodeToProject(type, position.x, position.y);
    }
  }
};

const onDragOver = (event: DragEvent) => {
  event.preventDefault();
};
</script>

<style scoped>
/* Override global box_4 styles to reduce margins and fit layout */
.box_4 {
  margin: 2.1vw 0.8vw 0 0.8vw !important;
  padding-top: 0.5vw !important;
  width: auto !important;
  flex: 1 !important;
  overflow: hidden; /* Prevent content from forcing width */
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  color: #333; /* Ensure text is visible */
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.modal-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #666;
}

.modal-body {
  margin-bottom: 20px;
}

.modal-body label {
    display: block;
    margin-bottom: 5px;
    color: #333;
    font-weight: 500;
}

.modal-body input {
  width: 100%;
  padding: 10px;
  margin-top: 5px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #fff;
  color: #333;
}

/* Override button styles for gradient look */
.text-wrapper_1,
.text-wrapper_2,
.text-wrapper_3,
.text-wrapper_4,
.text-wrapper_5,
.text-wrapper_6,
.text-wrapper_7 {
  /* Gradient based on user snippet:
     Start: (0,0) -> End: (1,1) (approx 135deg)
     Colors:
       1. R:57, G:234, B:255 -> #39EAFF
       2. R:110, G:158, B:255 -> #6E9EFF
       3. R:11, G:88, B:245 -> #0B58F5
     Locations: 0.0, 0.0, 1.0 (Interpreted as inclusive gradient)
  */
  background: linear-gradient(135deg, #39EAFF 0%, #6E9EFF 50%, #0B58F5 100%) !important;
  border-radius: 8px !important;
  cursor: pointer;
  transition: transform 0.1s, box-shadow 0.1s;
  /* Ensure text remains centered and white */
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  width: 6.8vw !important; /* Reduced width to fit all buttons */
}

.text-wrapper_2,
.text-wrapper_3,
.text-wrapper_4,
.text-wrapper_5,
.text-wrapper_6,
.text-wrapper_7 {
  margin-left: 0.6vw !important; /* Reduced margin */
}

.block_2 {
  width: 100% !important; /* Ensure toolbar container takes full width */
}

.text-wrapper_1:hover,
.text-wrapper_2:hover,
.text-wrapper_3:hover,
.text-wrapper_4:hover,
.text-wrapper_5:hover,
.text-wrapper_6:hover,
.text-wrapper_7:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(45, 107, 255, 0.3);
}

.text-wrapper_1:active,
.text-wrapper_2:active,
.text-wrapper_3:active,
.text-wrapper_4:active,
.text-wrapper_5:active,
.text-wrapper_6:active,
.text-wrapper_7:active {
  transform: translateY(0);
  box-shadow: 0 1px 3px rgba(45, 107, 255, 0.2);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.modal-footer button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.modal-footer button:first-child {
  background-color: #007bff;
  color: white;
}

.modal-footer button:last-child {
  background-color: #e0e0e0;
}

.project-list {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.project-item {
  padding: 8px 12px;
  cursor: pointer;
  border-bottom: 1px solid #eee;
}

.project-item:hover {
  background-color: #f5f5f5;
}

.project-item.selected {
  background-color: #e6f7ff;
  border-left: 3px solid #1890ff;
}

/* Ensure diagram takes full space of its container */
.block_4 {
  position: relative;
  overflow: hidden;
  width: 100% !important;
}
.vue-flow__container {
  width: 100%;
  height: 100%;
}
</style>