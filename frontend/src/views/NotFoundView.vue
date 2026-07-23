<script setup>
import { Compass } from '@lucide/vue'
import { RouterLink } from 'vue-router'
</script>

<template><section class="not-found-page"><Compass :size="48" :stroke-width="1.2" /><span>404</span><h1>这条路暂时没有线索</h1><p>页面可能已被移动，回到首页重新开始寻找吧。</p><RouterLink to="/">返回首页</RouterLink></section></template>
