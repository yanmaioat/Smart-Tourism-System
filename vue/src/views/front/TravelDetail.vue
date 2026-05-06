<template>
  <div class="travel-detail">
    <!-- 返回按钮 -->
    <div class="back-button" @click="router.back()">
      <el-icon><ArrowLeft /></el-icon>
      <span>返回列表</span>
    </div>

    <!-- 文章头部 -->
    <div class="article-header">
      <h1 class="article-title">{{ data.travel.title }}</h1>
      
      <!-- 作者信息栏 -->
      <div class="author-bar">
        <div class="author-info">
          <el-avatar :size="50" :src="data.travel.userAvatar" />
          <div class="author-meta">
            <div class="author-name">
              <el-icon><User /></el-icon>
              {{ data.travel.userName }}
            </div>
            <div class="post-meta">
              <span>
                <el-icon><Clock /></el-icon>
                {{ data.travel.time }}
              </span>
              <span>
                <el-icon><View /></el-icon>
                {{ data.travel.readCount }} 阅读
              </span>
            </div>
          </div>
        </div>
        <div class="like-section">
          <div class="like-count">{{ data.travel.praiseCount }}</div>
          <el-button 
            type="primary" 
            class="like-btn" 
            @click="top"
            :icon="Star"
          >
            点赞支持
          </el-button>
        </div>
      </div>

      <!-- 旅行信息卡片 -->
      <div class="travel-info-card">
        <div class="info-item">
          <el-icon color="#42b983"><Location /></el-icon>
          <div class="info-content">
            <div class="info-label">目的地</div>
            <div class="info-value">{{ data.travel.location }}</div>
          </div>
        </div>
        <div class="info-divider"></div>
        <div class="info-item">
          <el-icon color="#42b983"><Watch /></el-icon>
          <div class="info-content">
            <div class="info-label">出发日期</div>
            <div class="info-value">{{ data.travel.startDate }}</div>
          </div>
        </div>
        <div class="info-divider"></div>
        <div class="info-item">
          <el-icon color="#42b983"><Calendar /></el-icon>
          <div class="info-content">
            <div class="info-label">行程天数</div>
            <div class="info-value">{{ data.travel.days }}天</div>
          </div>
        </div>
        <div class="info-divider"></div>
        <div class="info-item">
          <el-icon color="#42b983"><Money /></el-icon>
          <div class="info-content">
            <div class="info-label">总花费</div>
            <div class="info-value">¥{{ data.travel.money }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 文章内容 -->
    <div class="article-content">
      <div class="content-wrapper" v-html="data.travel.content"></div>
    </div>

    <!-- 评论区 -->
    <div class="comment-section">
      <h2 class="section-title">评论交流</h2>
      <Comment :module="'travels'" />
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import router from "@/router/index.js";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";
import Comment from "@/components/Comment.vue";
import { User, Clock, View, Location, Watch, Calendar, Money, Star, ArrowLeft } from '@element-plus/icons-vue'

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user')),
  id: router.currentRoute.value.query.id,
  travel: {}
})

request.put('/travels/updateReadCount/' + data.id).then(res => {
  load()
})

const load = () => {
  request.get('/travels/selectById/' + data.id).then(res => {
    data.travel = res.data
  })
}

const top = () => {
  request.post('/praise/add', { fid: data.id, userId: data.user.id }).then(res => {
    if (res.code === '200') {
      ElMessage.success('点赞成功')
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}
</script>

<style scoped>
.travel-detail {
  max-width: 900px;
  margin: 0 auto;
  padding: 40px 20px;
}

/* 文章头部样式 */
.article-header {
  margin-bottom: 40px;
}

.article-title {
  font-size: 36px;
  font-weight: 700;
  color: #2c3e50;
  text-align: center;
  margin-bottom: 30px;
  line-height: 1.4;
}

/* 作者信息栏 */
.author-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.author-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-size: 16px;
  font-weight: 500;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 6px;
}

.post-meta {
  display: flex;
  gap: 15px;
  color: #666;
  font-size: 14px;
}

.post-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.like-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.like-count {
  font-size: 24px;
  font-weight: 600;
  color: #42b983;
}

.like-btn {
  border-radius: 20px;
  padding: 10px 20px;
}

/* 旅行信息卡片 */
.travel-info-card {
  background: #f8f9fa;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 30px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.info-divider {
  width: 1px;
  height: 40px;
  background: #e0e0e0;
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 13px;
  color: #666;
}

.info-value {
  font-size: 16px;
  font-weight: 500;
  color: #2c3e50;
}

/* 文章内容 */
.article-content {
  margin: 40px auto;
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
  border-left: 4px solid #42b983;
  background: #f8f9fa;
  border-radius: 0 8px 8px 0;
  color: #666;
  font-style: italic;
}

/* 链接样式 */
.content-wrapper :deep(a) {
  color: #42b983;
  text-decoration: none;
  border-bottom: 1px dashed #42b983;
  transition: all 0.3s ease;
}

.content-wrapper :deep(a:hover) {
  color: #33a06f;
  border-bottom-style: solid;
}

/* 代码块样式 */
.content-wrapper :deep(pre) {
  background: #f8f9fa;
  padding: 1em;
  border-radius: 8px;
  overflow-x: auto;
  margin: 1.5em 0;
}

.content-wrapper :deep(code) {
  font-family: 'Fira Code', monospace;
  font-size: 0.9em;
  background: #f1f3f5;
  padding: 0.2em 0.4em;
  border-radius: 4px;
  color: #e83e8c;
}

/* 表格样式 */
.content-wrapper :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 2em 0;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.content-wrapper :deep(th),
.content-wrapper :deep(td) {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.content-wrapper :deep(th) {
  background: #f8f9fa;
  font-weight: 600;
  color: #2c3e50;
}

.content-wrapper :deep(tr:hover) {
  background: #f8f9fa;
}

/* 分割线样式 */
.content-wrapper :deep(hr) {
  border: none;
  height: 1px;
  background: #eee;
  margin: 2em 0;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .article-content {
    padding: 20px;
    margin: 20px -20px;
    border-radius: 0;
  }
  
  .content-wrapper :deep(blockquote) {
    margin: 1.5em 0;
    padding: 0.8em 1em;
  }
  
  .content-wrapper :deep(pre) {
    padding: 0.8em;
    margin: 1em 0;
  }
  
  .content-wrapper :deep(table) {
    display: block;
    overflow-x: auto;
    white-space: nowrap;
  }
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
  .travel-detail {
    padding: 20px;
  }

  .article-title {
    font-size: 28px;
  }

  .travel-info-card {
    flex-direction: column;
    gap: 20px;
    padding: 20px;
  }

  .info-divider {
    width: 100%;
    height: 1px;
  }

  .author-bar {
    flex-direction: column;
    gap: 20px;
  }

  .like-section {
    width: 100%;
    justify-content: center;
  }
}

/* 添加返回按钮样式 */
.back-button {
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 100;
  background: white;
  padding: 12px 20px;
  border-radius: 24px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.back-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.back-button .el-icon {
  font-size: 16px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .back-button {
    top: 10px;
    left: 10px;
    padding: 8px 16px;
  }
}
</style>