<script setup>
import { onMounted, ref } from 'vue'
import { getShowPage, reindex } from '../api'
import { toast } from '../store/toast'
import { errorMessage } from '../utils/format'

const total = ref(0)
const loading = ref(true)
const reindexing = ref(false)
const lastCount = ref(null)
const error = ref('')

async function loadTotal() {
  loading.value = true
  error.value = ''
  try {
    const { data } = await getShowPage({ pageNo: 1, pageSize: 1 })
    total.value = Number(data.total) || 0
  } catch (err) {
    error.value = errorMessage(err, '数据统计加载失败')
  } finally {
    loading.value = false
  }
}

async function doReindex() {
  reindexing.value = true
  try {
    const { data } = await reindex()
    lastCount.value = Number(data)
    toast(`ES 索引重建完成，共导入 ${lastCount.value} 条演出`, 'success', 4500)
    loadTotal()
  } catch (err) {
    toast(`重建索引失败：${errorMessage(err)}（请确认 Elasticsearch 已启动并安装 IK 分词器）`, 'error', 6000)
  } finally {
    reindexing.value = false
  }
}

onMounted(loadTotal)
</script>

<template>
  <section class="container section">
    <div class="page-head">
      <div>
        <span class="eyebrow">DATA & SEARCH</span>
        <h1 class="page-title">索引管理</h1>
        <p class="sub">查看票务数据规模，并一键重建 Elasticsearch 搜索索引</p>
      </div>
      <RouterLink to="/" class="btn btn-ghost btn-sm">返回首页</RouterLink>
    </div>

    <div class="admin-grid">
      <div class="panel panel-pad stat-card">
        <span class="muted small">数据库演出总数</span>
        <strong class="stat-num">{{ loading ? '—' : total }}</strong>
        <span class="muted small">ticket_show.show_info</span>
      </div>
      <div class="panel panel-pad stat-card">
        <span class="muted small">搜索索引</span>
        <strong class="stat-num">shows</strong>
        <span class="muted small">Elasticsearch 7.12 · IK 分词</span>
      </div>
      <div class="panel panel-pad stat-card">
        <span class="muted small">网关</span>
        <strong class="stat-num">8080</strong>
        <span class="muted small">Spring Cloud Gateway · Nacos 动态路由</span>
      </div>
    </div>

    <div class="content-grid">
      <div class="panel panel-pad">
        <div class="between">
          <div>
            <h2 class="block-title">Elasticsearch 索引重建</h2>
            <p class="sub">删除旧索引 → 重建 mapping → 从 MySQL 全量导入 → 返回文档数</p>
          </div>
          <button class="btn btn-primary" :disabled="reindexing" @click="doReindex">
            <span v-if="reindexing" class="spinner"></span>
            {{ reindexing ? '重建中…' : '重建索引' }}
          </button>
        </div>
        <div class="divider"></div>
        <div v-if="lastCount !== null" class="result">
          <span class="status success">重建成功</span>
          <span>当前索引文档数：<strong class="price">{{ lastCount }}</strong></span>
        </div>
        <p v-else class="muted small">
          首次启动 show-service 时，如果索引为空会自动导入；已有旧数据时请点击重建，确保 216 条全部进入 ES。
        </p>
        <p v-if="error" class="error">{{ error }}</p>
      </div>

      <div class="panel panel-pad">
        <h2 class="block-title">演示信息</h2>
        <div class="divider"></div>
        <div class="info-list">
          <div><span class="muted">测试账号</span><strong>tester / 123456</strong></div>
          <div><span class="muted">初始余额</span><strong>¥10,000.00</strong></div>
          <div><span class="muted">种子数据</span><strong>216 条演出</strong></div>
          <div><span class="muted">Nacos</span><strong>192.168.17.130:8848</strong></div>
          <div><span class="muted">服务端口</span><strong>8080 / 8081 / 8082 / 8084 / 8085 / 8086</strong></div>
        </div>
      </div>
    </div>

    <div class="panel panel-pad tech">
      <h2 class="block-title">技术栈</h2>
      <div class="divider"></div>
      <div class="tech-tags">
        <span>Vue 3</span>
        <span>Vite</span>
        <span>Vue Router</span>
        <span>Axios</span>
        <span>Spring Cloud Gateway</span>
        <span>Nacos</span>
        <span>OpenFeign</span>
        <span>Seata AT</span>
        <span>RabbitMQ 延迟消息</span>
        <span>Elasticsearch</span>
        <span>MyBatis-Plus</span>
        <span>MySQL</span>
      </div>
    </div>
  </section>
</template>

<style scoped>
.admin-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
}

.stat-card {
  display: grid;
  gap: 10px;
}

.stat-num {
  font-size: clamp(28px, 4vw, 42px);
  letter-spacing: -0.03em;
  color: var(--gold);
  font-variant-numeric: tabular-nums;
}

.content-grid {
  margin-top: 18px;
  display: grid;
  grid-template-columns: 1.25fr 0.75fr;
  gap: 18px;
}

.block-title {
  font-size: 18px;
  letter-spacing: -0.02em;
}

.result {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
}

.info-list {
  display: grid;
  gap: 13px;
  font-size: 13px;
}

.info-list div {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}

.info-list strong {
  text-align: right;
  font-size: 12px;
}

.tech {
  margin-top: 18px;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tech-tags span {
  border: 1px solid var(--line-strong);
  border-radius: 999px;
  padding: 7px 13px;
  font-size: 12px;
  color: var(--muted);
  background: rgba(255, 255, 255, 0.03);
}

.error {
  margin-top: 14px;
  color: #ff9ca0;
  font-size: 12px;
  line-height: 1.6;
}

@media (max-width: 900px) {
  .admin-grid,
  .content-grid {
    grid-template-columns: 1fr;
  }
}
</style>
