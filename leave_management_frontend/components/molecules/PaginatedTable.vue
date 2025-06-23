<template>
  <div class="flex flex-col w-full space-y-2">
    <!-- Search input -->
    <div class="flex">
      <div class="flex-1 w-full sm:w-1/3">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Tìm kiếm..."
          class="w-full md:w-1/3 text-black border border-gray-600 rounded px-3 py-1"
        />
      </div>
    </div>

    <!-- Table display -->
    <div class="flex mt-2">
      <EasyDataTable
        theme-color="#000000"
        class="w-full h-full"
        :headers="headers"
        :rows-per-page="10"
        rowsPerPageMessage="Số lượng mỗi trang:"
        rowsOfPageSeparatorMessage="/"
        :items="filteredItems"
        buttons-pagination
        border-cell
        multi-sort
      >
        <!-- Actions -->
        <template #item-actions="{ id, is_active }">
          <div class="flex space-x-2">
            <NuxtLink :to="`/${entity}/${id}`">
              <button class="h-5 w-5">
                <img src="/images/edit_icon_table.png" alt="Edit" />
              </button>
            </NuxtLink>
            <button @click="handleAction(id, is_active)" class="h-6 w-6">
              <img
                :src="is_active === '✅ Active' ? '/images/delete_icon_table.png' : '/images/activate_icon.png'"
                :alt="is_active === '✅ Active' ? 'Delete' : 'Activate'"
              />
            </button>
          </div>
        </template>

        <!-- Order Actions -->
        <template #item-actions_order="{ id }">
          <div class="flex space-x-2">
            <NuxtLink :to="`/${entity}/${id}`">
              <button class="h-5 w-5">
                <img src="/images/edit_icon_table.png" alt="Edit" />
              </button>
            </NuxtLink>
          </div>
        </template>

        <!-- Product Main Image -->
        <template #item-mainimage="{ mainimage }">
          <img
            :src="mainimage || '/images/default_product.png'"
            alt="Product Image"
            class="w-20 h-20 object-cover"
            @error="onImageError"
          />
        </template>
      </EasyDataTable>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';

const props = defineProps({
  headers: { type: Array, required: true },
  items: { type: Array, required: true },
  totalItems: { type: Number, default: 0 },
  limit: { type: Number, default: 10 },
  entity: { type: String, required: true },
});

const emit = defineEmits(['delete', 'activate']);
const route = useRoute();

const searchQuery = ref('');

// Items ready to filter
const itemsWithIndex = computed(() => props.items);

// Filter items by search query
const filteredItems = computed(() => {
  if (!searchQuery.value) return itemsWithIndex.value;

  return itemsWithIndex.value.filter((item) =>
    Object.values(item).some((value) =>
      String(value).toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  );
});

// Handle activate or delete button
function handleAction(id: string | number, isActive: string) {
  emit(isActive === '✅ Active' ? 'delete' : 'activate', id);
}

// Handle image error
function onImageError(event: Event) {
  (event.target as HTMLImageElement).src = '/images/default_product.png';
}
</script>
