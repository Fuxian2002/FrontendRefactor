<template>
  <div class="problem-domain-node">
    <Handle id="top" type="target" :position="Position.Top" />
    <Handle id="bottom" type="source" :position="Position.Bottom" />
    <Handle id="left" type="target" :position="Position.Left" />
    <Handle id="right" type="source" :position="Position.Right" />
    
    <div class="indicator">{{ indicatorText }}</div>

    <div class="content">
        <div class="name">{{ name }}</div>
        <div class="short-name">({{ shortName }})</div>
        <div class="type-label">{{ domainType }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Handle, Position } from '@vue-flow/core';
import { computed } from 'vue';

const props = defineProps(['data', 'label']);

const name = computed(() => props.data?.problemdomain_name || props.label || 'Problem Domain');
const shortName = computed(() => props.data?.problemdomain_shortname || 'PD');
const domainType = computed(() => props.data?.problemdomain_type || 'Causal');
const property = computed(() => props.data?.problemdomain_property || 'GivenDomain');

const indicatorText = computed(() => {
    if (property.value === 'GivenDomain') return 'G';
    if (property.value === 'DesignedDomain') return 'D';
    return '';
});
</script>

<style scoped>
.problem-domain-node {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    /* background-color: #ffffff; Removed to avoid overlapping rounded corners of the wrapper */
    position: relative;
    box-sizing: border-box;
    color: black;
}

.indicator {
    position: absolute;
    top: 2px;
    left: 2px;
    font-size: 10px;
    font-weight: bold;
    color: #666;
}

.content {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 5px;
    width: 100%;
    overflow: hidden;
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
}

.type-label {
    font-size: 10px;
    margin-top: 2px;
    color: #555;
}
</style>
