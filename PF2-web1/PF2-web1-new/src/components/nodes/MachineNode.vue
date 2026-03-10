<template>
  <div class="machine-node">
    <Handle id="top" type="target" :position="Position.Top" />
    
    <div class="left-decoration">
        <div class="solid-line"></div>
        <div class="solid-line"></div>
    </div>
    
    <div class="content">
        <div class="name">{{ machineName }}</div>
        <div class="short-name">({{ machineShortName }})</div>
    </div>
    <Handle id="bottom" type="source" :position="Position.Bottom" />
    <Handle id="left" type="target" :position="Position.Left" />
    <Handle id="right" type="source" :position="Position.Right" />
  </div>
</template>

<script setup lang="ts">
import { Handle, Position } from '@vue-flow/core';
import { computed } from 'vue';

const props = defineProps(['data', 'label']);

const machineName = computed(() => {
    return props.data?.machine_name || props.label || 'Machine';
});

const machineShortName = computed(() => {
    return props.data?.machine_shortName || 'M';
});
</script>

<style scoped>
.machine-node {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    color: black;
    /* Background and border are handled by the wrapper (.vue-flow__node) 
       or we can override here if needed. */
}

.left-decoration {
    display: flex;
    height: 100%;
    padding: 0 8px;
    gap: 6px;
    align-items: center;
    justify-content: center;
}

.solid-line {
    width: 0;
    height: 100%;
    border-left: 1px solid black;
}

.content {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    padding: 0 4px;
}

.name {
    font-size: 14px;
    font-weight: 700;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 100%;
}

.short-name {
    font-size: 12px;
    margin-top: 2px;
}
</style>
