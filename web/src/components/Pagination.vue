<script setup>
import { computed } from 'vue'

const props = defineProps({
  pageNo: { type: Number, default: 1 },
  pages: { type: Number, default: 1 },
  total: { type: Number, default: 0 }
})

const emit = defineEmits(['change'])

const pageList = computed(() => {
  const total = props.pages
  const current = props.pageNo
  if (total <= 7) {
    return Array.from({ length: total }, (_, i) => i + 1)
  }
  const set = new Set([1, total, current])
  if (current - 1 > 1) set.add(current - 1)
  if (current + 1 < total) set.add(current + 1)
  const list = Array.from(set).sort((a, b) => a - b)
  const result = []
  list.forEach((page, index) => {
    if (index > 0 && page - list[index - 1] > 1) result.push('...')
    result.push(page)
  })
  return result
})

function go(page) {
  if (page === '...' || page === props.pageNo || page < 1 || page > props.pages) return
  emit('change', page)
}
</script>

<template>
  <div v-if="pages > 1" class="pager">
    <button class="pager-btn" :disabled="pageNo <= 1" @click="go(pageNo - 1)">上一页</button>
    <template v-for="(page, index) in pageList" :key="`${page}-${index}`">
      <span v-if="page === '...'" class="dots">···</span>
      <button
        v-else
        class="pager-btn"
        :class="{ active: page === pageNo }"
        @click="go(page)"
      >
        {{ page }}
      </button>
    </template>
    <button class="pager-btn" :disabled="pageNo >= pages" @click="go(pageNo + 1)">下一页</button>
    <span class="total">共 {{ total }} 条</span>
  </div>
</template>

<style scoped>
.pager {
  margin-top: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}

.pager-btn {
  min-width: 38px;
  height: 38px;
  padding: 0 12px;
  border-radius: 10px;
  border: 1px solid var(--line);
  background: rgba(255, 255, 255, 0.03);
  color: var(--muted);
  font-size: 13px;
  font-weight: 600;
  transition: all 0.18s ease;
}

.pager-btn:hover:not(:disabled) {
  color: var(--text);
  border-color: var(--line-strong);
}

.pager-btn.active {
  color: #201708;
  background: linear-gradient(135deg, #ffe08a, var(--gold));
  border-color: transparent;
  font-weight: 800;
}

.dots {
  color: var(--muted);
  padding: 0 2px;
}

.total {
  margin-left: 8px;
  font-size: 12px;
  color: var(--muted);
}
</style>
