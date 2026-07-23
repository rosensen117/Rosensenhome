<script setup>
import { Compass } from '@lucide/vue'
import { RouterLink } from 'vue-router'
</script>

<template>
  <footer>
    <div class="section-wrap footer-inner">
      <RouterLink to="/" class="footer-brand"><span class="brand-mark"><Compass :size="18" /></span><div><b>拾光</b><p>让每件物品都有回来的路</p></div></RouterLink>
      <div class="footer-links"><RouterLink to="/hall">寻物大厅</RouterLink><RouterLink to="/guide">认领指南</RouterLink><a href="#">隐私说明</a><a href="#">联系管理员</a></div>
      <div class="footer-note">毕业设计 UI 原型 · Vue 3</div>
    </div>
  </footer>
</template>
