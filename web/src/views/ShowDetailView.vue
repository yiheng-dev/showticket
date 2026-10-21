<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { addSelection, getShow, searchShows } from '../api'
import EmptyState from '../components/EmptyState.vue'
import PosterCover from '../components/PosterCover.vue'
import ShowCard from '../components/ShowCard.vue'
import { auth } from '../store/auth'
import { toast } from '../store/toast'
import { errorMessage, formatDate, formatPrice } from '../utils/format'

const route = useRoute()
const router = useRouter()
const show = ref(null)
const related = ref([])
const loading = ref(true)
const error = ref('')
const qty = ref(1)
const adding = ref(false)

const stock = computed(() => Number(show.value?.stock) || 0)
const maxQty = computed(() => Math.max(1, Math.min(10, stock.value || 1)))
const soldOut = computed(() => stock.value <= 0)

async function load() {
  loading.value = true
  error.value = ''
  try {
    const { data } = await getShow(route.params.id)
    if (!data) {
      error.value = '演出不存在'
      return
    }
    show.value = data
    qty.value = 1
    loadRelated(data)
  } catch (err) {
    error.value = errorMessage(err, '演出加载失败')
  } finally {
    loading.value = false
  }
}

async function loadRelated(current) {
  try {
    const { data } = await searchShows({ genre: current.genre, pageSize: 6 })
    related.value = (data.list || []).filter((item) => String(item.id) !== String(current.id)).slice(0, 4)
  } catch {
    related.value = []
  }
}

function requireLogin() {
  if (auth.token) return true
  toast('请先登录后再选票', 'warn')
  router.push({ path: '/login', query: { redirect: route.fullPath } })
  return false
}

async function addToSelection(buyNow = false) {
  if (!requireLogin() || !show.value || soldOut.value) return
  adding.value = true
  try {
    await addSelection({
      showId: show.value.id,
      name: show.value.name,
      ticketType: show.value.ticketType,
      price: show.value.price,
      poster: show.value.poster,
      showTime: show.value.showTime,
      num: qty.value
    })
    if (buyNow) {
      toast('已加入选票清单，去确认订单吧', 'success')
      router.push('/selections')
    } else {
      toast(`已加入选票清单 × ${qty.value}`, 'success')
    }
  } catch (err) {
    toast(errorMessage(err, '加入选票清单失败'), 'error')
  } finally {
    adding.value = false
  }
}

onMounted(load)
</script>

<template>
  <section class="container section">
    <div v-if="loading" class="loading-block">
      <span class="spinner spinner-lg"></span>
      <span>正在调取演出信息…</span>
    </div>

    <EmptyState v-else-if="error" title="无法打开演出" :description="error">
      <RouterLink to="/" class="btn btn-ghost btn-sm">返回首页</RouterLink>
    </EmptyState>

    <template v-else-if="show">
      <div class="crumb">
        <RouterLink to="/" class="muted">演出</RouterLink>
        <span class="muted">/</span>
        <span>{{ show.genre }}</span>
      </div>

      <div class="detail-grid">
        <div class="poster-wrap">
          <PosterCover :show="show" size="large" />
          <div class="poster-meta panel">
            <div><span class="muted">已售</span><strong>{{ show.sold || 0 }}</strong></div>
            <div><span class="muted">评价</span><strong>{{ show.reviewCount || 0 }}</strong></div>
            <div><span class="muted">票档</span><strong>{{ show.ticketType || '--' }}</strong></div>
          </div>
        </div>

        <div class="info">
          <div class="row wrap">
            <span class="badge badge-gold">{{ show.genre }}</span>
            <span v-if="show.isFeatured" class="badge">推荐演出</span>
            <span class="badge" :class="{ danger: soldOut }">{{ soldOut ? '已售罄' : `余票 ${stock}` }}</span>
          </div>
          <h1 class="name">{{ show.name }}</h1>
          <p class="venue">{{ show.venue || '场馆待定' }}</p>

          <div class="facts">
            <div class="fact">
              <span class="fact-label">场次时间</span>
              <strong>{{ formatDate(show.showTime) }}</strong>
            </div>
            <div class="fact">
              <span class="fact-label">票档</span>
              <strong>{{ show.ticketType || '--' }}</strong>
            </div>
            <div class="fact">
              <span class="fact-label">演出类型</span>
              <strong>{{ show.genre }}</strong>
            </div>
          </div>

          <div class="buy-panel panel">
            <div class="between">
              <div>
                <span class="muted small">当前票价</span>
                <p class="price price-lg">{{ formatPrice(show.price) }}</p>
              </div>
              <div class="qty">
                <button class="qty-btn" :disabled="qty <= 1" @click="qty--">−</button>
                <span>{{ qty }}</span>
                <button class="qty-btn" :disabled="qty >= maxQty" @click="qty++">＋</button>
              </div>
            </div>
            <div class="actions">
              <button class="btn btn-primary grow" :disabled="adding || soldOut" @click="addToSelection(true)">
                {{ soldOut ? '已售罄' : '立即购票' }}
              </button>
              <button class="btn btn-ghost grow" :disabled="adding || soldOut" @click="addToSelection(false)">
                加入选票清单
              </button>
            </div>
            <p class="tip">支持余额支付 · 支付后自动出票 · 未支付订单超时自动关闭</p>
          </div>
        </div>
      </div>

      <section v-if="related.length" class="related">
        <div class="result-head">
          <div>
            <p class="eyebrow">MORE LIKE THIS</p>
            <h2 class="page-title">同类演出</h2>
          </div>
        </div>
        <div class="grid-shows">
          <ShowCard v-for="item in related" :key="item.id" :show="item" />
        </div>
      </section>
    </template>
  </section>
