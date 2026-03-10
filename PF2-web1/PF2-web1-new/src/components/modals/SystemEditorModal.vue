<template>
  <div v-if="visible" class="modal-overlay">
    <div class="modal-content">
        <div class="modal-header">
            <h3>System Editor</h3>
            <button @click="cancel" class="close-btn">×</button>
        </div>
        <div class="modal-body">
            <div class="form-group">
                <label>Description</label>
                <textarea v-model="localDescription" rows="3"></textarea>
            </div>
            <div class="form-group">
                <label>ShortName</label>
                <input v-model="localShortName" type="text" />
            </div>
        </div>
        <div class="modal-footer">
            <button @click="confirm" class="confirm-btn">Confirm</button>
            <button @click="cancel" class="cancel-btn">Cancel</button>
        </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';

const props = defineProps<{
    visible: boolean;
    initialDescription?: string;
    initialShortName?: string;
}>();

const emit = defineEmits(['confirm', 'cancel', 'update:visible']);

const localDescription = ref('');
const localShortName = ref('');

watch(() => props.visible, (newVal) => {
    if (newVal) {
        localDescription.value = props.initialDescription || '';
        localShortName.value = props.initialShortName || '';
    }
});

const confirm = () => {
    emit('confirm', {
        description: localDescription.value,
        shortName: localShortName.value
    });
    emit('update:visible', false);
};

const cancel = () => {
    emit('cancel');
    emit('update:visible', false);
};
</script>

<style scoped>
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 2000;
}

.modal-content {
    background-color: white;
    padding: 20px;
    border-radius: 8px;
    width: 400px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    display: flex;
    flex-direction: column;
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.modal-header h3 {
    margin: 0;
    font-size: 18px;
    color: #333;
}

.close-btn {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    color: #999;
}

.form-group {
    margin-bottom: 15px;
}

.form-group label {
    display: block;
    margin-bottom: 5px;
    font-weight: 500;
    color: #333;
}

.form-group textarea,
.form-group input {
    width: 100%;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 4px;
    box-sizing: border-box;
    font-size: 14px;
}

.form-group textarea {
    resize: vertical;
}

.modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 10px;
}

.confirm-btn {
    background-color: #007bff;
    color: white;
    border: none;
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
}

.cancel-btn {
    background-color: #e0e0e0;
    color: #333;
    border: none;
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
}
</style>
