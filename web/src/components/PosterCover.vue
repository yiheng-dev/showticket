<script setup>
import { computed } from 'vue'
import { firstChar } from '../utils/format'

const props = defineProps({
  show: { type: Object, required: true },
  size: { type: String, default: 'card' }
})

const palettes = {
  演唱会: ['#681b36', '#260c1a', '#0a0810'],
  音乐剧: ['#4a215f', '#1d0d2a', '#0a0810'],
  话剧: ['#3a3320', '#17140d', '#0a0810'],
  音乐会: ['#1d3a5f', '#0d1728', '#080a10'],
  相声: ['#5b2d12', '#1f1006', '#0a0810'],
  舞剧: ['#12484a', '#071b1c', '#080a10'],
  儿童剧: ['#2b4770', '#101b2c', '#080a10'],
  戏曲: ['#4d2418', '#1b0c07', '#0a0810'],
  脱口秀: ['#523411', '#1c1205', '#0a0810'],
  音乐节: ['#5e1438', '#200817', '#0a0810']
}

const palette = computed(() => palettes[props.show.genre] || ['#31314a', '#141420', '#08080d'])
const background = computed(() => {
  const [a, b, c] = palette.value
  return `radial-gradient(120% 80% at 20% 0%, ${a} 0%, transparent 60%), radial-gradient(90% 70% at 100% 100%, ${b} 0%, transparent 65%), linear-gradient(160deg, ${b}, ${c})`
})
const hasImage = computed(() => /^https?:\/\//.test(props.show.poster || ''))
const initial = computed(() => firstChar(props.show.name))
</script>

<template>
  <div class="cover" :class="size" :style="{ background }">
    <img v-if="hasImage" class="cover-img" :src="show.poster" :alt="show.name" />
    <template v-else>
      <span class="ring ring-a"></span>
      <span class="ring ring-b"></span>
      <span class="initial">{{ initial }}</span>
      <span class="grain"></span>
    </template>
    <div class="top">
      <span class="genre">{{ show.genre || '演出' }}</span>
      <span v-if="show.isFeatured" class="featured">推荐</span>
    </div>
    <div class="bottom">
      <p class="name">{{ show.name }}</p>
      <p v-if="show.venue" class="venue">{{ show.venue }}</p>
    </div>
  </div>
</template>

<style scoped>
.cover {
  position: relative;
  overflow: hidden;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  isolation: isolate;
}

.cover.card {
  aspect-ratio: 3 / 4;
}

.cover.large {
  aspect-ratio: 4 / 5;
  border-radius: 20px;
}

.cover.mini {
  width: 96px;
  height: 120px;
  flex: none;
}

.cover-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.ring {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.12);
  z-index: 0;
}

.ring-a {
  width: 70%;
  aspect-ratio: 1;
  right: -18%;
  top: 8%;
}

.ring-b {
  width: 110%;
  aspect-ratio: 1;
  left: -40%;
  bottom: -34%;
  border-color: rgba(243, 201, 105, 0.14);
}

.initial {
  position: absolute;
  right: 6%;
  top: 18%;
  font-size: clamp(64px, 9vw, 150px);
  font-weight: 900;
  line-height: 1;
  color: rgba(255, 255, 255, 0.09);
  z-index: 0;
  user-select: none;
}

.grain {
  position: absolute;
  inset: 0;
  opacity: 0.22;
  background-image: radial-gradient(rgba(255, 255, 255, 0.22) 1px, transparent 1px);
  background-size: 18px 18px;
  mask-image: linear-gradient(180deg, #000, transparent 60%);
  z-index: 1;
}

.top,
.bottom {
  position: absolute;
  left: 14px;
  right: 14px;
  z-index: 2;
}

.top {
  top: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.genre,
.featured {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.08em;
  padding: 5px 9px;
  border-radius: 999px;
  backdrop-filter: blur(6px);
}

.genre {
  color: rgba(255, 255, 255, 0.86);
  background: rgba(0, 0, 0, 0.35);
  border: 1px solid rgba(255, 255, 255, 0.16);
}

.featured {
  color: #221706;
  background: linear-gradient(135deg, #ffe08a, #f3c969);
}

.bottom {
  bottom: 14px;
}

.name {
  font-size: 15px;
  font-weight: 700;
  line-height: 1.4;
  color: #fff;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.7);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.venue {
  margin-top: 5px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.64);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.mini .name {
  font-size: 12px;
  -webkit-line-clamp: 2;
}

.mini .venue,
.mini .initial {
  display: none;
}

.mini .top {
  top: 8px;
  left: 8px;
  right: 8px;
}

.mini .genre {
  font-size: 9px;
  padding: 3px 6px;
}

.mini .featured {
  display: none;
}

.large .name {
  font-size: clamp(18px, 2.4vw, 26px);
}

.large .venue {
  font-size: 13px;
}

.large .top,
.large .bottom {
  left: 20px;
  right: 20px;
}

.large .top {
  top: 20px;
}

.large .bottom {
  bottom: 20px;
}
</style>
