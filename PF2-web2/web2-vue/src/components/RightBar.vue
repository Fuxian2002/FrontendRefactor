<template>
  <div class="box_6 flex-col">
    <!-- Header -->
    <div class="process-header flex-col">
       <span class="process-title">组合验证过程</span>
    </div>

    <!-- Steps Container -->
    <div class="steps-container flex-col">
       <div class="current-sg" v-if="currentSgTitle">
         当前情景图：{{ currentSgTitle }}
       </div>
       
       <!-- Step 1 Header -->
       <div class="step-header" :class="{ 'step-active': store.step === 1 }" @click="store.setStep(1)">
         <span class="step-title">步骤1：读取问题图</span>
       </div>

       <!-- Step 2 Header -->
       <div class="step-header">
         <span class="step-title">步骤2：构建情景图</span>
       </div>
       
       <!-- Step 2.1 -->
       <div class="step-sub-item" :class="{ 'step-sub-active': store.step === 2 }" @click="store.setStep(2)">
         <span class="step-sub-text">2.1：绘制情景图</span>
       </div>

       <!-- Step 2.2 -->
       <div class="step-sub-item" :class="{ 'step-sub-active': store.step === 3 }" @click="store.setStep(3)">
         <span class="step-sub-text">2.2：检查情景图</span>
       </div>

       <!-- Buttons -->
       <div class="group_5 flex-row">
            <div class="text-wrapper_18" @click="prevStep">
              <span class="text_30">上一步</span>
            </div>
            <div class="text-wrapper_19" @click="nextStep">
              <span class="text_31">下一步</span>
            </div>
       </div>
    </div>

    <!-- Accordion -->
    <div class="accordion-container">
        <div class="group_6 flex-row">
          <div class="image-text_1 flex-row justify-between">
            <img class="thumbnail_5" src="../assets/img/28e6d4b1dadf20143e9396ee6d55deae.png" />
            <span class="text-group_1"> 图</span>
          </div>
        </div>
        <div class="group_7 flex-row">
           <div class="image-text_2 flex-row justify-between">
             <img class="thumbnail_6" src="../assets/img/8ee9cb390dc2e9e7beec22909649f6c6.png" />
             <span class="text-group_2"> 现象</span>
           </div>
        </div>
        <div class="group_8 flex-row">
          <div class="image-text_3 flex-row justify-between">
            <img class="thumbnail_7" src="../assets/img/5285ed24b2b18eb95d89d589d5437a4c.png" />
            <span class="text-group_3"> 接口</span>
          </div>
        </div>
        <div class="group_9 flex-row">
          <div class="image-text_4 flex-row justify-between">
            <img class="thumbnail_8" src="../assets/img/8ee9cb390dc2e9e7beec22909649f6c6.png" />
            <span class="text-group_4"> 引用</span>
          </div>
        </div>
        <div class="group_10 flex-row">
          <div class="image-text_5 flex-row justify-between">
            <img class="thumbnail_9" src="../assets/img/28e6d4b1dadf20143e9396ee6d55deae.png" />
            <span class="text-group_5"> 其他信息</span>
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
import { Node } from '../entity/Node';
import { CtrlNode } from '../entity/CtrlNode';
import { useEditorStore } from '../store/editor';
import { computed } from 'vue';

const store = useProjectStore();
const { project } = storeToRefs(store);
const editorStore = useEditorStore();
const { activeTab } = storeToRefs(editorStore);
const currentSgTitle = computed(() => {
  const tab = activeTab.value;
  if (tab && tab.endsWith('M')) return tab.slice(0, -1);
  return '';
});

