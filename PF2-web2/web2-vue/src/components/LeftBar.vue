<template>
  <div class="box_1 flex-col">
    <!-- <img class="label_3" src="../assets/img/87644858905e8b2f4a3d0024ea7322a3.png" /> -->
    <div class="box_2 flex-col">
      
      <!-- Scenario Graph Tools -->
      <div class="icon-list flex-col" v-if="step >= 2">
        <!-- Node Tools (Draggable) -->
        <img class="sidebar-icon" src="../assets/icons/start.png" draggable="true" @dragstart="onDragStart($event, 'Start')" title="Start (Drag to canvas)" />
        <img class="sidebar-icon" src="../assets/icons/end.png" draggable="true" @dragstart="onDragStart($event, 'End')" title="End (Drag to canvas)" />
        <img class="sidebar-icon" src="../assets/icons/decision.png" draggable="true" @dragstart="onDragStart($event, 'Decision')" title="Decision (Drag to canvas)" />
        <img class="sidebar-icon" src="../assets/icons/branch.png" draggable="true" @dragstart="onDragStart($event, 'Branch')" title="Branch (Drag to canvas)" />
        <img class="sidebar-icon" src="../assets/icons/merge.png" draggable="true" @dragstart="onDragStart($event, 'Merge')" title="Merge (Drag to canvas)" />
        <img class="sidebar-icon" src="../assets/icons/delay.png" draggable="true" @dragstart="onDragStart($event, 'Delay')" title="Delay (Drag to canvas)" />
        
        <!-- Link Tools (Click to Select) -->
        <img class="sidebar-icon" :class="{ selected: isToolSelected('BehInt') }" src="../assets/icons/BehInt.png" draggable="true" @dragstart="onDragStart($event, 'BehInt')" @click="toggleTool('BehInt')" title="BehaviorInteraction (Drag or Click)" />
        <img class="sidebar-icon" :class="{ selected: isToolSelected('ConnInt') }" src="../assets/icons/ConnInt.png" draggable="true" @dragstart="onDragStart($event, 'ConnInt')" @click="toggleTool('ConnInt')" title="ConnectionInteraction (Drag or Click)" />
        <img class="sidebar-icon" :class="{ selected: isToolSelected('ExpInt') }" src="../assets/icons/ExpInt.png" draggable="true" @dragstart="onDragStart($event, 'ExpInt')" @click="toggleTool('ExpInt')" title="ExpectInteraction (Drag or Click)" />
        <img class="sidebar-icon" :class="{ selected: isToolSelected('BehEnable') }" src="../assets/icons/hong.png" @click="toggleTool('BehEnable')" title="BehaviorEnable (Red Arrow)" />
        <img class="sidebar-icon" :class="{ selected: isToolSelected('BehOrder') }" src="../assets/icons/lan.png" @click="toggleTool('BehOrder')" title="BehaviorOrder (Blue Arrow)" />
        <img class="sidebar-icon" :class="{ selected: isToolSelected('Synchrony') }" src="../assets/icons/lv.png" @click="toggleTool('Synchrony')" title="Synchronization (Green Arrow)" />
        <img class="sidebar-icon" :class="{ selected: isToolSelected('ExpOrder') }" src="../assets/icons/cheng.png" @click="toggleTool('ExpOrder')" title="ExpectOrder (Orange Arrow)" />
        <img class="sidebar-icon" :class="{ selected: isToolSelected('ExpEnable') }" src="../assets/icons/zi.png" @click="toggleTool('ExpEnable')" title="ExpectEnable (Purple Arrow)" />
      </div>

      <!-- Context/Problem Diagram Tools (Context Diagram tools usually stay click-to-draw or can be draggable too. Let's make them draggable for consistency if user wants "Arrow tools" distinct from others, but user specifically mentioned "Node tools" vs "Arrow tools". Context diagram tools are nodes. Let's make them draggable too.) -->
      <div class="icon-list flex-col" v-if="step < 2">
        <img class="sidebar-icon" src="../assets/img/machine.png" draggable="true" @dragstart="onDragStart($event, 'Machine')" title="Machine" />
        <img class="sidebar-icon" src="../assets/img/domain.png" draggable="true" @dragstart="onDragStart($event, 'Domain')" title="Problem Domain" />
        <img class="sidebar-icon" src="../assets/img/requirement.png" draggable="true" @dragstart="onDragStart($event, 'Requirement')" title="Requirement" />
        
        <!-- Link Tools for Context/Problem Diagram -->
        <img class="sidebar-icon" :class="{ selected: isToolSelected('Interface') }" src="../assets/img/interface.png" @click="toggleTool('Interface')" title="Interface" />
        <img class="sidebar-icon" :class="{ selected: isToolSelected('Reference') }" src="../assets/img/reference.png" @click="toggleTool('Reference')" title="Reference" />
        <img class="sidebar-icon" :class="{ selected: isToolSelected('Constraint') }" src="../assets/img/constraint.png" @click="toggleTool('Constraint')" title="Constraint" />
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useProjectStore } from '../store/project';
import { useEditorStore } from '../store/editor';

const projectStore = useProjectStore();
const editorStore = useEditorStore();

const step = computed(() => projectStore.step);

// Check if a specific tool is currently selected
const isToolSelected = (tool: string) => {
  return editorStore.tool === tool;
};

// Toggle tool selection (Click to select, Click again to deselect)
const toggleTool = (tool: string) => {
  if (editorStore.tool === tool) {
    editorStore.clearTool();
  } else {
    editorStore.selectTool(tool);
  }
};

// Handle Drag Start
const onDragStart = (event: DragEvent, tool: string) => {
  if (event.dataTransfer) {
    event.dataTransfer.setData('tool', tool);
    event.dataTransfer.effectAllowed = 'copy';
  }
};
</script>

<style scoped>
/* Reused from PF2-web1-main Sidebar.vue */
.box_1 {
  height: 100vh !important;
  width: clamp(80px, 4.8vw, 120px) !important;
  display: flex;
  flex-direction: column;
}
.box_2 {
  height: 100% !important;
  overflow: hidden !important;
  padding: 0.8vw 0.4vw;
}
.icon-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.2vw;
  margin-top: 1vw; /* Added some top margin */
  width: 100%;
  padding-bottom: 2vw; /* Responsive padding */
  overflow-y: auto !important; /* Allow scroll if needed */
  height: 100% !important;
  max-height: 100% !important;
}

.sidebar-icon {
  width: clamp(28px, 2.4vw, 40px); /* Responsive icon size */
  height: clamp(28px, 2.4vw, 40px);
  cursor: pointer;
  object-fit: contain;
  transition: transform 0.2s, border 0.2s;
  border-radius: 4px; /* Soften edges */
}

.sidebar-icon:hover {
  transform: scale(1.1);
}

.sidebar-icon.selected {
  border: 2px solid #007bff; /* Blue border for selection */
  background-color: rgba(0, 123, 255, 0.1);
  transform: scale(1.1);
}

/* Explicit scrollbar styling for better visibility */
.icon-list::-webkit-scrollbar {
  width: 8px;
}
.icon-list::-webkit-scrollbar-track {
  background: rgba(148, 172, 208, 0.2);
  border-radius: 4px;
}
.icon-list::-webkit-scrollbar-thumb {
  background: rgba(110, 174, 251, 0.8);
  border-radius: 4px;
}
.icon-list::-webkit-scrollbar-thumb:hover {
  background: rgba(64, 169, 255, 0.9);
}
</style>
