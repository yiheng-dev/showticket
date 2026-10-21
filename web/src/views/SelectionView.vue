<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { createOrder, deleteSelection, getAddresses, getSelections, updateSelection } from '../api'
import EmptyState from '../components/EmptyState.vue'
import PosterCover from '../components/PosterCover.vue'
import { toast } from '../store/toast'
import { errorMessage, formatDate, formatPrice } from '../utils/format'

const router = useRouter()
const items = ref([])
const addresses = ref([])
const addressId = ref('')
const loading = ref(true)
const submitting = ref(false)
const error = ref('')

const total = computed(() => items.value.reduce((sum, item) => sum + Number(item.price) * Number(item.num), 0))
const totalCount = computed(() => items.value.reduce((sum, item) => sum + Number(item.num), 0))
const selectedAddress = computed(() => addresses.value.find((item) => String(item.id) === String(addressId.value)))

async function load() {
  loading.value = true
  error.value = ''
  try {
    const [selectionRes, addressRes] = await Promise.all([getSelections(), getAddresses()])
    items.value = selectionRes.data || []
    addresses.value = addressRes.data || []
    if (!addressId.value) {
      const defaultAddress = addresses.value.find((item) => item.isDefault === 1) || addresses.value[0]
      addressId.value = defaultAddress ? String(defaultAddress.id) : ''
    }
  } catch (err) {
    error.value = errorMessage(err, '选票清单加载失败')
  } finally {
    loading.value = false
  }
}

function maxQty(item) {
  const stock = Number(item.stock) || 10
  return Math.max(1, Math.min(10, stock))
}

async function changeNum(item, delta) {
  const next = Number(item.num) + delta
  if (next < 1 || next > maxQty(item)) return
  const previous = item.num
  item.num = next
  try {
    await updateSelection({ id: item.id, num: next })
  } catch (err) {
    item.num = previous
    toast(errorMessage(err, '修改数量失败'), 'error')
  }
}

async function remove(item) {
  if (!window.confirm('确定从选票清单移除这场演出吗？')) return
  try {
    await deleteSelection(item.id)
    items.value = items.value.filter((row) => row.id !== item.id)
    toast('已移除', 'success')
  } catch (err) {
    toast(errorMessage(err, '移除失败'), 'error')
  }
}

async function submit() {
  if (!items.value.length) {
    toast('选票清单为空', 'warn')
    return
  }
  if (!addressId.value) {
    toast('请选择收票地址', 'warn')
    return
  }
  submitting.value = true
  try {
    const { data } = await createOrder({
      deliveryAddressId: addressId.value,
      paymentType: 3,
      tickets: items.value.map((item) => ({ showId: item.showId, num: Number(item.num) }))
    })
    toast('订单创建成功，请尽快支付', 'success')
    router.push(`/orders/${data}`)
  } catch (err) {
    const message = errorMessage(err, '下单失败，请稍后重试')
    toast(`${message}（下单依赖 Seata 与 RabbitMQ）`, 'error', 5000)
  } finally {
    submitting.value = false
  }
}

onMounted(load)
</script>

