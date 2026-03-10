<template>
  <div class="modal-overlay" v-if="isVisible">
    <div class="modal-content">
      <div class="modal-header">
        <span class="label">Open Project:</span>
        <button class="close" @click="close">X</button>
      </div>
      <div class="modal-body">
        <ul v-if="projects.length > 0">
          <li v-for="pro in projects" :key="pro" :class="{ selected: selectedProject === pro }" @click="selectProject(pro)">
            {{ pro }}
            <ul v-if="selectedProject === pro">
              <li v-for="ver in versions" :key="ver">
                <label>
                  <input type="radio" name="project" :value="ver" v-model="selectedVersion">
                  {{ ver }}
                </label>
              </li>
            </ul>
          </li>
        </ul>
        <div v-else>No projects found.</div>
      </div>
      <div class="modal-footer">
        <button class="button" @click="open">Open</button>
        <button class="button" @click="close">Cancel</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import { useUIStore } from '../../store/ui';
import { useProjectStore } from '../../store/project';
import { searchProject, searchVersion, getProject } from '../../api/file';

const uiStore = useUIStore();
const projectStore = useProjectStore();

const isVisible = computed(() => uiStore.popups.OpenProject);

const projects = ref<string[]>([]);
const selectedProject = ref('');
const versions = ref<string[]>([]);
const selectedVersion = ref('');

watch(isVisible, async (newVal) => {
  if (newVal) {
    try {
      const res = await searchProject();
      // Ensure res is array
      projects.value = Array.isArray(res) ? res : [];
      selectedProject.value = '';
      versions.value = [];
      selectedVersion.value = '';
    } catch (error) {
      console.error('Failed to load projects', error);
      projects.value = [];
    }
  }
});

const selectProject = async (pro: string) => {
  if (selectedProject.value === pro) return;
  selectedProject.value = pro;
  try {
    const res = await searchVersion(pro);
    versions.value = Array.isArray(res) ? res : [];
    if (versions.value.length > 0) {
      selectedVersion.value = versions.value[0];
    } else {
      selectedVersion.value = '';
    }
  } catch (error) {
    console.error('Failed to load versions', error);
  }
};

const open = async () => {
  if (!selectedProject.value) return;
  try {
    const ver = selectedVersion.value || 'undefined';
    const project = await getProject(selectedProject.value, ver);
    // @ts-ignore
    projectStore.setProject(project);
    projectStore.setStep(1); // Reset step to 1 (Load Problem Diagram)
    // Initialize project if needed (Project.ts had initProject logic)
    // Assuming the backend returns a full project object.
    close();
  } catch (error) {
    console.error('Failed to open project', error);
  }
};

const close = () => {
  uiStore.closePopup('OpenProject');
};
</script>

<style scoped>
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
  z-index: 30000;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 5px;
  width: 400px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  border: 1px solid #ccc;
  padding: 10px;
}

.modal-footer {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  cursor: pointer;
  padding: 5px;
}

li.selected {
  background-color: #e0e0e0;
}

li:hover {
  background-color: #f0f0f0;
}
</style>
