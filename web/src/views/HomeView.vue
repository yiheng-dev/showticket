<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { getShowPage, searchShows } from '../api'
import EmptyState from '../components/EmptyState.vue'
import Pagination from '../components/Pagination.vue'
import ShowCard from '../components/ShowCard.vue'
import { errorMessage } from '../utils/format'

const GENRES = ['演唱会', '音乐剧', '话剧', '音乐会', '相声', '舞剧', '儿童剧', '戏曲', '脱口秀', '音乐节']

const sorts = [
  { key: 'default', label: '默认排序' },
  { key: 'sold', label: '销量优先' },
  { key: 'priceAsc', label: '价格从低到高' },
  { key: 'priceDesc', label: '价格从高到低' },
  { key: 'newest', label: '最新上架' }
]

const keyword = ref('')
const genre = ref('')
const venue = ref('')
const sort = ref('default')
const pageNo = ref(1)
const pageSize = 12
const loading = ref(false)
const error = ref('')
const result = reactive({ total: 0, pages: 0, list: [] })
const venues = ref([])
const listAnchor = ref(null)

const totalNum = computed(() => Number(result.total) || 0)
const pagesNum = computed(() => Number(result.pages) || 0)
const hasFilters = computed(() => !!(keyword.value || genre.value || venue.value || sort.value !== 'default'))

function sortParams() {
  if (sort.value === 'sold') return { sortBy: 'sold', isAsc: false }
  if (sort.value === 'priceAsc') return { sortBy: 'price', isAsc: true }
  if (sort.value === 'priceDesc') return { sortBy: 'price', isAsc: false }
  if (sort.value === 'newest') return { sortBy: 'update_time', isAsc: false }
  return {}
}

async function loadShows() {
  loading.value = true
  error.value = ''
  try {
    const params = { pageNo: pageNo.value, pageSize, ...sortParams() }
    if (keyword.value) params.key = keyword.value
    if (genre.value) params.genre = genre.value
    if (venue.value) params.venue = venue.value
    const { data } = await searchShows(params)
    result.total = data.total || 0
    result.pages = data.pages || 0
    result.list = data.list || []
  } catch (err) {
    error.value = errorMessage(err, '演出加载失败，请稍后重试')
    result.list = []
    result.total = 0
    result.pages = 0
  } finally {
    loading.value = false
  }
}

async function loadFacets() {
  try {
    const { data } = await getShowPage({ pageNo: 1, pageSize: 300 })
    const set = new Set((data.list || []).map((item) => item.venue).filter(Boolean))
    venues.value = Array.from(set).sort((a, b) => a.localeCompare(b, 'zh-CN'))
  } catch {
    venues.value = []
  }
}

