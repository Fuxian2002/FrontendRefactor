const fs = require('fs');
const path = require('path');

const componentsPath = path.join(__dirname, 'src', 'components');

// Helper to convert string to UTF-8 buffer
function writeUtf8(filePath, content) {
    fs.writeFileSync(filePath, content, { encoding: 'utf8' });
    console.log(`Restored ${path.basename(filePath)}`);
}

// RightBar.vue (Styled to match Web1)
// Compositional verification Process -> \u7EC4\u5408\u9A8C\u8BC1\u8FC7\u7A0B
// Step 1: Read the problem diagram -> \u6B65\u9AA41\uFF1A\u8BFB\u53D6\u95EE\u9898\u56FE
// Step 2: Construct the scenario graph -> \u6B65\u9AA42\uFF1A\u6784\u5EFA\u60C5\u666F\u56FE
// 2.1: Draw scenario graph. -> 2.1\uFF1A\u7ED8\u5236\u60C5\u666F\u56FE
// 2.2: Check the scenario graph. -> 2.2\uFF1A\u68C0\u67E5\u60C5\u666F\u56FE
// Back -> \u4E0A\u4E00\u6B65
// Next -> \u4E0B\u4E00\u6B65
// + Diagram -> \u56FE
// + Phenomenon -> \u73B0\u8C61
// + Interface -> \u63A5\u53E3
// + Reference -> \u5F15\u7528
// + Other Information -> \u5176\u4ED6\u4FE1\u606F
// Problem Diagram has errors: -> \u95EE\u9898\u56FE\u5B58\u5728\u9519\u8BEF\uFF1A
// Please draw a scenario graph first. -> \u8BF7\u5148\u7ED8\u5236\u60C5\u666F\u56FE\u3002
// Scenario Graph has errors: -> \u60C5\u666F\u56FE\u5B58\u5728\u9519\u8BEF\uFF1A
// Scenario Graph is correct! -> \u60C5\u666F\u56FE\u6B63\u786E\uFF01
// Error checking Problem Diagram. -> \u68C0\u67E5\u95EE\u9898\u56FE\u65F6\u53D1\u751F\u9519\u8BEF\u3002
// Error verifying Scenario Graph. -> \u9A8C\u8BC1\u60C5\u666F\u56FE\u65F6\u53D1\u751F\u9519\u8BEF\u3002

