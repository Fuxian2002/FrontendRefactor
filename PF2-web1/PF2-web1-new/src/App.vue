<script setup lang="ts">
import HomeView from './components/HomeView.vue';
</script>

<template>
  <HomeView />
</template>

<style>
body {
  margin: 0;
  padding: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden; /* Prevent body scroll if we want app-like feel */
}
#app {
  width: 100%;
  height: 100%;
}
</style>
