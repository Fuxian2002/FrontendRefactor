<template>
  <div class="box_6 flex-col">
    <div v-if="!selectedNode" class="steps-container flex-col">
          <div class="text-wrapper_13 flex-col"><span class="text_16">步骤1：获取任务意图</span></div>
          <span class="text_17" :class="{ 'step-active': currentStep === '1.1' }">步骤1.1：标识系统</span>
          <span class="text_18" :class="{ 'step-active': currentStep === '1.2' }">步骤1.2：识别外部实体</span>
          <span class="text_19" :class="{ 'step-active': currentStep === '1.3' }">步骤1.3：识别任务意图</span>
          <span class="text_20" :class="{ 'step-active': currentStep === '1.4' }">步骤1.4：识别共享现象</span>
          <div class="text-wrapper_14 flex-col"><span class="text_21">步骤2：识别系统边界</span></div>
          <span class="text_22" :class="{ 'step-active': currentStep === '2.1' }">步骤2.1：识别设备并建立接口</span>
          <span class="text_23" :class="{ 'step-active': currentStep === '2.2' }">步骤2.2：识别交互内容</span>
          <span class="text_24" :class="{ 'step-active': currentStep === '2.3' }">步骤2.3：检查上下文图</span>
          <div class="text-wrapper_15 flex-col"><span class="text_25">步骤3：确定系统需求</span></div>
          <span class="text_26" :class="{ 'step-active': currentStep === '3.1' }">步骤3.1：识别需求</span>
          <span class="text_27" :class="{ 'step-active': currentStep === '3.2' }">步骤3.2：识别需求引用和需求约束</span>
          <div class="text-wrapper_16 flex-col"><span class="text_28" :class="{ 'step-active': currentStep === '4' }">步骤4：推导软件行为</span></div>
          <div class="text-wrapper_17 flex-col"><span class="text_29" :class="{ 'step-active': currentStep === '5' }">步骤5：检查问题图</span></div>
          <div class="group_5 flex-row justify-between">
            <div class="text-wrapper_18 flex-col" @click="prevStep">
              <span class="text_30">上一步</span>
            </div>
            <div class="text-wrapper_19 flex-col" @click="nextStep">
              <span class="text_31">{{ currentStep === '5' ? '检查' : '下一步' }}</span>
            </div>
          </div>
    </div>
    <div v-else class="property-form flex-col">
       <h3 class="prop-title">{{ getNodeTitle(selectedNode) }} 编辑</h3>
       
       <!-- System Node Specific Fields -->
       <template v-if="selectedNode.type === 'system'">
         <div class="field-group flex-col">
           <label>名称</label>
           <input v-model="selectedNode.data.system_name" class="prop-input" />
         </div>
         <div class="field-group flex-col">
           <label>简称</label>
           <input v-model="selectedNode.data.system_shortName" class="prop-input" />
         </div>
       </template>

       <!-- Machine Node Specific Fields -->
       <template v-else-if="selectedNode.type === 'machine'">
         <div class="field-group flex-col">
           <label>名称</label>
           <input v-model="selectedNode.data.machine_name" class="prop-input" />
         </div>
         <div class="field-group flex-col">
           <label>简称</label>
           <input v-model="selectedNode.data.machine_shortName" class="prop-input" />
         </div>
       </template>

       <!-- Problem Domain Node Specific Fields -->
      <template v-else-if="selectedNode.type === 'problemDomain'">
        <div class="field-group flex-col">
          <label>名称</label>
          <input v-model="selectedNode.data.problemdomain_name" class="prop-input" />
        </div>
        <div class="field-group flex-col">
          <label>简称</label>
          <input v-model="selectedNode.data.problemdomain_shortname" class="prop-input" />
        </div>
        <div class="field-group flex-col">
          <label>Domain Type</label>
          <select v-model="selectedNode.data.problemdomain_type" class="prop-input">
            <option v-for="type in domainTypes" :key="type" :value="type">{{ type }}</option>
          </select>
        </div>
        <div class="field-group flex-col">
          <label>Property</label>
          <select v-model="selectedNode.data.problemdomain_property" class="prop-input">
            <option value="GivenDomain">GivenDomain</option>
            <option value="DesignedDomain">DesignedDomain</option>
          </select>
        </div>
      </template>

      <!-- Requirement Node Specific Fields -->
      <template v-else-if="selectedNode.type === 'requirement'">
        <div class="field-group flex-col">
          <label>Description</label>
          <input v-model="selectedNode.data.requirement_context" class="prop-input" />
        </div>
        <div class="field-group flex-col">
          <label>ShortName</label>
          <input v-model="selectedNode.data.requirement_shortname" class="prop-input" />
        </div>
      </template>

      <!-- Constraint (Edge) Specific Fields -->
      <template v-else-if="selectedNode.data && selectedNode.data.type === 'constraint'">
        <div class="field-group flex-col">
          <label>Initiator</label>
          <select v-model="selectedNode.data.constraint_from" class="prop-input">
            <option v-for="name in availableNodes" :key="name" :value="name">{{ name }}</option>
          </select>
        </div>
        <div class="field-group flex-col">
          <label>Receiver</label>
           <select v-model="selectedNode.data.constraint_to" class="prop-input">
            <option :value="undefined"></option> <!-- Empty option -->
            <option v-for="name in availableNodes" :key="name" :value="name">{{ name }}</option>
          </select>
        </div>
        <div class="field-group flex-col">
          <label>Phenomenon</label>
          <input v-model="selectedNode.data.constraint_phenomenon" class="prop-input" />
        </div>
        <div class="field-group flex-col">
          <label>Type</label>
          <select v-model="selectedNode.data.constraint_type" class="prop-input">
             <option value="instruction">instruction</option>
             <option value="signal">signal</option>
             <option value="state">state</option>
             <option value="value">value</option>
          </select>
        </div>
        <div class="field-group flex-col">
          <label>PhenomenonList</label>
          <div class="phenomenon-container flex-col">
              <select multiple class="phenomenon-list" v-model="selectedPhenomenonIndices">
                  <option v-for="(item, index) in phenomenonListItems" :key="index" :value="index">
                      {{ item }}
                  </option>
              </select>
              <div class="phenomenon-buttons flex-row justify-between">
                  <button @click="addPhenomenon" class="list-btn add">添加</button>
                  <button @click="deletePhenomenon" class="list-btn delete">删除</button>
                  <button @click="confirmPhenomenon" class="list-btn confirm">确定</button>
              </div>
          </div>
        </div>
      </template>

      <!-- Interface (Edge) Specific Fields -->
      <template v-else-if="selectedNode.data && selectedNode.data.type === 'interface'">
        <div class="field-group flex-col">
          <label>Initiator</label>
          <select v-model="selectedNode.data.interface_from" class="prop-input">
            <option v-for="name in availableNodes" :key="name" :value="name">{{ name }}</option>
          </select>
        </div>
        <div class="field-group flex-col">
          <label>Phenomenon</label>
          <input v-model="selectedNode.data.interface_phenomenon" class="prop-input" />
        </div>
        <div class="field-group flex-col">
          <label>Type</label>
          <select v-model="selectedNode.data.interface_type" class="prop-input">
             <option value="instruction">instruction</option>
             <option value="signal">signal</option>
             <option value="state">state</option>
             <option value="value">value</option>
          </select>
        </div>
        <div class="field-group flex-col">
          <label>PhenomenonList</label>
          <div class="phenomenon-container flex-col">
              <select multiple class="phenomenon-list" v-model="selectedPhenomenonIndices">
                  <option v-for="(item, index) in phenomenonListItems" :key="index" :value="index">
                      {{ item }}
                  </option>
              </select>
              <div class="phenomenon-buttons flex-row justify-between">
                  <button @click="addPhenomenon" class="list-btn add">添加</button>
                  <button @click="deletePhenomenon" class="list-btn delete">删除</button>
                  <button @click="confirmPhenomenon" class="list-btn confirm">确定</button>
              </div>
          </div>
        </div>
      </template>

       <!-- Generic Node Fields -->
       <template v-else>
         <div class="field-group flex-col">
           <label>描述</label>
           <textarea v-model="selectedNode.data.description" class="prop-input" rows="4"></textarea>
         </div>
         <div class="field-group flex-col">
           <label>简称</label>
           <input v-model="selectedNode.data.shortName" class="prop-input" />
         </div>
       </template>

       <!-- Removed Bottom Confirm Button for Constraint and Interface -->
       <button v-if="!(selectedNode.data && (selectedNode.data.type === 'constraint' || selectedNode.data.type === 'interface'))" @click="saveProperties" class="prop-btn">确认</button>
    </div>

    <!-- Tabs -->
    <div class="group_6 flex-row">
      <div class="image-text_1 flex-row justify-between">
        <img class="thumbnail_5" referrerpolicy="no-referrer" src="@/assets/img/28e6d4b1dadf20143e9396ee6d55deae.png" />
        <span class="text-group_1">图</span>
      </div>
    </div>
    <div class="group_7 flex-row">
       <div class="image-text_2 flex-row justify-between">
         <img class="thumbnail_6" referrerpolicy="no-referrer" src="@/assets/img/8ee9cb390dc2e9e7beec22909649f6c6.png" />
         <span class="text-group_2">现象</span>
       </div>
    </div>
    <div class="group_8 flex-row">
      <div class="image-text_3 flex-row justify-between">
        <img class="thumbnail_7" referrerpolicy="no-referrer" src="@/assets/img/5285ed24b2b18eb95d89d589d5437a4c.png" />
        <span class="text-group_3">交互</span>
      </div>
    </div>
    <div class="group_9 flex-row">
      <div class="image-text_4 flex-row justify-between">
        <img class="thumbnail_8" referrerpolicy="no-referrer" src="@/assets/img/8ee9cb390dc2e9e7beec22909649f6c6.png" />
        <span class="text-group_4">引用</span>
      </div>
    </div>
    <div class="group_10 flex-row">
      <div class="image-text_5 flex-row justify-between">
        <img class="thumbnail_9" referrerpolicy="no-referrer" src="@/assets/img/28e6d4b1dadf20143e9396ee6d55deae.png" />
        <span class="text-group_5">其他信息</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { useLspStore } from '@/stores/lspStore';

