<script setup>
import { computed, onMounted, ref } from 'vue'
import { getOrders } from '../api'
import EmptyState from '../components/EmptyState.vue'
import Pagination from '../components/Pagination.vue'
import { errorMessage, formatDate, formatPrice, formatStatus } from '../utils/format'

const orders = ref([])
const pageNo = ref(1)
const pageSize = 8
const total = ref(0)
const pages = ref(0)
const loading = ref(true)
const error = ref('')

const totalNum = computed(() => Number(total.value) || 0)
const pagesNum = computed(() => Number(pages.value) || 0)

async function load() {
  loading.value = true
  error.value = ''
  try {
    const { data } = await getOrders({ pageNo: pageNo.value, pageSize })
    orders.value = data.list || []
    total.value = data.total || 0
    pages.value = data.pages || 0
  } catch (err) {
    error.value = errorMessage(err, '订单加载失败')
    orders.value = []
  } finally {
    loading.value = false
  }
}

function changePage(page) {
  pageNo.value = page
  load()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(load)
</script>

<template>
  <section class="container section">
    <div class="page-head">
      <div>
        <span class="eyebrow">MY ORDERS</span>
        <h1 class="page-title">我的订单</h1>
        <p class="sub">查看购票记录、支付状态与电子票信息</p>
      </div>
      <RouterLink to="/" class="btn btn-ghost btn-sm">去选票</RouterLink>
    </div>

    <div v-if="loading" class="loading-block">
      <span class="spinner spinner-lg"></span>
      <span>正在加载订单…</span>
    </div>

    <EmptyState v-else-if="error" title="订单加载失败" :description="error">
      <button class="btn btn-ghost btn-sm" @click="load">重新加载</button>
    </EmptyState>

    <EmptyState
      v-else-if="!orders.length"
      title="还没有订单"
      description="选好演出后提交订单，就会在这里看到购票记录。"
    >
      <RouterLink to="/" class="btn btn-primary btn-sm">去发现演出</RouterLink>
    </EmptyState>

    <template v-else>
      <div class="order-list stack">
        <RouterLink v-for="order in orders" :key="order.id" :to="`/orders/${order.id}`" class="order panel">
          <div class="order-main">
            <div class="row wrap">
              <span class="status" :class="formatStatus(order.status).tone">{{ formatStatus(order.status).text }}</span>
              <span class="muted small">订单号 {{ order.id }}</span>
            </div>
            <div class="order-meta">
              <span>下单时间 {{ formatDate(order.createTime) }}</span>
              <span v-if="order.payTime">支付时间 {{ formatDate(order.payTime) }}</span>
              <span v-if="order.issueTime">出票时间 {{ formatDate(order.issueTime) }}</span>
            </div>
          </div>
          <div class="order-side">
            <span class="muted small">订单金额</span>
            <span class="price">{{ formatPrice(order.totalFee) }}</span>
            <span class="detail-link">查看详情 →</span>
          </div>
        </RouterLink>
      </div>
      <Pagination :page-no="pageNo" :pages="pagesNum" :total="totalNum" @change="changePage" />
    </template>
  </section>
</template>

<style scoped>
.order-list {
  gap: 14px;
}

.order {
  padding: 20px 22px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  transition: border-color 0.2s ease, transform 0.2s ease;
}

.order:hover {
  border-color: rgba(243, 201, 105, 0.3);
  transform: translateY(-2px);
}

.order-main {
  display: grid;
  gap: 12px;
  min-width: 0;
}

.order-meta {
  display: flex;
  gap: 18px;
  flex-wrap: wrap;
  font-size: 12px;
  color: var(--muted);
}

.order-side {
  display: grid;
  justify-items: end;
  gap: 6px;
  flex: none;
}

.detail-link {
  font-size: 12px;
  color: var(--gold);
}

@media (max-width: 640px) {
  .order {
    flex-direction: column;
    align-items: flex-start;
  }

  .order-side {
    justify-items: start;
    grid-auto-flow: column;
    align-items: center;
    gap: 12px;
  }
}
</style>
