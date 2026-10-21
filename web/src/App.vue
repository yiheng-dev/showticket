<script setup>
import { RouterLink, RouterView } from 'vue-router'
import NavBar from './components/NavBar.vue'
import ToastHost from './components/ToastHost.vue'
</script>

<template>
  <NavBar />
  <main>
    <RouterView v-slot="{ Component }">
      <Transition name="fade" mode="out-in">
        <component :is="Component" />
      </Transition>
    </RouterView>
  </main>
  <footer class="footer">
    <div class="container footer-inner">
      <div class="footer-brand">
        <span class="footer-mark">ST</span>
        <div>
          <strong>SHOWTICKET</strong>
          <p>发现下一场心动现场</p>
        </div>
      </div>
      <div class="footer-links">
        <RouterLink to="/admin">索引管理</RouterLink>
        <a href="http://localhost:8081/doc.html" target="_blank" rel="noreferrer">演出服务文档</a>
        <a href="http://localhost:8084/doc.html" target="_blank" rel="noreferrer">用户服务文档</a>
      </div>
      <p class="footer-tech">Vue 3 · Spring Cloud · Nacos · Seata · RabbitMQ · Elasticsearch</p>
    </div>
  </footer>
  <ToastHost />
</template>

<style scoped>
main {
  min-height: calc(100vh - 220px);
}

.footer {
  border-top: 1px solid var(--line);
  padding: 34px 0 40px;
  background: rgba(6, 6, 10, 0.6);
}

.footer-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 22px;
  flex-wrap: wrap;
}

.footer-brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.footer-mark {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: grid;
  place-items: center;
  font-size: 12px;
  font-weight: 900;
  color: var(--gold);
  border: 1px solid rgba(243, 201, 105, 0.35);
}

.footer-brand strong {
  font-size: 13px;
  letter-spacing: 0.1em;
}

.footer-brand p {
  margin-top: 4px;
  font-size: 11px;
  color: var(--muted);
}

.footer-links {
  display: flex;
  align-items: center;
  gap: 18px;
  font-size: 12px;
  color: var(--muted);
}

.footer-links a:hover {
  color: var(--gold);
}

.footer-tech {
  width: 100%;
  font-size: 11px;
  color: rgba(151, 148, 166, 0.7);
  letter-spacing: 0.06em;
}
</style>