// Helper to initialize Scenario Graph nodes from Problem Diagram
const initScenarioGraph = (proj: any) => {
  if (!proj.scenarioGraphList) {
    proj.scenarioGraphList = [];
  }
  
  // Create SG shells if missing
  if (proj.scenarioGraphList.length === 0 && proj.problemDiagram && proj.problemDiagram.requirementList) {
    proj.problemDiagram.requirementList.forEach((req: any) => {
      proj.scenarioGraphList.push({
        title: `SG${req.requirement_no}-${req.requirement_context}`,
        requirement: req.requirement_context, // Set the requirement field
        intNodeList: [],
        ctrlNodeList: [],
        lineList: []
      });
    });
  } else if (proj.scenarioGraphList.length > 0) {
      // Backfill requirement field if missing
      proj.scenarioGraphList.forEach((sg: any) => {
          if (!sg.requirement) {
              // Try to extract from title or find matching req
              const parts = sg.title.split('-');
              if (parts.length > 1) {
                  sg.requirement = parts.slice(1).join('-');
              }
              // If title doesn't match format, try to find matching requirement by context in problem diagram
              else if (proj.problemDiagram && proj.problemDiagram.requirementList) {
                  // Heuristic: check if title contains any requirement context
                  const req = proj.problemDiagram.requirementList.find((r: any) => sg.title.includes(r.requirement_context));
                  if (req) {
                      sg.requirement = req.requirement_context;
                  }
              }
          }
      });
  }

  // Populate SGs with nodes from phenomena
  proj.scenarioGraphList.forEach((sg: any) => {
    // Only populate if list is empty, to avoid overwriting existing layout
    if (sg.intNodeList && sg.intNodeList.length > 0) return; 

    let nodeList: Node[] = [];
    let ctrlNodeList: CtrlNode[] = [];
    let globalNodeNo = 1;
    const usedNos = new Set<number>();

    // Helper to get unique node no
    const getUniqueNodeNo = (preferredNo: number | undefined): number => {
        let no = preferredNo;
        if (!no || usedNos.has(no)) {
            no = globalNodeNo;
            while (usedNos.has(no)) {
                no = ++globalNodeNo;
            }
        }
        usedNos.add(no);
        return no;
    };

    // 1. Interfaces -> BehInt (Blue)
    if (proj.contextDiagram && proj.contextDiagram.interfaceList) {
      proj.contextDiagram.interfaceList.forEach((iface: any) => {
        if (iface.phenomenonList) {
          iface.phenomenonList.forEach((p: any) => {
            // Check uniqueness within the current SG
            const exists = nodeList.some(n => 
                n.pre_condition && 
                n.pre_condition.phenomenon_name === p.phenomenon_name &&
                n.pre_condition.phenomenon_from === p.phenomenon_from
            );
            if (!exists) {
                const node = new Node();
                // Use phenomenon_no from the project data if available and unique
                node.node_no = getUniqueNodeNo(p.phenomenon_no);
                node.node_type = 'BehInt';
                node.node_x = 150;
                node.node_y = 100 + (nodeList.length * 80);
                node.pre_condition = p;
                nodeList.push(node);
            }
          });
        }
      });
    }

    // 2. Constraints -> ExpInt (Blue)
    if (proj.problemDiagram && proj.problemDiagram.constraintList) {
      proj.problemDiagram.constraintList.forEach((cons: any) => {
        if (cons.phenomenonList) {
          cons.phenomenonList.forEach((p: any) => {
             // Check uniqueness
             const exists = nodeList.some(n => 
                n.pre_condition && 
                n.pre_condition.phenomenon_name === p.phenomenon_name &&
                n.pre_condition.phenomenon_from === p.phenomenon_from
             );
             if (!exists) {
                const node = new Node();
                // Use phenomenon_no from the project data if available and unique
                node.node_no = getUniqueNodeNo(p.phenomenon_no);
                node.node_type = 'ExpInt';
                node.node_x = 350;
                node.node_y = 100 + ((nodeList.length - (proj.contextDiagram?.interfaceList?.length || 0)) * 80); 
                // Simple fallback positioning
                if (node.node_y < 100) node.node_y = 100 + (nodeList.length * 80);
                
                node.pre_condition = p;
                nodeList.push(node);
             }
          });
        }
      });
    }
    
    // 3. Start Node
    // Removed automatic Start Node creation as per user request (should be dragged from toolbar)
    /*
    const startNode = new CtrlNode();
    startNode.node_type = 'Start';
    startNode.node_text = 'Start';
    startNode.node_x = 550;
    startNode.node_y = 200;
    ctrlNodeList.push(startNode);
    */

    sg.intNodeList = nodeList;
    sg.ctrlNodeList = ctrlNodeList;
    if (!sg.lineList) sg.lineList = [];
  });
};

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
        
        let hasError = false;
        let msg = "";
        
        if (errors && errors.length > 0) {
            errors.forEach((e) => {
                if (e.errorList && e.errorList.length > 0) {
                    hasError = true;
                    // Use title if available as prefix, though backend Error bean has title=null often
                    if (e.title) {
                         msg += `[${e.title}]\n`;
                    }
                    e.errorList.forEach((errStr) => {
                        msg += `- ${errStr}\n`;
                    });
                }
            });
        }

        if (hasError) {
            alert(msg);
        } else {
            // Only init SGs if they are completely missing or empty, to avoid overwriting user layout
            initScenarioGraph(project.value);
            store.setStep(2);
        }
    } catch (e) {
        console.error(e);
        alert("检查问题图时发生错误。");
    }
  } else if (store.step === 2) {
      if (project.value && project.value.scenarioGraphList) {
          const hasLines = project.value.scenarioGraphList.some((sg) =>
            sg.lineList && sg.lineList.length > 0
          );
          if (!hasLines) {
            alert("请先用箭头连接节点，至少绘制一条关系线。");
            return;
          }
          if (confirm("是否完成情景图的绘制？")) {
            store.setStep(3);
          }
      } else {
          alert("请先绘制情景图。");
      }
  } else if (store.step === 3) {
      try {
          const connected = project.value?.scenarioGraphList?.some(
            (sg) => sg.lineList && sg.lineList.length > 0
          );
          if (!connected) {
            alert("情景图尚未连接，无法检查。请绘制至少一条关系线。");
            return;
          }
          const errors = await checkCorrectness(project.value);
          let hasError = false;
          let msg = "";
          
          if (errors && errors.length > 0) {
              errors.forEach((e) => {
                  if (e.errorList && e.errorList.length > 0) {
                      hasError = true;
                      if (e.title) {
                          msg += `[${e.title}]\n`;
                      }
                      e.errorList.forEach((errStr) => {
                          msg += `- ${errStr}\n`;
                      });
                  }
              });
          }

          if (hasError) {
              alert(msg);
          } else {
              alert("情景图正确！");
          }
      } catch (e: any) {
          console.error(e);
          let errMsg = "Validation Error:\n";
          if (e.response && e.response.data && e.response.data.message) {
              errMsg += e.response.data.message;
          } else if (e.message) {
              errMsg += e.message;
          }
          alert(errMsg);
      }
  }
};
</script>

