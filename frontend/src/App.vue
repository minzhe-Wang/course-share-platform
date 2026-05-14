<template>
  <div class="app-shell">
    <aside class="sidebar">
      <div class="brand">
        <span class="brand-mark">CS</span>
        <div>
          <strong>课程资料共享平台</strong>
          <small>资料共享 · 互助问答 · AI 审核</small>
        </div>
      </div>

      <nav class="nav-list" aria-label="主导航">
        <button
          v-for="item in navItems"
          :key="item.key"
          :class="{ active: activeView === item.key }"
          type="button"
          @click="activeView = item.key"
        >
          <span>{{ item.icon }}</span>
          {{ item.label }}
        </button>
      </nav>

      <section class="session-panel">
        <template v-if="currentUser">
          <span class="eyebrow">当前账号</span>
          <strong>{{ currentUser.nickname || currentUser.username }}</strong>
          <small>{{ currentUser.role }} · {{ currentUser.token }}</small>
          <button class="secondary" type="button" @click="logout">退出登录</button>
        </template>
        <template v-else>
          <span class="eyebrow">未登录</span>
          <p>登录后可上传资料、收藏、提问和进入个人中心。</p>
        </template>
      </section>
    </aside>

    <main class="content">
      <header class="topbar">
        <div>
          <span class="eyebrow">Course Share Platform</span>
          <h1>{{ currentTitle }}</h1>
        </div>
        <div class="health" :class="healthStatus.toLowerCase()">
          <span></span>
          {{ healthStatusText }}
        </div>
      </header>

      <p v-if="notice" class="notice">{{ notice }}</p>
      <p v-if="error" class="error">{{ error }}</p>
      <p v-if="loading" class="loading">正在加载...</p>

      <section v-if="activeView === 'materials'" class="panel">
        <div class="panel-head">
          <div>
            <h2>资料检索</h2>
            <p>只展示已审核通过、正常上架的课程资料。</p>
          </div>
          <button type="button" @click="loadMaterials">刷新</button>
        </div>

        <div class="toolbar">
          <input v-model="materialQuery.keyword" placeholder="搜索标题或简介" @keyup.enter="loadMaterials" />
          <select v-model="materialQuery.categoryId">
            <option value="">全部分类</option>
            <option v-for="category in categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </option>
          </select>
          <select v-model="materialQuery.tagId">
            <option value="">全部标签</option>
            <option v-for="tag in tags" :key="tag.id" :value="tag.id">{{ tag.name }}</option>
          </select>
          <select v-model="materialQuery.sortBy">
            <option value="latest">最新</option>
            <option value="like">点赞</option>
            <option value="favorite">收藏</option>
            <option value="download">下载</option>
          </select>
          <button type="button" @click="loadMaterials">查询</button>
        </div>

        <div class="data-grid">
          <article v-for="item in materials" :key="item.id" class="data-card">
            <div>
              <h3>{{ item.title }}</h3>
              <p>{{ item.description || '暂无简介' }}</p>
            </div>
            <div class="meta-row">
              <span>{{ item.categoryName }}</span>
              <span>{{ item.fileType }}</span>
              <span>{{ formatSize(item.fileSize) }}</span>
            </div>
            <div class="metric-row">
              <span>浏览 {{ item.viewCount || 0 }}</span>
              <span>下载 {{ item.downloadCount || 0 }}</span>
              <span>收藏 {{ item.favoriteCount || 0 }}</span>
            </div>
            <button class="secondary" type="button" @click="openMaterial(item.id)">查看详情</button>
          </article>
        </div>
        <p v-if="!materials.length" class="empty-state">暂无资料。可以先登录学生账号并发布一份资料。</p>
      </section>

      <section v-if="activeView === 'detail'" class="panel">
        <div class="panel-head">
          <div>
            <h2>{{ materialDetail?.title || '资料详情' }}</h2>
            <p>{{ materialDetail?.description }}</p>
          </div>
          <button class="secondary" type="button" @click="activeView = 'materials'">返回列表</button>
        </div>

        <div v-if="materialDetail" class="detail-layout">
          <dl>
            <div><dt>分类</dt><dd>{{ materialDetail.categoryName }}</dd></div>
            <div><dt>上传者</dt><dd>{{ materialDetail.uploaderName }}</dd></div>
            <div><dt>文件地址</dt><dd>{{ materialDetail.fileUrl }}</dd></div>
            <div><dt>类型</dt><dd>{{ materialDetail.fileType }}</dd></div>
            <div><dt>大小</dt><dd>{{ formatSize(materialDetail.fileSize) }}</dd></div>
          </dl>
          <div class="tag-list">
            <span v-for="tag in materialDetail.tags || []" :key="tag.id">{{ tag.name }}</span>
          </div>
          <div class="action-row">
            <button type="button" @click="downloadMaterial(materialDetail.id)">下载</button>
            <button class="secondary" type="button" @click="likeMaterial(materialDetail.id)">点赞</button>
            <button class="secondary" type="button" @click="favoriteMaterial(materialDetail.id)">收藏</button>
          </div>
        </div>
      </section>

      <section v-if="activeView === 'upload'" class="panel">
        <div class="panel-head">
          <div>
            <h2>发布资料</h2>
            <p>上传文件后提交资料信息，系统会调用 Mock AI 审核。</p>
          </div>
        </div>

        <form class="form-grid" @submit.prevent="submitMaterial">
          <label>
            标题
            <input v-model="materialForm.title" required placeholder="例如：数据结构期末复习资料" />
          </label>
          <label>
            分类
            <select v-model="materialForm.categoryId" required>
              <option value="">请选择分类</option>
              <option v-for="category in categories" :key="category.id" :value="category.id">
                {{ category.name }}
              </option>
            </select>
          </label>
          <label class="wide">
            简介
            <textarea v-model="materialForm.description" rows="4" placeholder="资料内容、适用课程和使用建议"></textarea>
          </label>
          <label>
            标签
            <select v-model="materialForm.tagIds" multiple>
              <option v-for="tag in tags" :key="tag.id" :value="tag.id">{{ tag.name }}</option>
            </select>
          </label>
          <label>
            文件
            <input type="file" accept=".pdf,.doc,.docx,.zip" @change="uploadFile" />
          </label>
          <div v-if="uploadedFile" class="upload-result">
            <strong>{{ uploadedFile.originalFilename }}</strong>
            <span>{{ uploadedFile.fileType }} · {{ formatSize(uploadedFile.fileSize) }}</span>
          </div>
          <button type="submit">提交发布</button>
        </form>
      </section>

      <section v-if="activeView === 'questions'" class="panel">
        <div class="panel-head">
          <div>
            <h2>互助问答</h2>
            <p>浏览同学提出的问题，按分类或关键词检索。</p>
          </div>
          <button type="button" @click="loadQuestions">刷新</button>
        </div>

        <div class="toolbar">
          <input v-model="questionQuery.keyword" placeholder="搜索问题" @keyup.enter="loadQuestions" />
          <select v-model="questionQuery.categoryId">
            <option value="">全部分类</option>
            <option v-for="category in categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </option>
          </select>
          <button type="button" @click="loadQuestions">查询</button>
        </div>

        <div class="data-list">
          <article v-for="question in questions" :key="question.id" class="row-card">
            <div>
              <h3>{{ question.title }}</h3>
              <p>{{ question.content }}</p>
            </div>
            <div class="metric-row">
              <span>{{ question.categoryName }}</span>
              <span>回答 {{ question.answerCount || 0 }}</span>
              <span>点赞 {{ question.likeCount || 0 }}</span>
            </div>
            <button class="secondary" type="button" @click="openQuestion(question.id)">查看讨论</button>
          </article>
        </div>
        <p v-if="!questions.length" class="empty-state">暂无问题。可以发布一个课程问题等待同学回答。</p>
      </section>

      <section v-if="activeView === 'questionDetail'" class="panel">
        <div class="panel-head">
          <div>
            <h2>{{ questionDetail?.title || '问题详情' }}</h2>
            <p>{{ questionDetail?.content }}</p>
          </div>
          <button class="secondary" type="button" @click="activeView = 'questions'">返回问答区</button>
        </div>

        <div v-if="questionDetail" class="detail-layout">
          <div class="metric-row">
            <span>{{ questionDetail.categoryName }}</span>
            <span>{{ questionDetail.userName }}</span>
            <span>浏览 {{ questionDetail.viewCount || 0 }}</span>
            <span>点赞 {{ questionDetail.likeCount || 0 }}</span>
          </div>

          <form class="answer-form" @submit.prevent="submitAnswer">
            <textarea v-model="answerForm.content" rows="4" placeholder="写下你的回答" required></textarea>
            <button type="submit">提交回答</button>
          </form>

          <div class="data-list">
            <article v-for="answer in questionDetail.answers || []" :key="answer.id" class="row-card">
              <div>
                <h3>{{ answer.userName }}</h3>
                <p>{{ answer.content }}</p>
              </div>
              <div class="metric-row">
                <span>点赞 {{ answer.likeCount || 0 }}</span>
                <span>回复 {{ answer.replyCount || 0 }}</span>
              </div>
              <div v-if="answer.replies?.length" class="reply-list">
                <p v-for="reply in answer.replies" :key="reply.id">
                  <strong>{{ reply.userName }}</strong>
                  <span v-if="reply.replyToUserName"> 回复 {{ reply.replyToUserName }}</span>
                  ：{{ reply.content }}
                </p>
              </div>
            </article>
          </div>
          <p v-if="!questionDetail.answers?.length" class="empty-state">还没有回答，做第一个帮忙的人。</p>
        </div>
      </section>

      <section v-if="activeView === 'ask'" class="panel">
        <div class="panel-head">
          <div>
            <h2>发布问题</h2>
            <p>问题会经过 Mock AI 审核，通过后公开展示。</p>
          </div>
        </div>
        <form class="form-grid" @submit.prevent="submitQuestion">
          <label>
            标题
            <input v-model="questionForm.title" required />
          </label>
          <label>
            分类
            <select v-model="questionForm.categoryId" required>
              <option value="">请选择分类</option>
              <option v-for="category in categories" :key="category.id" :value="category.id">
                {{ category.name }}
              </option>
            </select>
          </label>
          <label class="wide">
            内容
            <textarea v-model="questionForm.content" rows="6" required></textarea>
          </label>
          <button type="submit">提交问题</button>
        </form>
      </section>

      <section v-if="activeView === 'account'" class="panel account-grid">
        <div>
          <h2>登录</h2>
          <form class="stack-form" @submit.prevent="login">
            <input v-model="loginForm.username" required placeholder="用户名" />
            <input v-model="loginForm.password" required type="password" placeholder="密码" />
            <button type="submit">登录</button>
          </form>
        </div>
        <div>
          <h2>注册</h2>
          <form class="stack-form" @submit.prevent="register">
            <input v-model="registerForm.username" required placeholder="用户名" />
            <input v-model="registerForm.password" required type="password" placeholder="密码" />
            <input v-model="registerForm.nickname" placeholder="昵称" />
            <button type="submit">注册</button>
          </form>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { apiRequest, clearSession, getStoredUser, storeSession } from './api'