<template>
  <section class="container section">
    <div class="page-head">
      <div>
        <span class="eyebrow">YOUR SELECTION</span>
        <h1 class="page-title">选票清单</h1>
        <p class="sub">{{ items.length }} 场演出 · {{ totalCount }} 张票</p>
      </div>
      <RouterLink to="/" class="btn btn-ghost btn-sm">继续选票</RouterLink>
    </div>

    <div v-if="loading" class="loading-block">
      <span class="spinner spinner-lg"></span>
      <span>正在整理你的选票…</span>
    </div>

    <EmptyState v-else-if="error" title="无法加载选票清单" :description="error">
      <button class="btn btn-ghost btn-sm" @click="load">重新加载</button>
    </EmptyState>

    <EmptyState
      v-else-if="!items.length"
      title="选票清单还是空的"
      description="去首页挑一场喜欢的演出吧，加入清单后可以一起下单。"
    >
      <RouterLink to="/" class="btn btn-primary btn-sm">去发现演出</RouterLink>
    </EmptyState>

    <div v-else class="selection-grid">
      <div class="list stack">
        <article v-for="item in items" :key="item.id" class="item panel">
          <PosterCover :show="item" size="mini" />
          <div class="item-main">
            <div class="between wrap">
              <div>
                <h3 class="item-name">{{ item.name }}</h3>
                <p class="sub">{{ item.ticketType }} · {{ formatDate(item.showTime) }}</p>
              </div>
              <span class="price">{{ formatPrice(item.price) }}</span>
            </div>
            <div class="item-foot">
              <div class="qty">
                <button class="qty-btn" :disabled="item.num <= 1" @click="changeNum(item, -1)">−</button>
                <span>{{ item.num }}</span>
                <button class="qty-btn" :disabled="item.num >= maxQty(item)" @click="changeNum(item, 1)">＋</button>
              </div>
              <div class="row">
                <span class="muted small">小计 {{ formatPrice(Number(item.price) * Number(item.num)) }}</span>
                <button class="btn btn-danger btn-sm" @click="remove(item)">移除</button>
              </div>
            </div>
          </div>
        </article>
      </div>

      <aside class="summary panel">
        <h2 class="summary-title">确认订单</h2>

        <div class="field">
          <span class="field-label">收票地址</span>
          <select v-model="addressId" class="select">
            <option v-for="address in addresses" :key="address.id" :value="String(address.id)">
              {{ address.contact }} · {{ address.mobile }} · {{ address.city }}{{ address.town }}{{ address.street }}
            </option>
          </select>
          <p v-if="selectedAddress" class="address-note">
            {{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.town }}{{ selectedAddress.street }}
          </p>
        </div>

        <div class="summary-rows">
          <div><span class="muted">票数</span><span>{{ totalCount }} 张</span></div>
          <div><span class="muted">支付方式</span><span>余额支付</span></div>
        </div>

        <div class="divider"></div>

        <div class="between">
          <span class="muted">应付总额</span>
          <span class="price price-lg">{{ formatPrice(total) }}</span>
        </div>

        <button class="btn btn-primary btn-block submit" :disabled="submitting" @click="submit">
          <span v-if="submitting" class="spinner"></span>
          {{ submitting ? '提交中…' : '提交订单' }}
        </button>
        <p class="tip">提交后请在支付超时前完成支付，超时订单将自动关闭并释放余票。</p>
      </aside>
    </div>
  </section>
</template>

<style scoped>
.selection-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 360px;
  gap: 26px;
  align-items: start;
}

.item {
  display: flex;
  gap: 16px;
  padding: 14px;
}

.item-main {
  flex: 1;
  min-width: 0;
  display: grid;
  gap: 14px;
  align-content: space-between;
}

.item-name {
  font-size: 15px;
  line-height: 1.45;
  margin-bottom: 6px;
}

.item-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.qty {
  display: flex;
  align-items: center;
  gap: 10px;
  border: 1px solid var(--line-strong);
  border-radius: 10px;
  padding: 3px;
}

.qty-btn {
  width: 30px;
  height: 30px;
  border: 0;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.06);
  color: var(--text);
  font-size: 16px;
}

.qty span {
  min-width: 24px;
  text-align: center;
  font-size: 14px;
  font-weight: 700;
}

.summary {
  padding: 24px;
  position: sticky;
  top: 96px;
  display: grid;
  gap: 18px;
}

.summary-title {
  font-size: 20px;
  letter-spacing: -0.02em;
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
}

.address-note {
  font-size: 11px;
  color: var(--muted);
  line-height: 1.6;
}

.submit {
  min-height: 48px;
}

.tip {
  font-size: 11px;
  color: var(--muted);
  line-height: 1.6;
  text-align: center;
}

@media (max-width: 900px) {
  .selection-grid {
    grid-template-columns: 1fr;
  }

  .summary {
    position: static;
  }
}
</style>
