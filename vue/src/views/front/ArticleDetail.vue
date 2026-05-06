<template>
  <div class="article-detail">
    <!-- 主要内容区域 -->
    <div class="main-container">
      <!-- 返回按钮 -->
      <div class="back-button" @click="router.back()">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回列表</span>
      </div>

      <!-- 文章头部横幅 -->
      <div class="hero-banner">
        <div class="banner-content">
          <div class="article-meta">
            <div class="meta-left">
              <span class="meta-item">
                <el-icon><Calendar /></el-icon>
                {{ data.article.date }}
              </span>
              <span class="meta-item">
                <el-icon><View /></el-icon>
                {{ data.article.readCount }} 阅读
              </span>
            </div>
            <div class="collect-section">
              <el-button 
                type="warning" 
                class="collect-btn" 
                @click="collect"
                :icon="Star"
              >
                <span class="collect-count">{{ data.article.collectCount }}</span>
                收藏文章
              </el-button>
            </div>
          </div>
          <h1 class="article-title">{{ data.article.title }}</h1>
        </div>
      </div>

      <!-- 文章内容 -->
      <div class="article-content">
        <div class="content-wrapper" v-html="data.article.content"></div>
      </div>

      <!-- 评论区 -->
      <div class="comment-section">
        <h2 class="section-title">评论交流</h2>
        <Comment :module="'article'" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import { useRouter } from "vue-router";
import request from "@/utils/request.js";
import Comment from "@/components/Comment.vue";
import { ElMessage } from "element-plus";
import { Calendar, View, Star, ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter();                 //获取路由实例
const data = reactive({
  id: router.currentRoute.value.query.id,   //从路由查询参数中获取当前文章的 ID
  article: {}                               //存储从后端获取的文章详细信息（如标题、内容、封面等）
})
//处理用户点击“收藏文章”按钮的操作
const collect = () => {
  request.post('/collect/add', { fid: data.id }).then(res => { //POST /collect/add：请求路径和参数（fid 是文章 ID）
    if (res.code === '200') {                                           //后端返回200，为收藏成功
      ElMessage.success('收藏成功')
      load()                                                            //更新文章的状态
    } else {
      ElMessage.error(res.msg)   //失败时显示错误信息
    }
  })
}
const load = () => {
  request.get('/article/selectById/' + data.id).then(res => {  ////根据文章 ID 从后端接口获取文章详细信息，并更新 data.article
    data.article = res.data
  })
}
load()
</script>

<style scoped>
.article-detail {
  min-height: 100vh;
  background: #f5f7fa;
}

.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

/* 返回按钮 */
.back-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  color: #2c3e50;
  font-size: 14px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  margin-bottom: 20px;
}

/* 顶部横幅 */
.hero-banner {
  height: 400px;
  background: linear-gradient(135deg, #1a237e 0%, #0277bd 100%);
  color: white;
  border-radius: 16px;
  margin-bottom: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 0 20px;
}

.banner-content {
  max-width: 900px;
  width: 100%;
}

.article-title {
  font-size: 42px;
  font-weight: 700;
  line-height: 1.4;
  margin-top: 20px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* 文章信息栏 */
.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.meta-left {
  display: flex;
  gap: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.1);
  padding: 8px 16px;
  border-radius: 20px;
  backdrop-filter: blur(10px);
}

.collect-btn {
  border-radius: 20px;
  padding: 12px 24px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
}

.collect-count {
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 14px;
}

/* 文章内容 */
.article-content {
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.content-wrapper :deep(*) {
  max-width: 100%;
}

/* 段落样式 */
.content-wrapper :deep(p) {
  font-size: 16px;
  line-height: 1.8;
  color: #2c3e50;
  margin: 1.5em 0;
  text-align: justify;
}

/* 图片样式 */
.content-wrapper :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 12px;
  margin: 2em auto;
  display: block;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.content-wrapper :deep(img:hover) {
  transform: scale(1.01);
}

/* 标题样式 */
.content-wrapper :deep(h1),
.content-wrapper :deep(h2),
.content-wrapper :deep(h3),
.content-wrapper :deep(h4),
.content-wrapper :deep(h5),
.content-wrapper :deep(h6) {
  color: #2c3e50;
  margin: 1.5em 0 1em;
  font-weight: 600;
  line-height: 1.4;
}

.content-wrapper :deep(h1) { font-size: 2em; }
.content-wrapper :deep(h2) { font-size: 1.75em; }
.content-wrapper :deep(h3) { font-size: 1.5em; }
.content-wrapper :deep(h4) { font-size: 1.25em; }
.content-wrapper :deep(h5) { font-size: 1.1em; }
.content-wrapper :deep(h6) { font-size: 1em; }

/* 列表样式 */
.content-wrapper :deep(ul),
.content-wrapper :deep(ol) {
  padding-left: 2em;
  margin: 1em 0;
}

.content-wrapper :deep(li) {
  margin: 0.5em 0;
  line-height: 1.6;
  color: #2c3e50;
}

/* 引用样式 */
.content-wrapper :deep(blockquote) {
  margin: 2em 0;
  padding: 1em 1.5em;
  border-left: 4px solid #ff9800;
  background: #fff8e1;
  border-radius: 0 8px 8px 0;
  color: #666;
  font-style: italic;
}

/* 评论区 */
.comment-section {
  margin-top: 60px;
  padding-top: 40px;
  border-top: 1px solid #eee;
}

.section-title {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 30px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-container {
    padding: 20px;
  }

  .back-button {
    padding: 8px 16px;
    margin-bottom: 15px;
  }

  .hero-banner {
    height: 300px;
    border-radius: 12px;
  }

  .article-title {
    font-size: 28px;
  }

  .article-meta {
    flex-direction: column;
    align-items: center;
  }

  .article-content {
    padding: 20px;
    border-radius: 16px 16px 0 0;
  }
}
</style>