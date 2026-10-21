<script setup>
defineProps({
  title: { type: String, default: '暂无内容' },
  description: { type: String, default: '' }
})
</script>

<template>
  <div class="empty">
    <span class="empty-mark">票</span>
    <p class="empty-title">{{ title }}</p>
    <p v-if="description" class="empty-desc">{{ description }}</p>
    <slot />
  </div>
</template>

<style scoped>
.empty-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text);
}

.empty-desc {
  font-size: 13px;
  max-width: 420px;
  line-height: 1.7;
}
</style>
