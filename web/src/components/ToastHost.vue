<script setup>
import { toasts } from '../store/toast'
</script>

<template>
  <div class="toast-host">
    <TransitionGroup name="toast">
      <div v-for="item in toasts" :key="item.id" class="toast" :class="item.type">
        {{ item.message }}
      </div>
    </TransitionGroup>
  </div>
</template>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateX(12px);
}
</style>
