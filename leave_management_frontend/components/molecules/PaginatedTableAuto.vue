<template>
  <div class="flex flex-col w-full space-y-2">
    <div class="flex">
      <div class="flex-1 w-full sm:w-1/3">
        <input v-model="searchQuery" type="text" placeholder="Tìm kiếm..."
          class="w-full text-black border border-gray-600 rounded px-3 py-1" />
      </div>
    </div>
    <!-- Bảng hiển thị danh sách -->
    <div class="flex mt-2">
      <EasyDataTable theme-color="#000000" class="w-full h-full" :headers="computedHeaders" :rows-per-page="10"
        rowsPerPageMessage="Số lượng mỗi trang:" rowsOfPageSeparatorMessage="/" :items="filteredItems"
        buttons-pagination border-cell multi-sort/>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from "vue";
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  ChevronDown,
} from 'lucide-vue-next'
const props = defineProps<{
  items: Record<string, any>[]; // Ép kiểu thành mảng object
  limit?: number;
}>();

// Tạo headers tự động từ items và thêm cột số thứ tự
const computedHeaders = computed(() => {
  if (!props.items || props.items.length === 0) return []; // Kiểm tra rỗng tránh lỗi

  // Thêm cột "STT" vào header
  return [
    { text: 'STT', value: 'stt', sortable: true }, // Cột số thứ tự
    ...Object.keys(props.items[0] as Record<string, any>).map((key) => ({
      text: key,   // Tiêu đề cột
      value: key,  // Giá trị map với dữ liệu
      sortable: true
    }))
  ];
});

// Tạo items mới với chỉ mục số thứ tự cho mỗi item
const itemsWithIndex = computed(() => {
  return props.items.map((item, index) => ({
    stt: index + 1, // Số thứ tự (bắt đầu từ 1)
    ...item         // Thêm các thuộc tính từ item
  }));
});

// Tìm kiếm
const searchQuery = ref('');

// Lọc items theo từ khóa tìm kiếm
const filteredItems = computed(() => {
  if (!searchQuery.value) return itemsWithIndex.value; // Nếu không có từ khóa tìm kiếm, trả về tất cả items

  // Lọc các item có giá trị chứa từ khóa tìm kiếm (tìm trên tất cả các trường)
  return itemsWithIndex.value.filter(item => {
    return Object.values(item).some(value =>
      value.toString().toLowerCase().includes(searchQuery.value.toLowerCase())
    );
  });
});

</script>