const lspStore = useLspStore();

const props = defineProps<{
  selectedNode: any
}>();

const emit = defineEmits(['close']);

const getNodeTitle = (node: any) => {
    if (node.data && node.data.type === 'constraint') return 'Constraint';
    if (node.data && node.data.type === 'interface') return 'Interface';
    return node.label || node.type;
};

const domainTypes = ['Clock', 'Data Storage','Sensor','Actuator','Active Device','Passive Device'];

const availableNodes = computed(() => {
    return lspStore.elements.filter((el: any) => !el.source && !el.target) // Simple check for nodes vs edges
        .map((n: any) => {
            const data = n.data || {};
            return data.machine_shortName || 
                   data.problemdomain_shortname || 
                   data.system_shortName || 
                   data.requirement_shortname || 
                   n.label || n.id;
        });
});

// Phenomenon List Logic
const phenomenonListItems = ref<string[]>([]);
const selectedPhenomenonIndices = ref<number[]>([]);

watch(() => props.selectedNode, (newVal) => {
    if (newVal && newVal.data && newVal.data.type === 'constraint') {
        const listStr = newVal.data.constraint_phenomenon_list || "";
        phenomenonListItems.value = listStr.split('\n').filter((s: string) => s.trim() !== '');
    } else if (newVal && newVal.data && newVal.data.type === 'interface') {
        const listStr = newVal.data.interface_phenomenon_list || "";
        phenomenonListItems.value = listStr.split('\n').filter((s: string) => s.trim() !== '');
    } else {
        phenomenonListItems.value = [];
    }
}, { immediate: true });