</template>

<style scoped>
.crumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  margin-bottom: 22px;
}

.detail-grid {
  display: grid;
  grid-template-columns: 380px minmax(0, 1fr);
  gap: 40px;
  align-items: start;
}

.poster-wrap {
  display: grid;
  gap: 16px;
}

.poster-meta {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  padding: 16px;
  gap: 10px;
  text-align: center;
}

.poster-meta div {
  display: grid;
  gap: 6px;
}

.poster-meta strong {
  font-size: 16px;
}

.name {
  margin-top: 18px;
  font-size: clamp(30px, 4.2vw, 50px);
  line-height: 1.12;
  letter-spacing: -0.03em;
  font-weight: 900;
}

.venue {
  margin-top: 12px;
  color: var(--muted);
  font-size: 14px;
}

.facts {
  margin-top: 26px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.fact {
  border: 1px solid var(--line);
  border-radius: 14px;
  padding: 14px 16px;
  background: rgba(255, 255, 255, 0.025);
  display: grid;
  gap: 7px;
}

.fact-label {
  font-size: 11px;
  color: var(--muted);
  letter-spacing: 0.08em;
}

.fact strong {
  font-size: 13px;
}

.buy-panel {
  margin-top: 24px;
  padding: 22px;
}

.qty {
  display: flex;
  align-items: center;
  gap: 12px;
  border: 1px solid var(--line-strong);
  border-radius: 12px;
  padding: 4px;
}

.qty-btn {
  width: 36px;
  height: 36px;
  border: 0;
  border-radius: 9px;
  background: rgba(255, 255, 255, 0.06);
  color: var(--text);
  font-size: 18px;
}

.qty span {
  min-width: 28px;
  text-align: center;
  font-weight: 700;
}

.actions {
  margin-top: 18px;
  display: flex;
  gap: 12px;
}

.tip {
  margin-top: 14px;
  font-size: 11px;
  color: var(--muted);
  text-align: center;
}

.badge.danger {
  color: #ff9ca0;
  border-color: rgba(229, 72, 77, 0.35);
  background: rgba(229, 72, 77, 0.1);
}

.related {
  margin-top: 64px;
}

.result-head {
  margin-bottom: 22px;
}

.result-head .page-title {
  margin-top: 8px;
  font-size: clamp(26px, 3.4vw, 38px);
}

@media (max-width: 900px) {
  .detail-grid {
    grid-template-columns: 1fr;
    gap: 26px;
  }

  .poster-wrap {
    max-width: 360px;
  }

  .facts {
    grid-template-columns: 1fr;
  }
}
</style>
