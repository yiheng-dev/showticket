<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { cancelOrder, createPayOrder, getMe, getOrder, payByBalance } from '../api'
import EmptyState from '../components/EmptyState.vue'
import PosterCover from '../components/PosterCover.vue'
import { updateUser } from '../store/auth'
import { toast } from '../store/toast'
import { errorMessage, formatDate, formatPrice, formatStatus } from '../utils/format'

const route = useRoute()
const order = ref(null)
const loading = ref(true)
const error = ref('')
const password = ref('123456')
const paying = ref(false)
const canceling = ref(false)
const justPaid = ref(false)
const syncing = ref(false)

const tickets = computed(() => order.value?.tickets || [])
const delivery = computed(() => order.value?.delivery || null)
const statusInfo = computed(() => formatStatus(order.value?.status))
const canPay = computed(() => Number(order.value?.status) === 1 && !justPaid.value)
const paid = computed(() => {
  const status = Number(order.value?.status)
  return status === 2 || status === 3 || status === 4 || status === 6
})
const pickupReady = computed(() => paid.value && !!delivery.value?.pickupCode)

const sleep = (ms) => new Promise((resolve) => window.setTimeout(resolve, ms))

async function load() {
  loading.value = true
  error.value = ''
  try {
    const { data } = await getOrder(route.params.id)
    order.value = data
  } catch (err) {
    error.value = errorMessage(err, '订单加载失败')
  } finally {
    loading.value = false
  }
}

async function refreshMe() {
  try {
    const { data } = await getMe()
    updateUser({ userId: data.userId, username: data.username, phone: data.phone, balance: data.balance })
  } catch {
    // 余额刷新失败不影响订单展示
  }
}

async function pollStatus() {
  syncing.value = true
  for (let i = 0; i < 15; i += 1) {
    await sleep(2000)
    try {
      const { data } = await getOrder(route.params.id)
      order.value = data
      if (Number(data.status) !== 1) {
        syncing.value = false
        return
      }
    } catch {
      // 忽略轮询中的临时错误
    }
  }
  syncing.value = false
  toast('支付已完成，订单状态同步中，请稍后刷新页面', 'warn', 4500)
}

async function pay() {
  if (!order.value || !canPay.value) return
  if (!password.value) {
    toast('请输入支付密码', 'warn')
    return
  }
  paying.value = true
  try {
    const applyRes = await createPayOrder({
      bizOrderNo: order.value.id,
      amount: order.value.totalFee,
      payChannelCode: 'balance',
      payType: 5,
      orderInfo: `购票订单 ${order.value.id}`
    })
    const payOrderId = String(applyRes.data)
    await payByBalance(payOrderId, password.value)
    justPaid.value = true
    toast('支付成功，正在为你出票…', 'success')
    refreshMe()
    pollStatus()
  } catch (err) {
    toast(errorMessage(err, '支付失败，请检查余额或稍后重试'), 'error', 4500)
  } finally {
    paying.value = false
  }
}

async function cancel() {
  if (!order.value || !canPay.value) return
  if (!window.confirm('确定取消这笔订单吗？取消后余票会立即释放。')) return
  canceling.value = true
  try {
    await cancelOrder(order.value.id)
    toast('订单已取消，余票已释放', 'success')
    await load()
  } catch (err) {
    toast(errorMessage(err, '取消订单失败'), 'error', 4500)
  } finally {
    canceling.value = false
  }
}

onMounted(load)
</script>