<style scoped>
.box_6 {
  margin: 2.6vw 1.6vw 0 0.8vw !important;
  background-color: #f0f5ff; /* Light blue background like screenshot */
  border-radius: 16px;
  width: 25vw;
  height: 94%; /* Fill most of parent height with small gap */
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
  display: flex; /* Enable flex for internal spacing */
  flex-direction: column;
}
.current-sg {
  margin-bottom: 0.8vw;
  padding: 0.6vw 1vw;
  background: rgba(255,255,255,0.95);
  border-radius: 10px;
  font-size: 1vw;
  font-weight: 600;
  color: #284B77;
  box-shadow: 0 2px 6px rgba(0,0,0,0.08);
}

/* Step Header (e.g., Step 1) */
.step-header {
  background-color: #fff;
  border-radius: 50px; /* Rounded pill shape */
  padding: 1vw 1.5vw;
  margin-bottom: 1vw;
  box-shadow: 0 4px 10px rgba(0,0,0,0.08); /* Softer, larger shadow */
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
}

.step-active {
  background-color: #fff; /* Keep white */
  /* Optional: Add active border or shadow if needed */
}

.step-title {
  font-size: 1.2vw;
  font-weight: 900; /* Extra bold */
  color: #000;
  font-family: 'Microsoft YaHei', sans-serif;
}