const addPhenomenon = () => {
    const data = props.selectedNode.data;
    if (data.type === 'constraint') {
        if (!data.constraint_from || !data.constraint_phenomenon || !data.constraint_type) {
            alert("Please fill Initiator, Phenomenon, and Type first.");
            return;
        }
        const newItem = `${data.constraint_from}! ${data.constraint_phenomenon} ${data.constraint_type}`;
        phenomenonListItems.value.push(newItem);
    } else if (data.type === 'interface') {
        if (!data.interface_from || !data.interface_phenomenon || !data.interface_type) {
            alert("Please fill Initiator, Phenomenon, and Type first.");
            return;
        }
        const newItem = `${data.interface_from}! ${data.interface_phenomenon} ${data.interface_type}`;
        phenomenonListItems.value.push(newItem);
    }
};

const deletePhenomenon = () => {
    // Sort descending to remove without index shift issues
    const indices = [...selectedPhenomenonIndices.value].sort((a, b) => b - a);
    indices.forEach(index => {
        phenomenonListItems.value.splice(index, 1);
    });
    selectedPhenomenonIndices.value = [];
};

const confirmPhenomenon = () => {
    if (props.selectedNode.data.type === 'constraint') {
        props.selectedNode.data.constraint_phenomenon_list = phenomenonListItems.value.join('\n');
    } else if (props.selectedNode.data.type === 'interface') {
        props.selectedNode.data.interface_phenomenon_list = phenomenonListItems.value.join('\n');
    }
    lspStore.updateShape(props.selectedNode);
    // Also emit close to return to the steps view, matching standard "Confirm" behavior
    emit('close');
};

