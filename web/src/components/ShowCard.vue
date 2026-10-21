<script setup>
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import PosterCover from './PosterCover.vue'
import { formatDate, formatPrice } from '../utils/format'

const props = defineProps({
  show: { type: Object, required: true }
})

const stockText = computed(() => {
  if (props.show.stock === 0) return '已售罄'
  return `余票 ${props.show.stock}`
})
</script>

<template>
  <RouterLink :to="`/shows/${show.id}`" class="card">
    <PosterCover :show="show" />
    <div class="info">
      <div class="meta">
        <span class="badge">{{ show.ticketType || '票档' }}</span>
        <span class="stock" :class="{ out: show.stock === 0 }">{{ stockText }}</span>
      </div>
      <p class="venue">{{ show.venue || '场馆待定' }}</p>
      <div class="bottom">
        <div>
          <span class="price">{{ formatPrice(show.price) }}</span>
          <span class="from">起</span>
        </div>
        <span class="time">{{ formatDate(show.showTime) }}</span>
      </div>
    </div>
  </RouterLink>
</template>

<style scoped>
.card {
  display: block;
  border-radius: var(--radius);
  border: 1px solid var(--line);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.045), rgba(255, 255, 255, 0.02));
  padding: 10px;
  transition: transform 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
}

.card:hover {
  transform: translateY(-4px);
  border-color: rgba(243, 201, 105, 0.35);
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.35);
}

.info {
  padding: 12px 6px 6px;
}

.meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 8px;
}

.stock {
  font-size: 11px;
  color: var(--muted);
}

.stock.out {
  color: var(--red);
}

.venue {
  font-size: 12px;
  color: var(--muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.bottom {
  margin-top: 12px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 8px;
}

.price {
  font-size: 19px;
  font-weight: 800;
  color: var(--gold);
  font-variant-numeric: tabular-nums;
}

.from {
  margin-left: 3px;
  font-size: 11px;
  color: var(--muted);
}

.time {
  font-size: 11px;
  color: var(--muted);
  white-space: nowrap;
}
</style>
