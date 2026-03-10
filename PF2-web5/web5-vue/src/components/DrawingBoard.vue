<template>
  <div class="box_4 flex-col">
    <div class="block_2 flex-row justify-between action-bar">
      <div class="action-btn flex-col" @click="back">
        <span class="action-text">返回</span>
      </div>

      <div class="action-btn flex-col" @click="openProject">
        <span class="action-text">打开</span>
      </div>

      <div class="action-btn flex-col" @click="triggerUpload">
        <span class="action-text">上传</span>
      </div>

      <div class="action-btn flex-col" @click="saveProject">
        <span class="action-text">保存</span>
      </div>

      <div class="action-btn flex-col" @click="download">
        <span class="action-text">下载</span>
      </div>

      <div class="action-btn flex-col" @click="switchView">
        <span class="action-text">切换</span>
      </div>

      <div class="action-btn flex-col" @click="help">
        <span class="action-text">帮助</span>
      </div>

      <div class="action-btn flex-col" @click="showDataDependencies">
        <span class="action-text">数据依赖</span>
      </div>
    </div>

    <!-- Tabs (Moved to top as primary navigation within DrawingBoard) -->
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
          <span class="tab-text">&#24773;&#26223;&#22270;<span v-if="project?.scenarioGraphList?.length" class="caret" :class="{ open: showSgMenu }"></span></span>
          <div v-if="showSgMenu && project?.scenarioGraphList?.length" class="sg-dropdown">
            <div class="sg-item" v-for="sg in project?.scenarioGraphList" :key="sg.title" @click.stop="selectSg(sg.title)">
              {{ sg.title }}
            </div>
          </div>
      </div>

      <!-- Sub Problem Diagram -->
      <div class="tab-item flex-col spd-tab"
           :class="{ 'tab-active': activeTab.includes('SPD') || activeTab === 'Sub Problem Diagram' }"
           @click="toggleSpdMenu">
          <span class="tab-text">&#23376;&#38382;&#39064;&#22270;<span v-if="project?.subProblemDiagramList?.length" class="caret" :class="{ open: showSpdMenu }"></span></span>
          <div v-if="showSpdMenu && project?.subProblemDiagramList?.length" class="spd-dropdown">
            <div class="spd-item" v-for="(spd, idx) in project?.subProblemDiagramList" :key="spd.title || idx" @click.stop="selectSpd(idx)">
              {{ spd.title || `SPD ${idx + 1}` }}
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
      <div class="graph-container graph-content" v-for="(spd, idx) in project?.subProblemDiagramList"
           :key="`SPD${idx}`"
           :id="`SPD${idx}`"
           v-show="activeTab === `SPD${idx}`">
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, watch, nextTick, ref } from 'vue';
import $ from 'jquery';
import { useProjectStore } from '../store/project';
import { useEditorStore } from '../store/editor';
import { useUIStore } from '../store/ui';
import { useDrawGraph } from '../composables/useDrawGraph';
import { storeToRefs } from 'pinia';
import { saveProject as apiSaveProject, downloadProject } from '../api/file';

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
const { initPapers, deleteSelection, forceUpdatePaper, papers } = useDrawGraph();

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
  uiStore.setCurrentView('projectDiagram');
};

const triggerUpload = () => {
  alert('上传功能暂未接入（web5-vue）');
};

const openProject = () => {
  uiStore.openPopup('OpenProject');
};

const saveProject = async () => {
  if (!project.value) {
    alert('请先打开项目');
    return;
  }
  const address = project.value.title || 'default';
  try {
    await apiSaveProject(address, project.value as any);
    alert('保存成功');
  } catch (error) {
    console.error(error);
    alert('保存失败');
  }
};

const download = async () => {
  if (!project.value) {
    alert('请先打开项目');
    return;
  }
  const address = project.value.title || 'default';
  try {
    const response = await downloadProject(address);
    const url = window.URL.createObjectURL(new Blob([response as any]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `${address}.zip`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  } catch (error) {
    console.error(error);
    alert('下载失败');
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
const showSpdMenu = ref(false);

const toggleSgMenu = () => {
  if (project.value?.scenarioGraphList?.length) {
    showSgMenu.value = !showSgMenu.value;
  } else {
    setActiveTab('Scenario Graph');
  }
};

const toggleSpdMenu = () => {
  if (project.value?.subProblemDiagramList?.length) {
    showSpdMenu.value = !showSpdMenu.value;
  } else {
    alert('还没有生成子问题图，请先完成投影步骤');
  }
};

const selectSg = (title: string) => {
  editorStore.setActiveTab(title + 'M');
  showSgMenu.value = false;
  nextTick(() => {
    const id = title + 'M';
    const paper = papers.value.find((p: any) => p && (p as any).id === id);
    if (paper) forceUpdatePaper(paper as any);
  });
};

const selectSpd = (idx: number) => {
  editorStore.setActiveTab(`SPD${idx}`);
  showSpdMenu.value = false;
  nextTick(() => {
    const id = `SPD${idx}`;
    const paper = papers.value.find((p: any) => p && (p as any).id === id);
    if (paper) forceUpdatePaper(paper as any);
  });
};

const showDataDependencies = () => {
  if (!project.value?.dataDependenceList) {
    alert('暂无数据依赖，请先完成相关分析步骤');
    return;
  }
  console.log("数据依赖列表:", project.value.dataDependenceList);
  // TODO: 打开数据依赖显示模态框
  alert(`数据依赖数量: ${project.value.dataDependenceList.length}\n详细内容请查看浏览器 Console`);
};

const setActiveTab = (tab: string) => {
  if (tab === 'Scenario Graph') {
    if (project.value?.scenarioGraphList?.length) {
      const title = project.value.scenarioGraphList[0].title;
      editorStore.setActiveTab(title + 'M');
      nextTick(() => {
        const id = title + 'M';
        const paper = papers.value.find((p: any) => p && (p as any).id === id);
        if (paper) forceUpdatePaper(paper as any);
      });
    }
  } else {
    editorStore.setActiveTab(tab);
    nextTick(() => {
      if (tab === 'Context Diagram' && papers.value[0]) {
        forceUpdatePaper(papers.value[0] as any);
      } else if (tab === 'Problem Diagram' && papers.value[1]) {
        forceUpdatePaper(papers.value[1] as any);
      }
    });
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
  background-color: #6eaefb;
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