<template>
  <section class="container section">
    <div class="page-head">
      <div>
        <span class="eyebrow">ORDER DETAIL</span>
        <h1 class="page-title">订单详情</h1>
        <p class="sub" v-if="order">订单号 {{ order.id }}</p>
      </div>
      <div class="row gap-8">
        <button class="btn btn-ghost btn-sm" @click="load">刷新订单</button>
        <RouterLink to="/orders" class="btn btn-ghost btn-sm">返回订单列表</RouterLink>
      </div>
    </div>

    <div v-if="loading" class="loading-block">
      <span class="spinner spinner-lg"></span>
      <span>正在读取订单…</span>
    </div>

    <EmptyState v-else-if="error" title="无法打开订单" :description="error">
      <button class="btn btn-ghost btn-sm" @click="load">重新加载</button>
    </EmptyState>

    <div v-else-if="order" class="detail-grid">
      <div class="stack">
        <div class="panel panel-pad">
          <div class="between">
            <h2 class="block-title">演出票</h2>
            <span class="muted small">{{ tickets.length }} 项</span>
          </div>
          <div class="divider"></div>
          <div v-if="tickets.length" class="ticket-list">
            <div v-for="ticket in tickets" :key="ticket.id" class="ticket">
              <PosterCover :show="ticket" size="mini" />
              <div class="ticket-main">
                <h3>{{ ticket.name }}</h3>
                <p class="sub">{{ ticket.ticketType }} · {{ formatDate(ticket.showTime) }}</p>
              </div>
              <div class="ticket-side">
                <span class="muted small">× {{ ticket.num }}</span>
                <span class="price">{{ formatPrice(ticket.price) }}</span>
              </div>
            </div>
          </div>
          <p v-else class="muted small">暂无票明细</p>
        </div>

        <div class="panel panel-pad">
          <div class="between">
            <h2 class="block-title">出票信息</h2>
            <span class="badge" :class="{ 'badge-gold': pickupReady }">
              {{ pickupReady ? '已出票' : (paid ? '出票中' : '待支付') }}
            </span>
          </div>
          <div class="divider"></div>
          <div v-if="pickupReady" class="delivery">
            <div class="pickup">
              <span class="muted small">取票码</span>
              <strong>{{ delivery.pickupCode }}</strong>
            </div>
            <div class="delivery-grid">
              <div><span class="muted small">取票方式</span><p>{{ delivery.deliveryType }}</p></div>
              <div><span class="muted small">联系人</span><p>{{ delivery.contact }} · {{ delivery.mobile }}</p></div>
              <div class="wide"><span class="muted small">地址</span><p>{{ delivery.province }}{{ delivery.city }}{{ delivery.town }}{{ delivery.street }}</p></div>
            </div>
          </div>
          <p v-else class="muted small">
            {{ paid ? '支付已完成，出票信息生成中，请稍后刷新。' : '支付成功后才会生成电子票和取票码。' }}
          </p>
        </div>
      </div>

      <aside class="summary panel">
        <div class="status-row">
          <span class="status" :class="statusInfo.tone">{{ statusInfo.text }}</span>
          <span class="muted small">共 {{ tickets.length }} 项</span>
        </div>

        <div class="summary-rows">
          <div><span class="muted">订单金额</span><span class="price">{{ formatPrice(order.totalFee) }}</span></div>
          <div><span class="muted">下单时间</span><span>{{ formatDate(order.createTime) }}</span></div>
          <div v-if="order.payTime"><span class="muted">支付时间</span><span>{{ formatDate(order.payTime) }}</span></div>
          <div v-if="order.issueTime"><span class="muted">出票时间</span><span>{{ formatDate(order.issueTime) }}</span></div>
          <div v-if="order.closeTime"><span class="muted">关闭时间</span><span>{{ formatDate(order.closeTime) }}</span></div>
        </div>

        <div class="divider"></div>

        <div v-if="canPay" class="pay-box">
          <p class="pay-title">余额支付</p>
          <p class="muted small">请输入登录密码完成支付，默认 123456</p>
          <input v-model="password" class="input" type="password" placeholder="支付密码" />
          <button class="btn btn-primary btn-block" :disabled="paying" @click="pay">
            <span v-if="paying" class="spinner"></span>
            {{ paying ? '支付中…' : `支付 ${formatPrice(order.totalFee)}` }}
          </button>
          <button class="btn btn-danger btn-block" :disabled="canceling" @click="cancel">
            {{ canceling ? '取消中…' : '取消订单' }}
          </button>
        </div>

        <div v-else-if="syncing || (justPaid && !paid)" class="notice">
          <span class="spinner"></span>
          <div>
            <strong>支付成功，正在出票</strong>
            <p class="muted small">支付结果正在同步，请稍候。</p>
          </div>
        </div>

        <div v-else-if="Number(order.status) === 5" class="notice closed">
          <span class="empty-mark">票</span>
          <div>
            <strong>订单已关闭</strong>
            <p class="muted small">超时未支付，余票已释放，可重新选票下单。</p>
          </div>
        </div>

        <div v-else class="notice">
          <span class="empty-mark">票</span>
          <div>
            <strong>购票成功</strong>
            <p class="muted small">凭取票码到场馆自助机取票，或使用电子票入场。</p>
          </div>
        </div>

        <RouterLink to="/" class="btn btn-ghost btn-block">继续看演出</RouterLink>
      </aside>
    </div>
  </section>
</template>

<style scoped>
.detail-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 360px;
  gap: 26px;
  align-items: start;
}

.block-title {
  font-size: 18px;
  letter-spacing: -0.02em;
}

.ticket-list {
  display: grid;
  gap: 14px;
}

.ticket {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid var(--line);
  background: rgba(255, 255, 255, 0.025);
}

.ticket-main {
  flex: 1;
  min-width: 0;
}

.ticket-main h3 {
  font-size: 14px;
  line-height: 1.45;
  margin-bottom: 6px;
}

.ticket-side {
  display: grid;
  justify-items: end;
  gap: 5px;
  flex: none;
}

.delivery {
  display: grid;
  gap: 18px;
}

.pickup {
  display: grid;
  gap: 6px;
  padding: 16px;
  border-radius: 14px;
  border: 1px dashed rgba(243, 201, 105, 0.4);
  background: rgba(243, 201, 105, 0.06);
}

.pickup strong {
  font-size: 26px;
  letter-spacing: 0.14em;
  color: var(--gold);
  font-variant-numeric: tabular-nums;
}

.delivery-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.delivery-grid .wide {
  grid-column: 1 / -1;
}

.delivery-grid p {
  margin-top: 6px;
  font-size: 13px;
  line-height: 1.6;
}

.summary {
  padding: 22px;
  position: sticky;
  top: 96px;
  display: grid;
  gap: 16px;
}

.status-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.summary-rows {
  display: grid;
  gap: 12px;
  font-size: 13px;
}

.summary-rows div {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.pay-box {
  display: grid;
  gap: 10px;
}

.pay-title {
  font-size: 15px;
  font-weight: 700;
}

.notice {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px;
  border-radius: 14px;
  border: 1px solid var(--line);
  background: rgba(255, 255, 255, 0.03);
}

.notice strong {
  font-size: 13px;
}

.notice p {
  margin-top: 5px;
  line-height: 1.6;
}

.notice.closed {
  border-color: rgba(229, 72, 77, 0.28);
  background: rgba(229, 72, 77, 0.06);
}

@media (max-width: 900px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }

  .summary {
    position: static;
  }

  .delivery-grid {
    grid-template-columns: 1fr;
  }
}
</style>