const navItems = [
  { key: 'materials', label: '资料库', icon: '▦' },
  { key: 'upload', label: '发布资料', icon: '↑' },
  { key: 'questions', label: '问答区', icon: '?' },
  { key: 'ask', label: '发布问题', icon: '+' },
  { key: 'account', label: '账号', icon: '◎' }
]

const activeView = ref('materials')
const currentUser = ref(getStoredUser())
const notice = ref('')
const error = ref('')
const healthStatus = ref('CHECKING')
const loading = ref(false)

const categories = ref([])
const tags = ref([])
const materials = ref([])
const materialDetail = ref(null)
const questions = ref([])
const questionDetail = ref(null)
const uploadedFile = ref(null)

const materialQuery = reactive({ pageNum: 1, pageSize: 12, keyword: '', categoryId: '', tagId: '', sortBy: 'latest' })
const questionQuery = reactive({ pageNum: 1, pageSize: 10, keyword: '', categoryId: '' })
const loginForm = reactive({ username: 'student', password: '123456' })
const registerForm = reactive({ username: '', password: '', nickname: '' })
const materialForm = reactive({ title: '', description: '', categoryId: '', tagIds: [] })
const questionForm = reactive({ title: '', content: '', categoryId: '' })
const answerForm = reactive({ content: '' })