const steps = [
  '1.1', '1.2', '1.3', '1.4',
  '2.1', '2.2', '2.3',
  '3.1', '3.2',
  '4',
  '5'
];

const currentStep = ref('1.1');

const nextStep = async () => {
  const currentIndex = steps.indexOf(currentStep.value);
  
  // Special handling for Step 5 (Check Problem Diagram)
  if (currentStep.value === '5') {
      await lspStore.checkProblem();
      return;
  }

  if (currentIndex < steps.length - 1) {
    const nextStepValue = steps[currentIndex + 1]!;
    
    // Check if entering Step 2 (specifically 2.1)
    if (nextStepValue === '2.1') {
        lspStore.initStep2();
    }

    // Check if finishing Step 2 (2.3 -> 3.1)
    if (currentStep.value === '2.3' && nextStepValue === '3.1') {
        await lspStore.checkContext();
    }
    
    currentStep.value = nextStepValue;
  }
};

const prevStep = () => {
  const currentIndex = steps.indexOf(currentStep.value);
  if (currentIndex > 0) {
    currentStep.value = steps[currentIndex - 1]!;
  }
};

const isConstraint = (node: any) => node && node.data && node.data.type === 'constraint';

const saveProperties = () => {
  console.log('Properties saved:', props.selectedNode.data);
  // Data is bound to node, so it updates automatically in the graph model.
  // We emit an event to trigger backend sync via store helper
  lspStore.updateShape(props.selectedNode);
  emit('close');
};
</script>

<style scoped>
/* @import url("@/views/lanhu_huaban1kaobeifuben/assets/index.response.css"); */

.phenomenon-container {
    width: 100%;
}
.phenomenon-list {
    width: 100%;
    height: 100px;
    border: 1px solid #ccc;
    border-radius: 4px;
    margin-bottom: 5px;
    padding: 5px;
    background-color: white;
    color: black;
}
.phenomenon-buttons {
    width: 100%;
    gap: 10px;
    justify-content: flex-end;
}
.list-btn {
    padding: 6px 12px;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    color: white;
    font-size: 12px;
    flex: 1; /* Distribute space evenly */
    background: linear-gradient(135deg, #39EAFF 0%, #6E9EFF 50%, #0B58F5 100%);
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    transition: transform 0.1s, box-shadow 0.1s;
    display: flex;
    justify-content: center;
    align-items: center;
}

.list-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(45, 107, 255, 0.3);
}

.list-btn:active {
  transform: translateY(0);
  box-shadow: 0 1px 3px rgba(45, 107, 255, 0.2);
}

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

.property-form {
  padding: 1vw;
  color: #333; /* Changed from #fff to #333 for visibility on light background */
  flex: 1;
}
.prop-title {
  font-size: 1.2vw;
  margin-bottom: 1vw;
  color: #333; /* Changed from white to #333 */
}
.field-group {
  margin-bottom: 1vw;
}
.field-group label {
  font-size: 0.9vw;
  margin-bottom: 0.5vw;
}
.prop-input {
  width: 100%;
  padding: 0.5vw;
  border-radius: 4px;
  border: 1px solid #ccc;
  background: rgba(255, 255, 255, 0.9);
  color: #333;
  font-size: 0.9vw;
}
.prop-btn {
  padding: 0.5vw 1vw;
  background: #409eff;
  color: white;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 1vw;
  text-align: center;
}
.prop-btn.cancel {
  background: #909399;
  margin-top: 0.5vw;
}
.steps-container {
  flex: 1;
}
</style>
