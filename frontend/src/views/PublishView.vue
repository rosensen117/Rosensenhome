<script setup>
import { Check, ImagePlus, PackageCheck, PenLine, Search, ShieldCheck, X } from '@lucide/vue'
import { onBeforeUnmount, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { uploadImage } from '../api/uploads'
import { createItem } from '../api/items'
import { categories } from '../data'
import { showToast } from '../state'

const route = useRoute()
const router = useRouter()
const submitted = ref(false)
const imageInput = ref(null)
const selectedImages = ref([])
const uploadProgress = ref('')
const form = reactive({
  type: route.query.type === 'found' ? 'found' : 'lost',
  title: '',
  category: '随身物品',
  date: '',
  location: '',
  description: '',
  hiddenFeature: '',
})

function chooseImages(event) {
  const files = Array.from(event.target.files || [])
  const remaining = 6 - selectedImages.value.length
  for (const file of files.slice(0, remaining)) {
    if (file.size > 5 * 1024 * 1024) {
      showToast(`${file.name} 超过5MB，已跳过`)
      continue
    }
    selectedImages.value.push({ file, preview: URL.createObjectURL(file) })
  }
  event.target.value = ''
}

function removeImage(index) {
  URL.revokeObjectURL(selectedImages.value[index].preview)
  selectedImages.value.splice(index, 1)
}

onBeforeUnmount(() => selectedImages.value.forEach((image) => URL.revokeObjectURL(image.preview)))

async function submit() {
  if (!form.title || !form.date || !form.location || !form.description) {
    showToast('请先补全带星号的必填信息')
    return
  }
  submitted.value = true
  try {
    const uploadedImages = []
    for (let index = 0; index < selectedImages.value.length; index += 1) {
      uploadProgress.value = `正在上传图片 ${index + 1} / ${selectedImages.value.length}`
      uploadedImages.push(await uploadImage(selectedImages.value[index].file))
    }
    uploadProgress.value = '正在发布信息…'
    const created = await createItem({
      type: form.type,
      title: form.title,
      category: form.category,
      eventDate: form.date,
      location: form.location,
      description: form.description,
      hiddenFeature: form.hiddenFeature,
      images: uploadedImages.map((image) => ({ key: image.key, url: image.url })),
    })
    showToast('发布成功，信息已进入寻物大厅')
    router.push(`/items/${created.id}`)
  } catch (exception) {
    showToast(exception.message)
    submitted.value = false
  } finally {
    uploadProgress.value = ''
  }
}
</script>

<template>
  <div class="route-page-shell publish-page">
    <section class="route-page-banner publish-banner">
      <div class="section-wrap route-page-banner-inner"><div><span class="section-kicker">NEW POST</span><h1>发布信息</h1><p>先把关键事实写清楚，详细证明只在认领核验时使用。</p></div><div class="route-page-index">02 / PUBLISH</div></div>
    </section>
    <section class="section-wrap publish-route-layout">
      <div class="publish-steps"><div class="active"><span>1</span><p><b>填写信息</b><small>物品、时间与地点</small></p></div><div><span>2</span><p><b>补充图片</b><small>最多上传 6 张</small></p></div><div><span>3</span><p><b>提交审核</b><small>审核后公开展示</small></p></div></div>

      <form class="publish-route-form" @submit.prevent="submit">
        <section class="form-section-card">
          <div class="form-section-title"><span>01</span><div><h2>你要发布哪类信息？</h2><p>系统会根据类型展示不同的认领规则。</p></div></div>
          <div class="publish-type route-publish-type">
            <button type="button" :class="{ active: form.type === 'lost' }" @click="form.type = 'lost'"><Search :size="21" /><span><b>我丢了东西</b><small>希望其他同学帮忙寻找</small></span><Check v-if="form.type === 'lost'" :size="17" /></button>
            <button type="button" :class="{ active: form.type === 'found' }" @click="form.type = 'found'"><PackageCheck :size="21" /><span><b>我捡到东西</b><small>等待失主提交认领</small></span><Check v-if="form.type === 'found'" :size="17" /></button>
          </div>
        </section>

        <section class="form-section-card">
          <div class="form-section-title"><span>02</span><div><h2>基本信息</h2><p>公开页面会展示这部分内容，请不要填写完整证件号码。</p></div></div>
          <div class="route-form-grid">
            <label class="wide">物品标题 <em>*</em><input v-model="form.title" maxlength="30" placeholder="例如：蓝色校园卡套" /></label>
            <label>物品分类 <em>*</em><select v-model="form.category"><option v-for="category in categories.slice(1)" :key="category">{{ category }}</option></select></label>
            <label>{{ form.type === 'lost' ? '遗失日期' : '拾取日期' }} <em>*</em><input v-model="form.date" type="date" /></label>
            <label class="wide">{{ form.type === 'lost' ? '可能遗失地点' : '拾取地点' }} <em>*</em><input v-model="form.location" placeholder="例如：图书馆三层东侧自习区" /></label>
            <label class="wide">公开描述 <em>*</em><textarea v-model="form.description" rows="5" maxlength="300" placeholder="描述颜色、外观、时间范围等可公开特征"></textarea><small>{{ form.description.length }} / 300</small></label>
          </div>
        </section>

        <section class="form-section-card">
          <div class="form-section-title"><span>03</span><div><h2>图片与隐藏特征</h2><p>隐藏特征用于判断认领人是否真正了解物品。</p></div></div>
          <input ref="imageInput" class="visually-hidden" type="file" accept="image/jpeg,image/png,image/webp" multiple @change="chooseImages" />
          <button v-if="selectedImages.length < 6" type="button" class="upload-placeholder" @click="imageInput.click()"><ImagePlus :size="27" /><b>点击添加物品图片</b><span>支持 JPG、PNG、WebP，单张不超过 5 MB，最多 6 张</span></button>
          <div v-if="selectedImages.length" class="publish-image-grid">
            <figure v-for="(image, index) in selectedImages" :key="image.preview">
              <img :src="image.preview" :alt="`待上传物品图片 ${index + 1}`" />
              <button type="button" aria-label="移除图片" @click="removeImage(index)"><X :size="15" /></button>
              <figcaption>{{ index + 1 }} / {{ selectedImages.length }}</figcaption>
            </figure>
          </div>
          <label class="hidden-feature-label">隐藏核验特征<textarea v-model="form.hiddenFeature" rows="3" placeholder="例如：卡套内部贴纸、钥匙数字牌、设备序列号末四位。此内容不会公开显示。"></textarea></label>
          <div class="privacy-note route-privacy-note"><ShieldCheck :size="19" /><p><b>隐私保护</b><br />联系方式默认隐藏；认领证明、隐藏特征只对相关双方和授权管理员可见。</p></div>
        </section>

        <div class="publish-route-actions"><button type="button" class="draft-button" @click="showToast('草稿已暂存在当前设备')">保存草稿</button><button class="submit-publish" :disabled="submitted"><Check v-if="submitted" :size="18" /><PenLine v-else :size="18" />{{ uploadProgress || (submitted ? '正在保存…' : '保存并预览') }}</button></div>
      </form>
    </section>
  </div>
</template>
