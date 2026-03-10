<script setup lang="ts">
import TopBar from './components/TopBar.vue';
import LeftBar from './components/LeftBar.vue';
import RightBar from './components/RightBar.vue';
import DrawingBoard from './components/DrawingBoard.vue';
import OpenProjectModal from './components/modals/OpenProjectModal.vue';
</script>

<template>
  <div class="page flex-col">
    <div class="section_1 flex-row">
      <TopBar />
      <div class="group_4 flex-row">
        <LeftBar />
        <DrawingBoard />
        <RightBar />
      </div>
    </div>

    <!-- Popups -->
    <OpenProjectModal />
  </div>
</template>

<style>
/* Global styles handled by theme.css and common.css */
</style>
