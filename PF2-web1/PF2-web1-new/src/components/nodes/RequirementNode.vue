<template>
  <div class="requirement-node">
    <Handle id="top" type="target" :position="Position.Top" />
    <Handle id="bottom" type="source" :position="Position.Bottom" />
    <Handle id="left" type="target" :position="Position.Left" />
    <Handle id="right" type="source" :position="Position.Right" />
    
    <div class="content">
        <div class="name">{{ name }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Handle, Position } from '@vue-flow/core';
import { computed } from 'vue';

const props = defineProps(['data', 'label']);

const name = computed(() => props.data?.requirement_context || props.label || 'Requirement');
</script>

<style scoped>
.requirement-node {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: relative;
    box-sizing: border-box;
    border: 1px dashed black;
    border-radius: 50%;
    background-color: transparent;
    color: black;
}

.content {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 5px;
    width: 100%;
    height: 100%;
    justify-content: center;
    overflow: hidden;
}

.name {
    font-size: 14px;
    font-weight: 700;
    white-space: normal;
    word-break: break-word;
    text-align: center;
    max-width: 80%;
    max-height: 80%;
    overflow: hidden;
}
</style>
