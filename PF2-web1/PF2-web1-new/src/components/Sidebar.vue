<template>
  <div class="box_1 flex-col">
    <!-- <img class="label_3" referrerpolicy="no-referrer" src="@/assets/img/87644858905e8b2f4a3d0024ea7322a3.png" /> -->
    <div class="box_2 flex-col">
      
      <!-- Draggable Icons List -->
      <div class="icon-list flex-col">
        <!-- 1. System -->
        <img class="sidebar-icon" referrerpolicy="no-referrer" src="@/assets/img/system.png" draggable="true" @dragstart="onDragStart($event, 'system')" title="System" />
        
        <!-- 2. External Entity -->
        <img class="sidebar-icon" referrerpolicy="no-referrer" src="@/assets/img/extentity.png" draggable="true" @dragstart="onDragStart($event, 'extentity')" title="External Entity" />
        
        <!-- 3. Intent -->
        <img class="sidebar-icon" referrerpolicy="no-referrer" src="@/assets/img/intent.png" draggable="true" @dragstart="onDragStart($event, 'intent')" title="Intent" />

        <!-- 4. Machine -->
        <img class="sidebar-icon" referrerpolicy="no-referrer" src="@/assets/img/machine.png" draggable="true" @dragstart="onDragStart($event, 'machine')" title="Machine" />
        
        <!-- 5. Problem Domain -->
        <img class="sidebar-icon" referrerpolicy="no-referrer" src="@/assets/img/domain.png" draggable="true" @dragstart="onDragStart($event, 'problemDomain')" title="Problem Domain" />
        
        <!-- 6. Requirement -->
        <img class="sidebar-icon" referrerpolicy="no-referrer" src="@/assets/img/requirement.png" draggable="true" @dragstart="onDragStart($event, 'requirement')" title="Requirement" />
        
        <!-- 7. Interface -->
        <img 
            class="sidebar-icon" 
            :class="{ 'active-icon': activeTool === 'interface' }"
            referrerpolicy="no-referrer" 
            src="@/assets/img/interface.png" 
            draggable="true" 
            @dragstart="onDragStart($event, 'interface')" 
            @click="onIconClick('interface')" 
            title="Interface" 
        />
        
        <!-- 8. Reference -->
        <img class="sidebar-icon" referrerpolicy="no-referrer" src="@/assets/img/reference.png" draggable="true" @dragstart="onDragStart($event, 'reference')" title="Reference" />

        <!-- 9. Constraint -->
        <img 
            class="sidebar-icon" 
            :class="{ 'active-icon': activeTool === 'constraint' }"
            referrerpolicy="no-referrer" 
            src="@/assets/img/constraint.png" 
            draggable="true" 
            @dragstart="onDragStart($event, 'constraint')" 
            @click="onIconClick('constraint')" 
            title="Constraint" 
        />
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
const emit = defineEmits(['tool-select']);
const props = defineProps<{
  activeTool?: string | null
}>();

const onDragStart = (event: DragEvent, nodeType: string) => {
  if (event.dataTransfer) {
    event.dataTransfer.setData('application/vueflow', nodeType);
    event.dataTransfer.effectAllowed = 'move';
  }
};

const onIconClick = (nodeType: string) => {
    emit('tool-select', nodeType);
};
</script>

<style scoped>
/* @import url("@/views/lanhu_huaban1kaobeifuben/assets/index.response.css"); */

.icon-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.5vw; /* Responsive gap */
  margin-top: 0;
  width: 100%;
  padding-bottom: 2vw; /* Responsive padding */
}

.sidebar-icon {
  width: 2.2vw; /* Responsive icon size */
  height: 2.2vw;
  cursor: grab;
  object-fit: contain;
  transition: transform 0.2s;
}

.sidebar-icon:hover {
  transform: scale(1.1);
}

.active-icon {
  transform: scale(1.2);
  filter: drop-shadow(0 0 5px rgba(0, 123, 255, 0.7));
  border: 2px solid #007bff;
  border-radius: 4px;
  background-color: rgba(0, 123, 255, 0.1);
}

.delete-icon {
  color: #ff4d4f;
  padding: 4px;
}

.delete-icon.active-icon {
  background-color: rgba(255, 77, 79, 0.1);
  border: 2px solid #ff4d4f;
}

.box_1 {
  /* Inherit layout styles or define here */
  width: 4.5vw !important; /* Responsive width */
  background-color: rgba(255, 255, 255, 0.5);
  border: 2px solid rgba(255, 255, 255, 1);
  border-radius: 16px;
  /* padding-bottom: 20px; */
  display: flex;
  flex-direction: column;
  justify-content: flex-start; /* Align to top */
  align-items: center;
  height: 71.93vw !important;
  margin: 2.6vw 0.8vw 0 1.6vw !important;
  padding-top: 2vw; /* Responsive top padding */
  box-sizing: border-box;
  overflow-y: auto; /* Allow scrolling if content overflows */
  flex-shrink: 0; /* Prevent shrinking */
}

.box_2 {
  align-items: center;
  /* padding-top: 20px; */
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  width: 100% !important;
  height: auto !important;
  margin: 0 !important;
}

.label_4 {
  width: 30px;
  height: 30px;
  margin-bottom: 20px;
  display: none; /* Hide if element still exists or just cleanup style */
}
</style>
