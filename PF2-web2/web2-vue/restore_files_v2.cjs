const fs = require('fs');
const path = require('path');

const componentsPath = path.join(__dirname, 'src', 'components');

// Helper to convert string to UTF-8 buffer
function writeUtf8(filePath, content) {
    fs.writeFileSync(filePath, content, { encoding: 'utf8' });
    console.log(`Restored ${path.basename(filePath)}`);
}

// 1. TopBar.vue
// ????????????????????`??
// ???
// ???
const topBarContent = `<template>
  <div class="group_1 flex-col">
    <div class="group_2 flex-row">
      <span class="text_1">Insight</span>
      <span class="text_2">\u667A\u80FD\u5316\u9700\u6C42\u5EFA\u6A21\u5206\u6790\u89C4\u7EA6\u4E00\u4F53\u5316\u5E73\u53F0</span>
    </div>
    <div class="auth-buttons flex-row">
      <div class="auth-item flex-row">
        <!-- <img class="label_1" src="../assets/img/d40431bfb45d877dfb533659563e3882.png" /> -->
        <span class="text_3">\u767B\u5F55</span>
      </div>
      <div class="auth-divider"></div>
      <div class="auth-item flex-row">
        <!-- <img class="label_2" src="../assets/img/93cd7b6954e7ac55e1ded824eb2e1b83.png" /> -->
        <span class="text_4">\u6CE8\u518C</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// TopBar logic
</script>

<style scoped>
/* Styles are handled by theme.css */
</style>`;

