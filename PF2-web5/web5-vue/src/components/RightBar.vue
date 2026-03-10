<template>
  <div class="box_6 flex-col">
    <div class="rightbar-header">
      <span class="rightbar-title">web5-vue</span>
    </div>

    <div class="rightbar-body">
      <div class="steps-area">
        <div class="step-item pill" :class="{ active: store.step === 1 }" @click="store.setStep(1)">
          <span class="step-text">步骤1：打开项目</span>
        </div>

        <div class="step-item pill" :class="{ active: store.step === 2 }" @click="store.setStep(2)">
          <span class="step-text">步骤2：问题解耦</span>
        </div>

        <div class="step-item pill" :class="{ active: store.step === 3 || store.step === 4 }">
          <span class="step-text">步骤3：问题投影</span>
        </div>
        <div class="substeps">
          <div class="substep" :class="{ active: store.step === 3 }" @click="store.setStep(3)">
            <span class="substep-text">3.1：良构性检查</span>
          </div>
          <div class="substep" :class="{ active: store.step === 4 }" @click="store.setStep(4)">
            <span class="substep-text">3.2：投影</span>
          </div>
        </div>

        <div class="step-item pill" :class="{ active: store.step === 5 }" @click="store.setStep(5)">
          <span class="step-text">步骤4：获取追溯关系</span>
        </div>

        <div class="step-item pill" :class="{ active: store.step === 6 || store.step === 7 }">
          <span class="step-text">步骤5：获取依赖关系</span>
        </div>
        <div class="substeps">
          <div class="substep" :class="{ active: store.step === 6 }" @click="store.setStep(6)">
            <span class="substep-text">5.1：数据依赖</span>
          </div>
          <div class="substep" :class="{ active: store.step === 7 }" @click="store.setStep(7)">
            <span class="substep-text">5.2：控制依赖</span>
          </div>
        </div>

        <div class="button-group">
          <button class="btn prev-btn" @click="prevStep" :disabled="store.step === 1">上一步</button>
          <button class="btn next-btn" @click="nextStep">下一步</button>
        </div>
      </div>

      <div class="accordion-area">
        <div class="accordion-item">
          <div class="accordion-header" @click="open1 = !open1">{{ open1 ? '- 图' : '+ 图' }}</div>
          <div class="accordion-content" v-if="open1">
            <div v-if="project">
              <div class="info-row">{{ project.title }}</div>
              <div class="info-row" v-if="project.contextDiagram">{{ project.contextDiagram.title }}</div>
              <div class="info-row" v-if="project.problemDiagram">{{ project.problemDiagram.title }}</div>
              <div class="info-row" v-if="project.scenarioGraphList?.length">情景图：{{ project.scenarioGraphList.length }}</div>
              <div class="info-row" v-if="project.subProblemDiagramList?.length">子问题图：{{ project.subProblemDiagramList.length }}</div>
            </div>
          </div>
        </div>

        <div class="accordion-item">
          <div class="accordion-header" @click="open2 = !open2">{{ open2 ? '- 现象' : '+ 现象' }}</div>
          <div class="accordion-content" v-if="open2">
            <div v-for="phe in pheList" :key="phe.phenomenon_no" class="info-row">
              phe{{ phe.phenomenon_no }}: {{ phe.phenomenon_name }} ({{ phe.phenomenon_type }})
            </div>
          </div>
        </div>

        <div class="accordion-item">
          <div class="accordion-header" @click="open3 = !open3">{{ open3 ? '- 交互' : '+ 交互' }}</div>
          <div class="accordion-content" v-if="open3">
            <div v-for="phe in pheList" :key="phe.phenomenon_no" class="info-row">
              int{{ phe.phenomenon_no }}: {{ phe.phenomenon_from }} -> {{ phe.phenomenon_to }} : {{ phe.phenomenon_name }}
            </div>
          </div>
        </div>

        <div class="accordion-item">
          <div class="accordion-header" @click="open4 = !open4">{{ open4 ? '- 引用' : '+ 引用' }}</div>
          <div class="accordion-content" v-if="open4">
            <div v-for="phe in reqPheList" :key="phe.phenomenon_requirement" class="info-row">
              req{{ phe.phenomenon_requirement }}: {{ phe.phenomenon_from }} -> {{ phe.phenomenon_to }} [{{ phe.phenomenon_constraint }}]
            </div>
          </div>
        </div>

        <div class="accordion-item">
          <div class="accordion-header" @click="open5 = !open5">{{ open5 ? '- 其他信息' : '+ 其他信息' }}</div>
          <div class="accordion-content" v-if="open5">
            <div class="info-label">接口:</div>
            <div v-for="iface in project?.contextDiagram?.interfaceList" :key="iface.interface_description" class="info-row">
              {{ iface.interface_description }}
            </div>
            <div class="info-label">引用:</div>
            <div v-for="c in project?.problemDiagram?.constraintList" :key="c.constraint_description" class="info-row">
              {{ c.constraint_description }}
            </div>
            <div v-for="r in project?.problemDiagram?.referenceList" :key="r.reference_description" class="info-row">
              {{ r.reference_description }}
            </div>
            <div class="info-label">需求:</div>
            <div v-for="req in project?.problemDiagram?.requirementList" :key="req.requirement_no" class="info-row">
              req{{ req.requirement_no }}: {{ req.requirement_context }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { storeToRefs } from 'pinia';
import { useProjectStore } from '../store/project';
import {
  analyzeControlDependencies,
  analyzeDataDependencies,
  checkStrategy,
  checkTrace,
  checkWellFormed,
  getSubProblemDiagram,
  initTrace,
  recordLastProject
} from '../api/project';

const store = useProjectStore();
const { project } = storeToRefs(store);

const open1 = ref(false);
const open2 = ref(false);
const open3 = ref(false);
const open4 = ref(false);
const open5 = ref(false);

const pheList = computed(() => store.allPhenomena);
const reqPheList = ref<any[]>([]);

watch(
  project,
  async (newVal) => {
    if (!newVal) return;
    reqPheList.value = [];
  },
  { deep: true }
);

onMounted(() => {
  if (store.step === 0) store.setStep(1);
});

const prevStep = () => {
  if (store.step > 1) store.setStep(store.step - 1);
};

const nextStep = async () => {
  if (!project.value) {
    alert('请先打开项目');
    return;
  }

  try {
    switch (store.step) {
      case 1: {
        if (!project.value.traceList || project.value.traceList.length === 0) {
          const traces = await initTrace(project.value as any);
          if (traces) {
            store.setTraceList(traces as any);
            (project.value as any).traceList = traces;
          }
        }
        store.setStep(2);
        break;
      }

      case 2: {
        if (!project.value.scenarioGraphList || project.value.scenarioGraphList.length === 0) {
          alert('无情景图，无法进行问题解耦！');
          return;
        }
        const advice = await checkStrategy(project.value as any);
        if (advice && (advice as any[]).length > 0) {
          const firstAdvice = (advice as any[])[0];
          const accept = confirm(
            `策略建议：${firstAdvice.title}\n${firstAdvice.content ? firstAdvice.content.join('\n') : ''}\n\n是否接受建议？`
          );
          if (accept) {
            const record = {
              id: (project.value as any).id || 0,
              strategyId: '1',
              project: project.value
            };
            await recordLastProject(record);
            alert('已接受建议并记录项目。');
          }
        } else {
          alert('没有解耦建议。');
        }
        store.setStep(3);
        break;
      }

      case 3: {
        if (!project.value?.problemDiagram) {
          alert('请先打开项目并完成步骤1。');
          return;
        }
        const errors = await checkWellFormed(project.value as any);

        // 详细打印所有错误信息
        console.log("========== 良构性检查结果 ==========");
        console.log("错误数量:", errors?.length || 0);
        console.log("完整错误对象:", JSON.stringify(errors, null, 2));
        console.log("=====================================");

        if (errors && (errors as any[]).length > 0) {
          let msg = '❌ 发现良构性检查错误（共 ' + errors.length + ' 项）：\n\n';
          (errors as any[]).forEach((e, idx) => {
            console.log(`错误 ${idx + 1}:`, e);
            if (e.errorList && Array.isArray(e.errorList)) {
              msg += `【错误 ${idx + 1}】\n`;
              e.errorList.forEach((error: string) => {
                msg += `  • ${error}\n`;
              });
              msg += '\n';
            } else if (e.message) {
              msg += `【错误 ${idx + 1}】${e.message}\n\n`;
            } else {
              msg += `【错误 ${idx + 1}】${JSON.stringify(e)}\n\n`;
            }
          });
          msg += '请检查浏览器 Console 标签查看详细错误，然后修复对应的 XML 文件。';
          alert(msg);
          return;
        }
        // 检查成功，显示提示并进入下一步
        alert('✅ 良构性检查完成，未发现错误！');
        store.setStep(4);
        break;
      }

      case 4: {
        try {
          // Validate project before sending
          console.log("投影前的项目数据检查:");
          console.log("  project.value:", project.value);
          console.log("  project.value.title:", project.value?.title);
          console.log("  project.value.scenarioGraphList:", project.value?.scenarioGraphList);
          console.log("  scenarioGraphList length:", project.value?.scenarioGraphList?.length);

          if (!project.value) {
            alert('项目数据为空！');
            return;
          }

          console.log("发送投影请求，项目数据:", JSON.stringify(project.value, null, 2));
          const newProject = await getSubProblemDiagram(project.value as any);

          // Check if response is error response
          if ((newProject as any)?.error) {
            const errorMsg = (newProject as any).message || '未知错误';
            console.error('后端返回错误：', newProject);
            alert(`投影失败：${errorMsg}\n\n请检查浏览器控制台(F12)和后端日志获取详细信息`);
            return;
          }

          if (newProject) {
            // 方案 A：验证接收的数据
            console.log("========== 投影结果验证 (方案A) ==========");
            console.log("✅ 投影完成，接收到的 SubProblemDiagrams:");
            console.log("  数量:", (newProject as any).subProblemDiagramList?.length || 0);
            // Add detailed logging for each SPD
            if ((newProject as any).subProblemDiagramList) {
              (newProject as any).subProblemDiagramList.forEach((spd: any, idx: number) => {
                console.log(`\n  SPD${idx}: ${spd.title}`, {
                  machine: spd.machine?.machine_name,
                  problemDomains: spd.problemDomainList?.map((pd: any) => `${pd.problemdomain_name}(${pd.problemdomain_shortname})`) || [],
                  problemDomainCount: spd.problemDomainList?.length,
                  interfaces: spd.interfaceList?.map((i: any) => i.interface_name) || [],
                  interfaceCount: spd.interfaceList?.length,
                  requirement: spd.requirement?.requirement_context,
                  constraints: spd.constraintList?.length || 0,
                  references: spd.referenceList?.length || 0
                });
              });
            }
            console.log("=========================================");

            store.setProject(newProject as any);
            store.setSpdList((newProject as any).subProblemDiagramList);
            alert(`✅ 投影完成！\n已生成 ${(newProject as any).subProblemDiagramList?.length || 0} 个子问题图\n\n在图表标签页中可以查看`);
            store.setStep(5);
          } else {
            alert('投影失败：后端返回空结果');
          }
        } catch (e: any) {
          console.error('投影网络错误：', e);
          // 尝试提取更详细的错误信息
          let errorMsg = '未知错误';
          if (e.response?.data?.message) {
            errorMsg = e.response.data.message;
          } else if (e.response?.data?.error) {
            errorMsg = e.response.data.error;
          } else if (e.response?.data) {
            errorMsg = JSON.stringify(e.response.data);
          } else if (e.message) {
            errorMsg = e.message;
          }
          alert(`投影失败：${errorMsg}\n\n请检查浏览器控制台(F12)获取更多信息`);
          return;
        }
        break;
      }

      case 5: {
        const body = { id: (project.value as any).id, project: project.value };
        const traces = await checkTrace(body);
        if (traces) {
          store.setTraceList(traces as any);
          alert(`获取到 ${(traces as any[]).length} 条追溯关系。`);
        }
        store.setStep(6);
        break;
      }

      case 6: {
        const dataDeps = await analyzeDataDependencies(project.value as any);
        if (dataDeps) {
          store.setDataDependenceList(dataDeps as any);
          alert(`获取到 ${(dataDeps as any[]).length} 条数据依赖。`);
        } else {
          alert('数据依赖分析失败');
          return;
        }
        store.setStep(7);
        break;
      }

      case 7: {
        const ctrlDeps = await analyzeControlDependencies(project.value as any);
        if (ctrlDeps) {
          store.setControlDependenceList(ctrlDeps as any);
          alert(`获取到 ${(ctrlDeps as any[]).length} 条控制依赖。`);
        } else {
          alert('控制依赖分析失败');
          return;
        }
        alert('当前已是最终步骤！');
        break;
      }
    }
  } catch (e) {
    console.error(e);
    alert('操作失败，请检查控制台日志。');
  }
};
</script>

<style scoped>
.box_6 {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  min-height: 0;
}

.rightbar-header {
  flex: 0 0 auto;
  padding: 1.2vw 1.2vw 0.6vw;
}

.rightbar-title {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 3.86vw;
  background: transparent;
  border-radius: 0;
  font-size: 1.45vw;
  font-weight: 600;
  color: rgba(0, 0, 0, 0.85);
}

.rightbar-body {
  flex: 1 1 auto;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  padding: 0 1.2vw 1.2vw;
  gap: 1vw;
  min-height: 0;
}

.steps-area {
  flex: 0 0 auto;
  display: flex;
  flex-direction: column;
  gap: 0.8vw;
}

.step-item {
  padding: 0.4vw 0.2vw;
  cursor: pointer;
  user-select: none;
  background: transparent;
  border: none;
  border-radius: 0;
}

.step-item.pill {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 999px;
  padding: 0.9vw 1.1vw;
  border: 1px solid rgba(255, 255, 255, 0.65);
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  transition: all 0.25s;
}

.step-item.pill.active {
  background: linear-gradient(90deg, rgba(57, 234, 255, 1) 0%, rgba(110, 158, 255, 1) 100%);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
  border-color: transparent;
}

.step-item.pill.active .step-text {
  color: #fff;
  font-weight: 900;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.step-text {
  font-size: 1.1vw;
  font-weight: 600;
  color: #000;
}

.substeps {
  margin-top: 0.6vw;
  display: flex;
  flex-direction: column;
  gap: 0.5vw;
}

.substep {
  padding: 0.65vw 1.05vw;
  border-radius: 999px;
  background: transparent;
  border: none;
}

.substep.active {
  background: linear-gradient(90deg, rgba(57, 234, 255, 1) 0%, rgba(110, 158, 255, 1) 100%);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
}

.substep.active .substep-text {
  color: #fff;
  font-weight: bold;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.substep-text {
  font-size: 1vw;
  color: rgba(0, 0, 0, 0.75);
}

.button-group {
  display: flex;
  gap: 0.8vw;
  margin-top: 0.6vw;
}

.btn {
  flex: 1;
  height: 3.3vw;
  border: none;
  border-radius: 10px;
  font-size: 1.1vw;
  font-weight: 600;
  cursor: pointer;
  color: #fff;
}

.prev-btn {
  background: rgba(159, 168, 218, 0.95);
}

.next-btn {
  background: linear-gradient(90deg, rgba(41, 121, 255, 1) 0%, rgba(68, 138, 255, 1) 100%);
}

.btn:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.accordion-area {
  flex: 1 1 auto;
  overflow-y: auto;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
  gap: 0.6vw;
  padding-right: 0.4vw;
  min-height: 0;
}

.accordion-item {
  border-radius: 8px;
  overflow: hidden;
}

.accordion-header {
  background: rgba(54, 100, 139, 0.95);
  color: #fff;
  font-weight: 700;
  padding: 0.8vw 1vw;
  cursor: pointer;
}

.accordion-content {
  background: rgba(24, 116, 205, 0.95);
  color: rgba(255, 255, 255, 0.95);
  padding: 0.8vw 1vw;
  font-size: 0.95vw;
}

.info-row {
  padding: 0.4vw 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.18);
}

.info-label {
  margin-top: 0.6vw;
  font-weight: 700;
  color: rgba(207, 216, 220, 1);
}
</style>
