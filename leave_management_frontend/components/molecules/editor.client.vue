// components/TheCkeditor.client.vue
<template>
  <div>
    <ckeditor
      :editor="ClassicEditor"
      v-model="editorData"
      :config="editorConfig">
    </ckeditor>
  </div>
</template>

<script setup>
import CKEditor from '@ckeditor/ckeditor5-vue';
import ClassicEditor from '@ckeditor/ckeditor5-build-classic';

const ckeditor = defineComponent(CKEditor.component);
const editorData = ref('');
const editorConfig = {
  placeholder: 'Nhập nội dung trả lời'
}
</script>