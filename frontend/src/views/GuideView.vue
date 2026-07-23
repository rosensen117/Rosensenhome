<script setup>
import { AlertTriangle, ArrowRight, CheckCircle2, LockKeyhole, MessageCircle, PackageCheck, Search, ShieldCheck } from '@lucide/vue'
import { RouterLink } from 'vue-router'

const steps = [
  { number: '01', icon: Search, title: '找到相似信息', text: '按类别、时间和地点筛选。系统也会给出可能匹配的失物或招领信息。' },
  { number: '02', icon: ShieldCheck, title: '提交核验信息', text: '回答物品的隐藏特征，必要时上传能够证明归属的材料。' },
  { number: '03', icon: MessageCircle, title: '与发布者沟通', text: '认领通过后，通过站内消息约定校内公共交接地点。' },
  { number: '04', icon: PackageCheck, title: '确认交接完成', text: '双方确认物品已交接，信息自动关闭并停止后续匹配提醒。' },
]
</script>

<template>
  <div class="route-page-shell guide-page">
    <section class="route-page-banner guide-banner"><div class="section-wrap route-page-banner-inner"><div><span class="section-kicker">CLAIM GUIDE</span><h1>认领指南</h1><p>先核验、再交接。保护物品，也保护每一位参与者。</p></div><div class="route-page-index">03 / GUIDE</div></div></section>
    <section class="section-wrap guide-intro"><div><span class="section-kicker">HOW IT WORKS</span><h2>四步完成安全认领</h2></div><p>公开页面不会展示所有识别信息。只有真正了解物品细节的人，才更容易通过认领核验。</p></section>
    <section class="section-wrap guide-route-grid"><article v-for="step in steps" :key="step.number"><span>{{ step.number }}</span><component :is="step.icon" :size="28" /><h2>{{ step.title }}</h2><p>{{ step.text }}</p></article></section>
    <section class="guide-safety-band"><div class="section-wrap safety-layout"><div><span class="section-kicker">SAFETY FIRST</span><h2>交接时，请记住这些</h2></div><ul><li><CheckCircle2 :size="17" />优先选择服务台、门卫处等校内公共区域</li><li><CheckCircle2 :size="17" />证件类物品不要公开完整姓名和号码</li><li><CheckCircle2 :size="17" />贵重物品建议邀请同学陪同并保留交接记录</li><li><AlertTriangle :size="17" />任何要求转账、支付保证金的行为都应立即停止</li></ul></div></section>
    <section class="section-wrap privacy-guide"><div class="privacy-guide-icon"><LockKeyhole :size="31" /></div><div><span class="section-kicker">PRIVACY</span><h2>哪些信息不会公开？</h2><p>隐藏核验特征、完整联系方式、认领证明和交接备注，只对相关双方及有权限的管理员可见。</p></div><RouterLink to="/publish">发布一条信息 <ArrowRight :size="17" /></RouterLink></section>
  </div>
</template>