/* Sub Step Item (e.g., 2.1) */
.step-sub-item {
  padding: 0.8vw 1.5vw;
  margin-bottom: 0.8vw;
  cursor: pointer;
  border-radius: 50px; /* Match header pill shape */
  transition: all 0.2s;
  margin-left: 0; /* Align with headers */
}

.step-sub-active {
  background-image: linear-gradient(
    90deg,
    rgba(57, 234, 255, 1) 0%,
    rgba(110, 158, 255, 1) 100%
  );
  box-shadow: 0 4px 10px rgba(0,0,0,0.15); /* Shadow for active state */
}

.step-sub-text {
  font-size: 1.1vw;
  color: #555; /* Slightly lighter for inactive */
  font-family: 'Microsoft YaHei', sans-serif;
  margin-left: 0.5vw; /* Indent text slightly */
}

.step-sub-active .step-sub-text {
  color: #fff; /* White text */
  font-weight: bold;
  text-shadow: 0 1px 2px rgba(0,0,0,0.2);
}

/* Accordion Groups */
.accordion-container {
    padding-bottom: 1vw;
    overflow-x: hidden; /* Prevent horizontal scrollbar */
    display: none; /* Hide accordion section as per request to remove the bottom part */
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

/* Buttons - FIXED VERSION */
.group_5 {
    width: 100%;
    margin-top: auto; /* Push to bottom if space allows */
    margin-bottom: 1vw;
    padding: 1vw 1vw 0; /* Add top padding instead of margin-top */
    justify-content: center;
    gap: 1.5vw;
    display: flex;
    align-items: center;
    flex-shrink: 0; /* Prevent shrinking */
}

.text-wrapper_18, .text-wrapper_19 {
  width: 9vw !important;
  height: 2.8vw;
  /* 关键修复：使用flex布局并正确设置对齐方式 */
  display: flex;
  justify-content: center;
  align-items: center; /* 垂直居中 */
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1), 0 1px 3px rgba(0,0,0,0.08);
  /* 移除可能导致问题的line-height设置 */
  line-height: normal;
  /* 添加内边距调整，让文本视觉上稍微偏下一点点（如果需要） */
  padding-bottom: 0.1vw; /* 轻微向下调整 */
}

/* Inactive / Previous Button */
.text-wrapper_18 {
    background-color: rgba(146, 171, 207, 1);
}

/* Active / Next Button */
.text-wrapper_19 {
  background-image: linear-gradient(
    180deg,
    rgba(57, 234, 255, 1) 0%,
    rgba(110, 158, 255, 1) 50%,
    rgba(11, 88, 245, 1) 100%
  );
}

.text-wrapper_18:hover, .text-wrapper_19:hover {
  filter: brightness(1.1);
  transform: translateY(-1px);
  box-shadow: 0 6px 8px rgba(0,0,0,0.15), 0 3px 6px rgba(0,0,0,0.1);
}

.text-wrapper_18:active, .text-wrapper_19:active {
  transform: translateY(1px);
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.text_30, .text_31 {
  font-size: 1.1vw;
  font-weight: bold;
  color: #fff;
  font-family: 'Microsoft YaHei', sans-serif;
  /* 关键修复：移除所有可能影响垂直对齐的属性 */
  line-height: 1;
  display: block;
  /* 微调垂直位置，让文本稍微偏下（如果需要） */
  margin-top: 0.1vw; /* 可选：微调向下移动 */
  margin-left: 1.2vw;
}

/* Text group spans */
.text-group_1, .text-group_2, .text-group_3, .text-group_4, .text-group_5 {
  margin-left: 1vw;
  font-size: 1.1vw;
  font-weight: 500;
  color: #333; 
  font-family: 'Microsoft YaHei', sans-serif;
}
</style>
