const fs = require('fs');
const path = require('path');

const componentsPath = path.join(__dirname, 'src', 'components');

// Helper to convert string to UTF-8 buffer
function writeUtf8(filePath, content) {
    fs.writeFileSync(filePath, content, { encoding: 'utf8' });
    console.log(`Restored ${path.basename(filePath)}`);
}

// RightBar.vue (Translated to Chinese)
// Compositional verification Process -> \u7EC4\u5408\u9A8C\u8BC1\u8FC7\u7A0B
// Step 1: Read the problem diagram -> \u6B65\u9AA41\uFF1A\u8BFB\u53D6\u95EE\u9898\u56FE
// Step 2: Construct the scenario graph -> \u6B65\u9AA42\uFF1A\u6784\u5EFA\u573A\u666F\u56FE
// 2.1: Draw scenario graph. -> 2.1\uFF1A\u7ED8\u5236\u573A\u666F\u56FE
// 2.2: Check the scenario graph. -> 2.2\uFF1A\u68C0\u67E5\u573A\u666F\u56FE
// Back -> \u4E0A\u4E00\u6B65
// Next -> \u4E0B\u4E00\u6B65
// + Diagram -> \u56FE
// + Phenomenon -> \u73B0\u8C61
// + Interface -> \u63A5\u53E3
// + Reference -> \u5F15\u7528
// + Other Information -> \u5176\u4ED6\u4FE1\u606F
// Problem Diagram has errors: -> \u95EE\u9898\u56FE\u5B58\u5728\u9519\u8BEF\uFF1A
// Please draw a scenario graph first. -> \u8BF7\u5148\u7ED8\u5236\u573A\u666F\u56FE\u3002
// Scenario Graph has errors: -> \u573A\u666F\u56FE\u5B58\u5728\u9519\u8BEF\uFF1A
// Scenario Graph is correct! -> \u573A\u666F\u56FE\u6B63\u786E\uFF01
// Error checking Problem Diagram. -> \u68C0\u67E5\u95EE\u9898\u56FE\u65F6\u53D1\u751F\u9519\u8BEF\u3002
// Error verifying Scenario Graph. -> \u9A8C\u8BC1\u573A\u666F\u56FE\u65F6\u53D1\u751F\u9519\u8BEF\u3002