const rightBarContent = `<template>
  <div class="box_6 flex-col">
    <!-- Header -->
    <div class="process-header flex-col">
       <span class="process-title">\u7EC4\u5408\u9A8C\u8BC1\u8FC7\u7A0B</span>
    </div>

    <!-- Steps Container -->
    <div class="steps-container flex-col">
       
       <!-- Step 1 Header -->
       <div class="step-header" :class="{ 'step-active': store.step === 1 }" @click="store.setStep(1)">
         <span class="step-title">\u6B65\u9AA41\uFF1A\u8BFB\u53D6\u95EE\u9898\u56FE</span>
       </div>

       <!-- Step 2 Header -->
       <div class="step-header">
         <span class="step-title">\u6B65\u9AA42\uFF1A\u6784\u5EFA\u60C5\u666F\u56FE</span>
       </div>
       
       <!-- Step 2.1 -->
       <div class="step-sub-item" :class="{ 'step-sub-active': store.step === 2 }" @click="store.setStep(2)">
         <span class="step-sub-text">2.1\uFF1A\u7ED8\u5236\u60C5\u666F\u56FE</span>
       </div>

       <!-- Step 2.2 -->
       <div class="step-sub-item" :class="{ 'step-sub-active': store.step === 3 }" @click="store.setStep(3)">
         <span class="step-sub-text">2.2\uFF1A\u68C0\u67E5\u60C5\u666F\u56FE</span>
       </div>

       <!-- Buttons -->
       <div class="group_5 flex-row justify-between">
            <div class="text-wrapper_18 flex-col" @click="prevStep">
              <span class="text_30">\u4E0A\u4E00\u6B65</span>
            </div>
            <div class="text-wrapper_19 flex-col" @click="nextStep">
              <span class="text_31">\u4E0B\u4E00\u6B65</span>
            </div>
       </div>
    </div>

    <!-- Accordion -->
    <div class="accordion-container">
        <div class="group_6 flex-row">
          <div class="image-text_1 flex-row justify-between">
            <img class="thumbnail_5" src="../assets/img/28e6d4b1dadf20143e9396ee6d55deae.png" />
            <span class="text-group_1"> \u56FE</span>
          </div>
        </div>
        <div class="group_7 flex-row">
           <div class="image-text_2 flex-row justify-between">
             <img class="thumbnail_6" src="../assets/img/8ee9cb390dc2e9e7beec22909649f6c6.png" />
             <span class="text-group_2"> \u73B0\u8C61</span>
           </div>
        </div>
        <div class="group_8 flex-row">
          <div class="image-text_3 flex-row justify-between">
            <img class="thumbnail_7" src="../assets/img/5285ed24b2b18eb95d89d589d5437a4c.png" />
            <span class="text-group_3"> \u63A5\u53E3</span>
          </div>
        </div>
        <div class="group_9 flex-row">
          <div class="image-text_4 flex-row justify-between">
            <img class="thumbnail_8" src="../assets/img/8ee9cb390dc2e9e7beec22909649f6c6.png" />
            <span class="text-group_4"> \u5F15\u7528</span>
          </div>
        </div>
        <div class="group_10 flex-row">
          <div class="image-text_5 flex-row justify-between">
            <img class="thumbnail_9" src="../assets/img/28e6d4b1dadf20143e9396ee6d55deae.png" />
            <span class="text-group_5"> \u5176\u4ED6\u4FE1\u606F</span>
          </div>
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
             alert("\u8BF7\u5148\u7ED8\u5236\u60C5\u666F\u56FE\u3002");
          }
      } else {
          alert("\u8BF7\u5148\u7ED8\u5236\u60C5\u666F\u56FE\u3002");
      }
  } else if (store.step === 3) {
      // Step 2.2: Check Scenario Graph
      try {
          const errors = await checkCorrectness(project.value);
          if (errors && errors.length > 0) {
              let msg = "\u60C5\u666F\u56FE\u5B58\u5728\u9519\u8BEF\uFF1A\\n";
              errors.forEach((e) => msg += \`- \${e.description}\\n\`);
              alert(msg);
          } else {
              alert("\u60C5\u666F\u56FE\u6B63\u786E\uFF01");
          }
      } catch (e) {
          console.error(e);
          alert("\u9A8C\u8BC1\u60C5\u666F\u56FE\u65F6\u53D1\u751F\u9519\u8BEF\u3002");
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
  display: flex;
  flex-direction: column;
}

/* Header Style */
.process-header {
  height: auto;
  min-height: 4vw;
  justify-content: center;
  align-items: center;
  padding: 1vw 0.5vw;
}

.process-title {
  font-size: 1.5vw; /* Larger font */
  font-weight: bold;
  text-align: center;
  color: #333;
  font-family: 'Microsoft YaHei', sans-serif;
  text-shadow: 1px 1px 2px rgba(255,255,255,0.8);
}

.steps-container {
  flex: 1;
  padding: 1vw;
  overflow-y: auto;
}

/* Step Header (e.g., Step 1) */
.step-header {
  background-color: #fff;
  border-radius: 50px; /* Rounded pill shape */
  padding: 0.8vw 1.5vw;
  margin-bottom: 0.8vw;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
  cursor: pointer;
  transition: all 0.3s;
}

.step-active {
  background-color: #fff; /* Keep white */
  /* Optional: Add active border or shadow if needed, but Web1 looks plain white */
}

.step-title {
  font-size: 1.1vw;
  font-weight: bold;
  color: #000;
  font-family: 'Microsoft YaHei', sans-serif;
}

/* Sub Step Item (e.g., 2.1) */
.step-sub-item {
  padding: 0.5vw 1.5vw;
  margin-bottom: 0.5vw;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
}

.step-sub-active {
  background-color: #e6f7ff; /* Light blue background */
}

.step-sub-text {
  font-size: 1vw;
  color: #333;
  font-family: 'Microsoft YaHei', sans-serif;
}

.step-sub-active .step-sub-text {
  color: #1890ff; /* Blue text */
  font-weight: bold;
}

/* Accordion Groups */
.accordion-container {
    padding-bottom: 1vw;
}

.group_6, .group_7, .group_8, .group_9, .group_10 {
  margin-left: 1.56vw !important;
  width: 21.88vw !important;
  margin-bottom: 0.5vw;
  cursor: pointer;
  display: flex;
  align-items: center;
  padding: 0.5vw 0;
}

/* Buttons */
.group_5 {
    width: 100%;
    margin-top: 2vw;
    margin-bottom: 1vw;
    padding: 0 1vw; /* Add padding inside container */
}

.text-wrapper_18, .text-wrapper_19 {
  width: 9vw !important; /* Slightly narrower */
  height: 2.8vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #1890ff; /* Blue background like Web1 screenshot */
  border-radius: 6px;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
  transition: all 0.2s;
}

.text-wrapper_18:hover, .text-wrapper_19:hover {
  background-color: #40a9ff;
}

.text-wrapper_18:active, .text-wrapper_19:active {
  transform: translateY(1px);
}

.text_30, .text_31 {
  font-size: 1.1vw;
  font-weight: bold;
  color: #fff; /* White text on blue button */
  font-family: 'Microsoft YaHei', sans-serif;
}

/* Text group spans */
.text-group_1, .text-group_2, .text-group_3, .text-group_4, .text-group_5 {
  margin-left: 1vw;
  font-size: 1.1vw;
  font-weight: 500;
  color: #fff; /* White text for accordion items based on contrast? Or black? Screenshot shows white text on blue background usually, but let's stick to theme.css text color which is usually dark on light bg. 
  Wait, Web1 sidebar usually has dark background? 
  The screenshot in Message 13 shows a light blue container background. 
  The text in accordion seems to be white in the original Web1 sidebar if it was blue. 
  But here box_6 is rgba(255, 255, 255, 0.5). 
  Let's keep text dark for now unless user complains. */
  color: #333; 
  font-family: 'Microsoft YaHei', sans-serif;
}
</style>`;

try {
  writeUtf8(path.join(componentsPath, 'RightBar.vue'), rightBarContent);
} catch (err) {
  console.error('Error writing files:', err);
}