function applyFilters() {
  pageNo.value = 1
  loadShows()
  if (listAnchor.value) listAnchor.value.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

function setGenre(value) {
  genre.value = value
  applyFilters()
}

function resetFilters() {
  keyword.value = ''
  genre.value = ''
  venue.value = ''
  sort.value = 'default'
  applyFilters()
}

function changePage(page) {
  pageNo.value = page
  loadShows()
  if (listAnchor.value) listAnchor.value.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

onMounted(() => {
  loadFacets()
  loadShows()
})
</script>

<template>
  <div>
    <section class="hero">
      <div class="container hero-inner">
        <div class="hero-copy">
          <span class="eyebrow">LIVE · MUSICAL · THEATRE</span>
          <h1>发现下一场<br /><span>心动现场</span></h1>
          <p class="hero-sub">
            从万人演唱会到小剧场话剧，从古典音乐会到亲子儿童剧。<br />
            选好票档，锁定座位，把夜晚交给现场。
          </p>
          <form class="search" @submit.prevent="applyFilters">
            <input
              v-model.trim="keyword"
              class="input search-input"
              placeholder="搜索演出、艺人、话剧或场馆…"
              aria-label="搜索演出"
            />
            <button class="btn btn-primary" type="submit">搜索演出</button>
          </form>
          <div class="hero-stats">
            <div><strong>216+</strong><span>场演出</span></div>
            <div><strong>{{ GENRES.length }}</strong><span>种类型</span></div>
            <div><strong>{{ venues.length || 32 }}</strong><span>个场馆</span></div>
          </div>
        </div>
        <div class="hero-art" aria-hidden="true">
          <div class="art-card">
            <span class="art-genre">SHOWTICKET</span>
            <span class="art-title">ADMIT<br />ONE</span>
            <span class="art-line"></span>
            <span class="art-meta">LIVE / 2026–2028</span>
          </div>
          <div class="art-glow"></div>
        </div>
      </div>
    </section>

    <section class="container section">
      <div ref="listAnchor" class="filters">
        <div class="chips">
          <button class="chip" :class="{ active: genre === '' }" @click="setGenre('')">全部</button>
          <button
            v-for="item in GENRES"
            :key="item"
            class="chip"
            :class="{ active: genre === item }"
            @click="setGenre(item)"
          >
            {{ item }}
          </button>
        </div>
        <div class="filter-row">
          <select v-model="venue" class="select" aria-label="按场馆筛选" @change="applyFilters">
            <option value="">全部场馆</option>
            <option v-for="item in venues" :key="item" :value="item">{{ item }}</option>
          </select>
          <select v-model="sort" class="select" aria-label="排序方式" @change="applyFilters">
            <option v-for="item in sorts" :key="item.key" :value="item.key">{{ item.label }}</option>
          </select>
          <button v-if="hasFilters" class="btn btn-ghost btn-sm" @click="resetFilters">重置筛选</button>
        </div>
      </div>

      <div class="result-head">
        <div>
          <p class="eyebrow">NOW SHOWING</p>
          <h2 class="page-title">正在热演</h2>
        </div>
        <p class="sub" v-if="!loading">共 <strong class="count">{{ totalNum }}</strong> 场演出</p>
      </div>

      <div v-if="loading" class="grid-shows">
        <div v-for="i in 8" :key="i" class="skeleton"></div>
      </div>

      <EmptyState
        v-else-if="error"
        title="演出加载失败"
        :description="error"
      >
        <button class="btn btn-ghost btn-sm" @click="loadShows">重新加载</button>
      </EmptyState>

      <EmptyState
        v-else-if="!result.list.length"
        title="没有找到相关演出"
        description="换个关键词、类型或场馆再试试。"
      >
        <button class="btn btn-ghost btn-sm" @click="resetFilters">清空筛选</button>
      </EmptyState>

      <template v-else>
        <div class="grid-shows">
          <ShowCard v-for="show in result.list" :key="show.id" :show="show" />
        </div>
        <Pagination :page-no="pageNo" :pages="pagesNum" :total="totalNum" @change="changePage" />
      </template>
    </section>
  </div>
</template>

<style scoped>
.hero {
  padding: 62px 0 30px;
}

.hero-inner {
  display: grid;
  grid-template-columns: 1.15fr 0.85fr;
  gap: 40px;
  align-items: center;
}

.hero-copy h1 {
  margin-top: 16px;
  font-size: clamp(46px, 6.4vw, 82px);
  line-height: 1.02;
  letter-spacing: -0.05em;
  font-weight: 900;
}

.hero-copy h1 span {
  background: linear-gradient(120deg, #fff0bd 0%, var(--gold) 45%, #dfa63a 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.hero-sub {
  margin-top: 20px;
  color: var(--muted);
  line-height: 1.9;
  font-size: 14px;
}

.search {
  margin-top: 28px;
  display: flex;
  gap: 10px;
  max-width: 620px;
}

.search-input {
  min-height: 52px;
  border-radius: 14px;
  background: rgba(10, 10, 16, 0.8);
}

.search .btn {
  min-height: 52px;
  padding: 0 26px;
  border-radius: 14px;
}

.hero-stats {
  margin-top: 30px;
  display: flex;
  gap: 34px;
}

.hero-stats div {
  display: grid;
  gap: 5px;
}

.hero-stats strong {
  font-size: 24px;
  letter-spacing: -0.02em;
}

.hero-stats span {
  font-size: 11px;
  color: var(--muted);
  letter-spacing: 0.12em;
}

.hero-art {
  position: relative;
  min-height: 420px;
  display: grid;
  place-items: center;
}

.art-card {
  position: relative;
  width: min(320px, 90%);
  aspect-ratio: 3 / 4;
  border-radius: 24px;
  border: 1px solid rgba(243, 201, 105, 0.3);
  background:
    radial-gradient(120% 80% at 15% 0%, rgba(229, 72, 77, 0.5), transparent 58%),
    radial-gradient(100% 80% at 100% 100%, rgba(243, 201, 105, 0.25), transparent 60%),
    linear-gradient(160deg, #1c0c16, #0b0810 70%);
  box-shadow: 0 40px 90px rgba(0, 0, 0, 0.5);
  transform: rotate(-6deg);
  display: grid;
  align-content: space-between;
  padding: 28px;
  overflow: hidden;
}

.art-card::after {
  content: "";
  position: absolute;
  inset: 10px;
  border: 1px dashed rgba(255, 255, 255, 0.18);
  border-radius: 18px;
  pointer-events: none;
}

.art-genre {
  font-size: 11px;
  letter-spacing: 0.26em;
  color: var(--gold);
}

.art-title {
  font-size: clamp(38px, 5vw, 58px);
  line-height: 0.94;
  font-weight: 900;
  letter-spacing: -0.04em;
  color: rgba(255, 255, 255, 0.92);
}

.art-line {
  display: block;
  width: 46px;
  height: 2px;
  background: var(--gold);
}

.art-meta {
  font-size: 10px;
  letter-spacing: 0.18em;
  color: rgba(255, 255, 255, 0.55);
}

.art-glow {
  position: absolute;
  width: 280px;
  height: 280px;
  border-radius: 50%;
  filter: blur(70px);
  background: rgba(229, 72, 77, 0.22);
  z-index: -1;
}

.filters {
  padding-top: 10px;
  display: grid;
  gap: 16px;
}

.filter-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr) auto;
  gap: 12px;
  align-items: center;
}

.result-head {
  margin: 36px 0 22px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  flex-wrap: wrap;
}

.result-head .page-title {
  margin-top: 8px;
  font-size: clamp(28px, 3.6vw, 40px);
}

.count {
  color: var(--gold);
}

@media (max-width: 900px) {
  .hero-inner {
    grid-template-columns: 1fr;
  }

  .hero-art {
    display: none;
  }

  .filter-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 560px) {
  .search {
    flex-direction: column;
  }

  .hero-stats {
    gap: 22px;
  }
}
</style>
