<template>
  <div class="system-node" :class="{ 'large-node': isLarge }">
    <Handle id="top" type="target" :position="Position.Top" />
    
    <div class="left-decoration">
        <div class="dashed-line"></div>
        <div class="dashed-line"></div>
    </div>
    
    <div class="content" :class="{ 'large-mode': isLarge }">
      <div class="name">{{ systemName }}</div>
      <div class="short-name">({{ systemShortName }})</div>
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

const systemName = computed(() => {
    return props.data?.system_name || props.label || 'System';
});

const systemShortName = computed(() => {
    return props.data?.system_shortName || 'S';
});

const isLarge = computed(() => {
    return (props.data?.system_w || 0) > 200;
});
</script>

<style scoped>
.system-node {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    color: black;
    /* Background and border are handled by the wrapper (.vue-flow__node) 
       which is set in lspStore to have dashed black border and light blue bg. */
}

.system-node.large-node {
    align-items: stretch;
}

.left-decoration {
    display: flex;
    height: 100%;
    padding: 0 8px;
    gap: 6px;
    /* border-right: 1px dashed black; Removed separator to match image */
    align-items: center;
    justify-content: center;
}

.dashed-line {
    width: 0;
    height: 100%;
    border-left: 1px dashed black;
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

.content.large-mode {
    align-items: flex-start;
    justify-content: flex-end;
    padding-left: 20px;
    padding-bottom: 20px;
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