const currentTitle = computed(() => {
  if (activeView.value === 'detail') return '资料详情'
  if (activeView.value === 'questionDetail') return '问题详情'
  return navItems.find(item => item.key === activeView.value)?.label || '课程平台'
})
const healthStatusText = computed(() => healthStatus.value === 'UP' ? '服务正常' : healthStatus.value === 'DOWN' ? '服务异常' : '检查中')

onMounted(async () => {
  await Promise.allSettled([loadHealth(), loadBaseData(), loadMaterials(), loadQuestions()])
})

async function run(action, successMessage = '') {
  error.value = ''
  notice.value = ''
  loading.value = true
  try {
    const result = await action()
    if (successMessage) notice.value = successMessage
    return result
  } catch (err) {
    error.value = err.message || '操作失败'
    return null
  } finally {
    loading.value = false
  }
}

async function loadHealth() {
  await run(async () => {
    const data = await apiRequest('/api/health')
    healthStatus.value = data.status
  }).then(() => {
    if (error.value) healthStatus.value = 'DOWN'
  })
}

async function loadBaseData() {
  await run(async () => {
    const [categoryData, tagData] = await Promise.all([
      apiRequest('/api/categories'),
      apiRequest('/api/tags')
    ])
    categories.value = categoryData || []
    tags.value = tagData || []
  })
}

