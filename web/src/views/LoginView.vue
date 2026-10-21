<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { login } from '../api'
import { auth, setLogin } from '../store/auth'
import { toast } from '../store/toast'
import { errorMessage } from '../utils/format'

const route = useRoute()
const router = useRouter()
const form = reactive({ username: '', password: '' })
const loading = ref(false)
const error = ref('')

function fillDemo() {
  form.username = 'tester'
  form.password = '123456'
}

async function submit() {
  error.value = ''
  if (!form.username || !form.password) {
    error.value = '请输入用户名和密码'
    return
  }
  loading.value = true
  try {
    const { data } = await login({ username: form.username, password: form.password })
    setLogin(data)
    toast(`欢迎回来，${data.username}`, 'success')
    const redirect = route.query.redirect
    router.replace(typeof redirect === 'string' && redirect.startsWith('/') ? redirect : '/')
  } catch (err) {
    error.value = errorMessage(err, '登录失败，请检查账号密码')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="login-page">
    <div class="container login-grid">
      <div class="brand-panel">
        <span class="eyebrow">SHOWTICKET</span>
        <h1>让每一次<br /><span>心动现场</span><br />都有座位。</h1>
        <p class="lead">
          演唱会、音乐剧、话剧、音乐会、相声、舞剧……<br />
          一站选票、下单、出票，沉浸式购票体验。
        </p>
        <div class="brand-tags">
          <span>216+ 场演出</span>
          <span>32 个场馆</span>
          <span>10 种类型</span>
        </div>
        <div class="ticket-stub" aria-hidden="true">
          <div class="stub-left">
            <strong>ADMIT ONE</strong>
            <small>SHOWTICKET · LIVE</small>
            <span class="stub-code">ST-2026-0001</span>
          </div>
          <div class="stub-notch"></div>
          <div class="stub-right">ST</div>
        </div>
      </div>

      <div class="form-panel panel">
        <div class="form-head">
          <h2>登录账户</h2>
          <p class="sub">登录后即可选票、下单和查看订单</p>
        </div>
        <form class="form" @submit.prevent="submit">
          <label class="field">
            <span class="field-label">用户名</span>
            <input v-model.trim="form.username" class="input" autocomplete="username" placeholder="请输入用户名" />
          </label>
          <label class="field">
            <span class="field-label">密码</span>
            <input
              v-model="form.password"
              class="input"
              type="password"
              autocomplete="current-password"
              placeholder="请输入密码"
            />
          </label>
          <p v-if="error" class="error">{{ error }}</p>
          <button class="btn btn-primary btn-block" type="submit" :disabled="loading">
            <span v-if="loading" class="spinner"></span>
            {{ loading ? '登录中…' : '登 录' }}
          </button>
        </form>

        <div class="demo">
          <div>
            <p class="demo-title">演示账号</p>
            <p class="demo-sub">tester / 123456 · 余额 ¥10,000.00</p>
          </div>
          <button class="btn btn-ghost btn-sm" type="button" @click="fillDemo">填入</button>
        </div>

        <p v-if="auth.token" class="sub center">
          你已登录，可直接
          <RouterLink class="link" to="/">返回首页</RouterLink>
        </p>
      </div>
    </div>
  </section>
</template>

<style scoped>
.login-page {
  padding: 64px 0 88px;
}

.login-grid {
  display: grid;
  grid-template-columns: 1.15fr 0.85fr;
  gap: 56px;
  align-items: center;
}

.brand-panel h1 {
  margin-top: 18px;
  font-size: clamp(40px, 5.4vw, 68px);
  line-height: 1.06;
  letter-spacing: -0.04em;
  font-weight: 900;
}

.brand-panel h1 span {
  background: linear-gradient(120deg, #ffe08a, var(--gold) 45%, #e6a93c);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.lead {
  margin-top: 22px;
  color: var(--muted);
  line-height: 1.9;
  font-size: 14px;
}

.brand-tags {
  margin-top: 26px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.brand-tags span {
  border: 1px solid var(--line-strong);
  border-radius: 999px;
  padding: 7px 13px;
  font-size: 12px;
  color: var(--muted);
  background: rgba(255, 255, 255, 0.03);
}

.ticket-stub {
  margin-top: 34px;
  display: flex;
  align-items: stretch;
  width: min(420px, 100%);
  border-radius: 16px;
  border: 1px solid rgba(243, 201, 105, 0.28);
  background: linear-gradient(120deg, rgba(243, 201, 105, 0.12), rgba(255, 255, 255, 0.03));
  overflow: hidden;
}

.stub-left {
  flex: 1;
  padding: 18px 20px;
  display: grid;
  gap: 5px;
}

.stub-left strong {
  font-size: 13px;
  letter-spacing: 0.22em;
  color: var(--gold);
}

.stub-left small {
  font-size: 10px;
  color: var(--muted);
  letter-spacing: 0.16em;
}

.stub-code {
  margin-top: 8px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.55);
  letter-spacing: 0.08em;
  font-variant-numeric: tabular-nums;
}

.stub-notch {
  width: 1px;
  background-image: linear-gradient(180deg, transparent 0 8px, rgba(255, 255, 255, 0.2) 8px 14px, transparent 14px 20px);
  background-size: 1px 20px;
}

.stub-right {
  width: 84px;
  display: grid;
  place-items: center;
  font-size: 22px;
  font-weight: 900;
  color: rgba(243, 201, 105, 0.8);
  border-left: 1px dashed rgba(255, 255, 255, 0.16);
}

.form-panel {
  padding: 34px;
}

.form-head h2 {
  font-size: 26px;
  letter-spacing: -0.02em;
}

.form-head p {
  margin-top: 8px;
}

.form {
  margin-top: 26px;
  display: grid;
  gap: 16px;
}

.error {
  color: #ff9ca0;
  font-size: 13px;
  line-height: 1.5;
}

.demo {
  margin-top: 24px;
  padding: 14px 16px;
  border-radius: 12px;
  border: 1px dashed var(--line-strong);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}

.demo-title {
  font-size: 12px;
  font-weight: 700;
  color: var(--gold);
}

.demo-sub {
  margin-top: 4px;
  font-size: 12px;
  color: var(--muted);
}

.center {
  margin-top: 20px;
  text-align: center;
}

@media (max-width: 900px) {
  .login-grid {
    grid-template-columns: 1fr;
    gap: 34px;
  }

  .ticket-stub {
    display: none;
  }
}
</style>