const rightBarContent = `<template>
  <div class="box_6 flex-col">
    <!-- Header -->
    <div class="text-wrapper_13 flex-col" style="height: auto; min-height: 3vw; justify-content: center; padding: 0.5vw;">
       <span class="text_16" style="font-size: 1.2vw; font-weight: bold; text-align: center; color: #333;">\u7EC4\u5408\u9A8C\u8BC1\u8FC7\u7A0B</span>
    </div>

    <!-- Steps Container -->
    <div class="steps-container flex-col">
       
       <!-- Step 1 -->
       <div class="step-item" :class="{ 'step-active': store.step === 1 }" @click="store.setStep(1)">
         <span class="step-text">\u6B65\u9AA41\uFF1A\u8BFB\u53D6\u95EE\u9898\u56FE</span>
       </div>

       <!-- Step 2 Header -->
       <div class="step-header" style="margin-top: 1vw; margin-bottom: 0.5vw; padding-left: 0.5vw;">
         <span class="step-text" style="font-weight: bold; font-size: 1vw; color: #333;">\u6B65\u9AA42\uFF1A\u6784\u5EFA\u573A\u666F\u56FE</span>
       </div>
       
       <!-- Step 2.1 -->
       <div class="step-sub-item" :class="{ 'step-active': store.step === 2 }" @click="store.setStep(2)">
         <span class="step-text">2.1\uFF1A\u7ED8\u5236\u573A\u666F\u56FE</span>
       </div>

       <!-- Step 2.2 -->
       <div class="step-sub-item" :class="{ 'step-active': store.step === 3 }" @click="store.setStep(3)">
         <span class="step-text">2.2\uFF1A\u68C0\u67E5\u573A\u666F\u56FE</span>
       </div>

       <!-- Buttons -->
       <div class="group_5 flex-row justify-between" style="margin-top: 2vw;">
            <div class="text-wrapper_18 flex-col" @click="prevStep">
              <span class="text_30">\u4E0A\u4E00\u6B65</span>
            </div>
            <div class="text-wrapper_19 flex-col" @click="nextStep">
              <span class="text_31">\u4E0B\u4E00\u6B65</span>
            </div>
       </div>
    </div>

    <!-- Accordion -->
    <div class="group_6 flex-row">
      <div class="image-text_1 flex-row justify-between">
        <img class="thumbnail_5" src="../assets/img/28e6d4b1dadf20143e9396ee6d55deae.png" />
        <span class="text-group_1">+ \u56FE</span>
      </div>
    </div>
    <div class="group_7 flex-row">
       <div class="image-text_2 flex-row justify-between">
         <img class="thumbnail_6" src="../assets/img/8ee9cb390dc2e9e7beec22909649f6c6.png" />
         <span class="text-group_2">+ \u73B0\u8C61</span>
       </div>
    </div>
    <div class="group_8 flex-row">
      <div class="image-text_3 flex-row justify-between">
        <img class="thumbnail_7" src="../assets/img/5285ed24b2b18eb95d89d589d5437a4c.png" />
        <span class="text-group_3">+ \u63A5\u53E3</span>
      </div>
    </div>
    <div class="group_9 flex-row">
      <div class="image-text_4 flex-row justify-between">
        <img class="thumbnail_8" src="../assets/img/8ee9cb390dc2e9e7beec22909649f6c6.png" />
        <span class="text-group_4">+ \u5F15\u7528</span>
      </div>
    </div>
    <div class="group_10 flex-row">
      <div class="image-text_5 flex-row justify-between">
        <img class="thumbnail_9" src="../assets/img/28e6d4b1dadf20143e9396ee6d55deae.png" />
        <span class="text-group_5">+ \u5176\u4ED6\u4FE1\u606F</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useProjectStore } from '../store/project';
import { checkCorrectProblem, checkCorrectness } from '../api/project';
import { storeToRefs } from 'pinia';
import { onMounted } from 'vue';

const store = useProjectStore();
const { project } = storeToRefs(store);

onMounted(() => {
  if (store.step === 0) {
    store.setStep(1);
  }
});

const prevStep = () => {
  if (store.step > 1) {
    store.setStep(store.step - 1);
  }
};

const nextStep = async () => {
  if (store.step === 1) {
    // Step 1: Read Problem Diagram
    try {
        if (!project.value) {
            alert("Project data is missing.");
            return;
        }
        const errors = await checkCorrectProblem(project.value);
        if (errors && errors.length > 0) {
            let msg = "\u95EE\u9898\u56FE\u5B58\u5728\u9519\u8BEF\uFF1A\\n";
            errors.forEach((e) => msg += \`- \${e.description}\\n\`);
            alert(msg);
        } else {
            store.setStep(2);
        }
    } catch (e) {
        console.error(e);
        alert("\u68C0\u67E5\u95EE\u9898\u56FE\u65F6\u53D1\u751F\u9519\u8BEF\u3002");
    }
  } else if (store.step === 2) {
      // Step 2.1: Draw Scenario Graph
      if (project.value && project.value.scenarioGraphList) {
          const hasNodes = project.value.scenarioGraphList.some((sg) => 
            (sg.intNodeList && sg.intNodeList.length > 0) || 
            (sg.ctrlNodeList && sg.ctrlNodeList.length > 0)
          );
          
          if (hasNodes) {
             store.setStep(3);
          } else {
             alert("\u8BF7\u5148\u7ED8\u5236\u573A\u666F\u56FE\u3002");
          }
      } else {
          alert("\u8BF7\u5148\u7ED8\u5236\u573A\u666F\u56FE\u3002");
      }
  } else if (store.step === 3) {
      // Step 2.2: Check Scenario Graph
      try {
          const errors = await checkCorrectness(project.value);
          if (errors && errors.length > 0) {
              let msg = "\u573A\u666F\u56FE\u5B58\u5728\u9519\u8BEF\uFF1A\\n";
              errors.forEach((e) => msg += \`- \${e.description}\\n\`);
              alert(msg);
          } else {
              alert("\u573A\u666F\u56FE\u6B63\u786E\uFF01");
          }
      } catch (e) {
          console.error(e);
          alert("\u9A8C\u8BC1\u573A\u666F\u56FE\u65F6\u53D1\u751F\u9519\u8BEF\u3002");
      }
  }
};
</script>

<style scoped>
.box_6 {
  margin: 2.6vw 1.6vw 0 0.8vw !important;
  background-color: rgba(255, 255, 255, 0.5);
  border-radius: 16px;
  width: 25vw;
  height: 71.93vw;
  border: 2px solid rgba(255, 255, 255, 1);
  overflow: hidden;
  flex-shrink: 0;
}

.steps-container {
  flex: 1;
  padding: 1vw;
  overflow-y: auto;
}

.step-item {
  padding: 0.8vw;
  margin-bottom: 0.5vw;
  cursor: pointer;
  border-radius: 4px;
  background-color: rgba(255,255,255,0.5);
  transition: all 0.3s;
}

.step-sub-item {
  padding: 0.6vw;
  margin-bottom: 0.5vw;
  margin-left: 1.5vw;
  cursor: pointer;
  border-radius: 4px;
  background-color: rgba(255,255,255,0.3);
  transition: all 0.3s;
}

.step-active {
  background-color: #0d6efd !important; /* Bootstrap Primary Blue */
  color: white !important;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.step-text {
  font-size: 1vw;
  display: block;
  font-family: 'Microsoft YaHei', sans-serif;
}

/* Accordion Groups */
.group_6, .group_7, .group_8, .group_9, .group_10 {
  margin-left: 1.56vw !important;
  width: 21.88vw !important;
  margin-bottom: 0.5vw;
  cursor: pointer;
  display: flex;
  align-items: center;
}

/* Buttons */
.group_5 {
    width: 100%;
    margin-top: auto;
    padding-bottom: 1vw;
}

.text-wrapper_18, .text-wrapper_19 {
  width: 10vw !important;
  height: 3vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: white;
  border-radius: 8px;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  transition: transform 0.1s;
}

.text-wrapper_18:active, .text-wrapper_19:active {
  transform: scale(0.98);
}

.text_30, .text_31 {
  font-size: 1.2vw;
  font-weight: bold;
  color: #333;
  font-family: 'Microsoft YaHei', sans-serif;
}

/* Text group spans */
.text-group_1, .text-group_2, .text-group_3, .text-group_4, .text-group_5 {
  margin-left: 1vw;
  font-size: 1.1vw;
  font-weight: 500;
  color: #333;
  font-family: 'Microsoft YaHei', sans-serif;
}
</style>`;

try {
  writeUtf8(path.join(componentsPath, 'RightBar.vue'), rightBarContent);
} catch (err) {
  console.error('Error writing files:', err);
}