// 2. RightBar.vue (Web1 Style Layout with Web2 Logic)
const rightBarContent = `<template>
  <div class="box_6 flex-col">
    <!-- Steps Container -->
    <div class="steps-container flex-col">
       
       <!-- Step 0 Header (Web2 Specific) -->
       <div class="text-wrapper_13 flex-col" v-if="store.step === 0">
         <span class="text_16">\u5EFA\u6A21\u6B65\u9AA4</span>
       </div>
       <span class="text_17" :class="{ 'step-active': store.step === 0 }" v-if="store.step === 0">
         \u6B65\u9AA40\uFF1A\u8BF7\u6839\u636E\u53F3\u4FA7\u5217\u8868\u7EE7\u7EED\u5EFA\u6A21
       </span>

       <!-- Step 1 Header -->
       <div class="text-wrapper_13 flex-col"><span class="text_16">\u6B65\u9AA41\uFF1A\u83B7\u53D6\u4EFB\u52A1\u610F\u56FE</span></div>
       <!-- Step 1 Substeps -->
       <span class="text_17" :class="{ 'step-active': store.step === 1 }">\u6B65\u9AA41.1\uFF1A\u6807\u8BC6\u7CFB\u7EDF</span>
       <span class="text_18" :class="{ 'step-active': store.step === 1 }">\u6B65\u9AA41.2\uFF1A\u8BC6\u522B\u5916\u90E8\u5B9E\u4F53</span>
       <span class="text_19" :class="{ 'step-active': store.step === 1 }">\u6B65\u9AA41.3\uFF1A\u8BC6\u522B\u4EFB\u52A1\u610F\u56FE</span>
       <span class="text_20" :class="{ 'step-active': store.step === 1 }">\u6B65\u9AA41.4\uFF1A\u8BC6\u522B\u5171\u4EAB\u73B0\u8C61</span>
       
       <!-- Step 2 Header -->
       <div class="text-wrapper_14 flex-col"><span class="text_21">\u6B65\u9AA42\uFF1A\u8BC6\u522B\u7CFB\u7EDF\u8FB9\u754C</span></div>
       <!-- Step 2 Substeps -->
       <span class="text_22" :class="{ 'step-active': store.step === 2 }">\u6B65\u9AA42.1\uFF1A\u8BC6\u522B\u8BBE\u5907\u5E76\u5EFA\u7ACB\u63A5\u53E3</span>
       <span class="text_23" :class="{ 'step-active': store.step === 2 }">\u6B65\u9AA42.2\uFF1A\u8BC6\u522B\u4EA4\u4E92\u5185\u5BB9</span>
       <span class="text_24" :class="{ 'step-active': store.step === 2 }">\u6B65\u9AA42.3\uFF1A\u68C0\u67E5\u4E0A\u4E0B\u6587\u56FE</span>

       <!-- Step 3 Header -->
       <div class="text-wrapper_15 flex-col"><span class="text_25">\u6B65\u9AA43\uFF1A\u786E\u5B9A\u7CFB\u7EDF\u9700\u6C42</span></div>
       <!-- Step 3 Substeps -->
       <span class="text_26" :class="{ 'step-active': store.step === 3 }">\u6B65\u9AA43.1\uFF1A\u8BC6\u522B\u9700\u6C42</span>
       <span class="text_27" :class="{ 'step-active': store.step === 3 }">\u6B65\u9AA43.2\uFF1A\u8BC6\u522B\u9700\u6C42\u5F15\u7528\u548C\u9700\u6C42\u7EA6\u675F</span>

       <!-- Step 4 Header -->
       <div class="text-wrapper_16 flex-col"><span class="text_28" :class="{ 'step-active': store.step === 4 }">\u6B65\u9AA44\uFF1A\u63A8\u5BFC\u8F6F\u4EF6\u884C\u4E3A</span></div>
       
       <!-- Step 5 Header -->
       <div class="text-wrapper_17 flex-col"><span class="text_29" :class="{ 'step-active': store.step === 5 }">\u6B65\u9AA45\uFF1A\u68C0\u67E5\u95EE\u9898\u56FE</span></div>
       
       <!-- Prev/Next Buttons -->
       <div class="group_5 flex-row justify-between">
            <div class="text-wrapper_18 flex-col" @click="prevStep">
              <span class="text_30">\u4E0A\u4E00\u6B65</span>
            </div>
            <div class="text-wrapper_19 flex-col" @click="nextStep">
              <span class="text_31">{{ store.step === 5 ? '\u5B8C\u6210' : '\u4E0B\u4E00\u6B65' }}</span>
            </div>
       </div>
    </div>

    <!-- Tabs (Accordion) -->
    <div class="group_6 flex-row">
      <div class="image-text_1 flex-row justify-between">
        <img class="thumbnail_5" src="../assets/img/28e6d4b1dadf20143e9396ee6d55deae.png" />
        <span class="text-group_1">\u56FE</span>
      </div>
    </div>
    <div class="group_7 flex-row">
       <div class="image-text_2 flex-row justify-between">
         <img class="thumbnail_6" src="../assets/img/8ee9cb390dc2e9e7beec22909649f6c6.png" />
         <span class="text-group_2">\u73B0\u8C61</span>
       </div>
    </div>
    <div class="group_8 flex-row">
      <div class="image-text_3 flex-row justify-between">
        <img class="thumbnail_7" src="../assets/img/5285ed24b2b18eb95d89d589d5437a4c.png" />
        <span class="text-group_3">\u4EA4\u4E92</span>
      </div>
    </div>
    <div class="group_9 flex-row">
      <div class="image-text_4 flex-row justify-between">
        <img class="thumbnail_8" src="../assets/img/8ee9cb390dc2e9e7beec22909649f6c6.png" />
        <span class="text-group_4">\u5F15\u7528</span>
      </div>
    </div>
    <div class="group_10 flex-row">
      <div class="image-text_5 flex-row justify-between">
        <img class="thumbnail_9" src="../assets/img/28e6d4b1dadf20143e9396ee6d55deae.png" />
        <span class="text-group_5">\u5176\u4ED6\u4FE1\u606F</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useProjectStore } from '../store/project';
// import { onMounted } from 'vue';

const store = useProjectStore();

// Removed onMounted force step set to allow Step 0

const prevStep = () => {
  if (store.step > 0) {
    store.setStep(store.step - 1);
  }
};

const nextStep = () => {
  if (store.step < 5) {
    store.setStep(store.step + 1);
  }
};
</script>

<style scoped>
.box_6 {
  /* Override global box_6 styles to reduce margins and fit layout */
  margin: 2.6vw 1.6vw 0 0.8vw !important;
  background-color: rgba(255, 255, 255, 0.5);
  border-radius: 16px;
  width: 25vw; /* Increased width to fit content */
  height: 71.93vw;
  border: 2px solid rgba(255, 255, 255, 1);
  justify-content: flex-center;
  overflow: hidden;
  flex-shrink: 0; /* Prevent shrinking */
}

/* Align groups and text wrappers */
.group_6, .group_7, .group_8, .group_9, .group_10,
.text-wrapper_13, .text-wrapper_14, .text-wrapper_15, .text-wrapper_16, .text-wrapper_17 {
  margin-left: 1.56vw !important;
  width: 21.88vw !important; /* Adjusted width to match container - padding */
}

/* Reduce height of step wrappers and ensure correct alignment */
.text-wrapper_13, .text-wrapper_14, .text-wrapper_15, .text-wrapper_16, .text-wrapper_17 {
  height: 3vw !important; /* Reduced from 3.86vw */
  display: flex !important;
  flex-direction: row !important; /* Override flex-col to allow left alignment */
  align-items: center !important; /* Vertically center */
  justify-content: flex-start !important; /* Left align */
}

/* Adjust text margins within wrappers */
.text_16, .text_21, .text_25, .text_28, .text_29 {
  margin-top: 0 !important; /* Remove top margin for vertical centering */
  margin-bottom: 0 !important; /* Remove bottom margin */
  width: auto !important;
  /* margin-left is inherited from global CSS (1.3vw) which combined with wrapper margin (1.56vw) = 2.86vw, matching sub-steps */
}

/* Adjust bottom button group width */
.group_5 {
  width: 21.88vw !important;
  margin-left: 1.56vw !important;
}

/* Adjust bottom buttons */
.text-wrapper_18, .text-wrapper_19 {
  width: 10.5vw !important;
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
  cursor: pointer !important;
  transition: all 0.2s;
}

.text-wrapper_18:hover, .text-wrapper_19:hover {
  opacity: 0.8;
  transform: translateY(-1px);
}

.text-wrapper_18:active, .text-wrapper_19:active {
  transform: translateY(0);
}

.step-active {
  color: #409eff !important;
  font-weight: bold !important;
  background-color: rgba(64, 158, 255, 0.1);
  padding: 0.2vw 0.5vw;
  border-radius: 4px;
}

/* Center text in bottom buttons */
.text_30, .text_31 {
  margin: 0 !important;
  width: auto !important;
}


/* Allow text wrapping for long step descriptions */
.text_27, .text_16, .text_17, .text_18, .text_19, .text_21, .text_22, .text_23, .text_24, .text_25, .text_26, .text_28, .text_29 {
  white-space: normal !important;
  height: auto !important;
  width: auto !important;
  max-width: 95%; /* Ensure it doesn't touch the edge */
}

.steps-container {
  flex: 1;
}
</style>`;

try {
  writeUtf8(path.join(componentsPath, 'TopBar.vue'), topBarContent);
  writeUtf8(path.join(componentsPath, 'RightBar.vue'), rightBarContent);
} catch (err) {
  console.error('Error writing files:', err);
}