async function loadMaterials() {
  await run(async () => {
    const query = new URLSearchParams(cleanQuery(materialQuery))
    const data = await apiRequest(`/api/materials?${query}`)
    materials.value = data?.list || []
  })
}

async function openMaterial(id) {
  await run(async () => {
    materialDetail.value = await apiRequest(`/api/materials/${id}`)
    activeView.value = 'detail'
  })
}

async function loadQuestions() {
  await run(async () => {
    const query = new URLSearchParams(cleanQuery(questionQuery))
    const data = await apiRequest(`/api/questions?${query}`)
    questions.value = data?.list || []
  })
}

async function openQuestion(id) {
  await run(async () => {
    questionDetail.value = await apiRequest(`/api/questions/${id}`)
    activeView.value = 'questionDetail'
  })
}

async function login() {
  await run(async () => {
    const data = await apiRequest('/api/user/login', {
      method: 'POST',
      body: loginForm
    })
    storeSession(data)
    currentUser.value = data
  }, '登录成功')
}

async function register() {
  await run(async () => {
    await apiRequest('/api/user/register', {
      method: 'POST',
      body: registerForm
    })
  }, '注册成功，请登录')
}

function logout() {
  clearSession()
  currentUser.value = null
  notice.value = '已退出登录'
}

async function uploadFile(event) {
  const file = event.target.files?.[0]
  if (!file) return
  await run(async () => {
    const formData = new FormData()
    formData.append('file', file)
    uploadedFile.value = await apiRequest('/api/files/upload', {
      method: 'POST',
      body: formData
    })
  }, '文件上传成功')
}

async function submitMaterial() {
  if (!uploadedFile.value) {
    error.value = '请先上传文件'
    return
  }
  await run(async () => {
    await apiRequest('/api/materials', {
      method: 'POST',
      body: {
        ...materialForm,
        categoryId: Number(materialForm.categoryId),
        tagIds: materialForm.tagIds.map(Number),
        originalFilename: uploadedFile.value.originalFilename,
        fileKey: uploadedFile.value.fileKey,
        fileUrl: uploadedFile.value.fileUrl,
        fileType: uploadedFile.value.fileType,
        fileSize: uploadedFile.value.fileSize
      }
    })
    materialForm.title = ''
    materialForm.description = ''
    materialForm.categoryId = ''
    materialForm.tagIds = []
    uploadedFile.value = null
    await loadMaterials()
  }, '资料已提交审核')
}

async function submitQuestion() {
  await run(async () => {
    await apiRequest('/api/questions', {
      method: 'POST',
      body: {
        ...questionForm,
        categoryId: Number(questionForm.categoryId)
      }
    })
    questionForm.title = ''
    questionForm.content = ''
    questionForm.categoryId = ''
    await loadQuestions()
  }, '问题已提交审核')
}

async function submitAnswer() {
  if (!questionDetail.value?.id) return
  await run(async () => {
    await apiRequest(`/api/questions/${questionDetail.value.id}/answers`, {
      method: 'POST',
      body: answerForm
    })
    answerForm.content = ''
    await openQuestion(questionDetail.value.id)
  }, '回答已提交审核')
}

async function downloadMaterial(id) {
  await run(async () => {
    const data = await apiRequest(`/api/materials/${id}/download`, { method: 'POST' })
    if (data?.downloadUrl) window.open(data.downloadUrl, '_blank')
  }, '下载记录已生成')
}

async function likeMaterial(id) {
  await run(async () => {
    await apiRequest(`/api/materials/${id}/like`, { method: 'POST' })
    await openMaterial(id)
  }, '点赞成功')
}

async function favoriteMaterial(id) {
  await run(async () => {
    await apiRequest(`/api/materials/${id}/favorite`, { method: 'POST' })
    await openMaterial(id)
  }, '收藏成功')
}

function cleanQuery(query) {
  return Object.fromEntries(Object.entries(query).filter(([, value]) => value !== '' && value !== null && value !== undefined))
}

function formatSize(size) {
  if (!size && size !== 0) return '-'
  if (size < 1024) return `${size} B`
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`
  return `${(size / 1024 / 1024).toFixed(1)} MB`
}
</script>
