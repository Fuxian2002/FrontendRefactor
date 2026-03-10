<script setup lang="ts">
import { useUIStore } from './store/ui';
import TopBar from './components/TopBar.vue';
import LeftBar from './components/LeftBar.vue';
import RightBar from './components/RightBar.vue';
import DrawingBoard from './components/DrawingBoard.vue';
import OpenProjectModal from './components/modals/OpenProjectModal.vue';

const uiStore = useUIStore();
</script>

<template>
  <div class="page flex-col">
    <div class="section_1 flex-row">
      <TopBar />
      <div class="group_4 flex-row">
        <template v-if="uiStore.currentView === 'projectDiagram'">
          <LeftBar />
          <DrawingBoard />
          <RightBar />
        </template>

        <template v-else-if="uiStore.currentView === 'traceText'">
          <div class="empty-view">Trace Text Editor</div>
        </template>

        <template v-else>
          <div class="empty-view">Select a view</div>
        </template>
      </div>
    </div>

    <OpenProjectModal />
  </div>
</template>

<style scoped>
.empty-view {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(0, 0, 0, 0.55);
}
</style>
