<script setup>
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { auth, logout } from '../store/auth'
import { toast } from '../store/toast'
import { formatPrice } from '../utils/format'

const route = useRoute()
const router = useRouter()

const links = [
  { to: '/', label: '演出' },
  { to: '/orders', label: '我的订单' },
  { to: '/selections', label: '选票清单' },
  { to: '/admin', label: '索引管理' }
]

function isActive(path) {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

function handleLogout() {
  logout()
  toast('已退出登录', 'success')
  router.push('/')
}
</script>

<template>
  <header class="nav">
    <div class="container nav-inner">
      <RouterLink to="/" class="brand">
        <span class="brand-mark">ST</span>
        <span class="brand-text">
          <strong>SHOWTICKET</strong>
          <small>演出票务平台</small>
        </span>
      </RouterLink>

      <nav class="links">
        <RouterLink
          v-for="link in links"
          :key="link.to"
          :to="link.to"
          class="nav-link"
          :class="{ active: isActive(link.to) }"
        >
          {{ link.label }}
        </RouterLink>
      </nav>

      <div class="account">
        <template v-if="auth.token && auth.user">
          <div class="user">
            <span class="avatar">{{ (auth.user.username || 'U').slice(0, 1).toUpperCase() }}</span>
            <div class="user-meta">
              <strong>{{ auth.user.username }}</strong>
              <small>余额 {{ formatPrice(auth.user.balance) }}</small>
            </div>
          </div>
          <button class="btn btn-ghost btn-sm" @click="handleLogout">退出</button>
        </template>
        <RouterLink v-else to="/login" class="btn btn-primary btn-sm">登录 / 注册</RouterLink>
      </div>
    </div>
  </header>
</template>

<style scoped>
.nav {
  position: sticky;
  top: 0;
  z-index: 50;
  border-bottom: 1px solid var(--line);
  background: rgba(8, 8, 13, 0.78);
  backdrop-filter: blur(18px);
}

.nav-inner {
  min-height: 72px;
  display: flex;
  align-items: center;
  gap: 28px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 11px;
  flex: none;
}

.brand-mark {
  width: 38px;
  height: 38px;
  border-radius: 11px;
  display: grid;
  place-items: center;
  font-size: 13px;
  font-weight: 900;
  letter-spacing: 0.04em;
  color: #201708;
  background: linear-gradient(135deg, #ffe08a, var(--gold) 55%, var(--gold-2));
  box-shadow: 0 8px 22px rgba(243, 201, 105, 0.24);
}

.brand-text {
  display: grid;
  line-height: 1.1;
}

.brand-text strong {
  font-size: 15px;
  letter-spacing: 0.08em;
}

.brand-text small {
  margin-top: 3px;
  font-size: 10px;
  color: var(--muted);
  letter-spacing: 0.18em;
}

.links {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
}

.nav-link {
  padding: 8px 14px;
  border-radius: 9px;
  font-size: 13px;
  font-weight: 600;
  color: var(--muted);
  transition: all 0.18s ease;
  white-space: nowrap;
}

.nav-link:hover {
  color: var(--text);
  background: rgba(255, 255, 255, 0.04);
}

.nav-link.active {
  color: var(--gold);
  background: rgba(243, 201, 105, 0.09);
}

.account {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: none;
}

.user {
  display: flex;
  align-items: center;
  gap: 9px;
}

.avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  font-size: 13px;
  font-weight: 800;
  color: var(--gold);
  border: 1px solid rgba(243, 201, 105, 0.35);
  background: rgba(243, 201, 105, 0.08);
}

.user-meta {
  display: grid;
  line-height: 1.15;
}

.user-meta strong {
  font-size: 12px;
}

.user-meta small {
  margin-top: 3px;
  font-size: 10px;
  color: var(--muted);
}

@media (max-width: 900px) {
  .nav-inner {
    gap: 14px;
    flex-wrap: wrap;
    padding: 10px 0;
  }

  .links {
    order: 3;
    width: 100%;
    overflow-x: auto;
  }

  .account {
    margin-left: auto;
  }

  .user-meta {
    display: none;
  }
}

@media (max-width: 560px) {
  .brand-text small {
    display: none;
  }

  .nav-link {
    padding: 7px 10px;
  }
}
</style>
