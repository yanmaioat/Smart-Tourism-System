<template>
  <div class="article-page">
    <!-- 顶部横幅 -->
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">探索清远旅游攻略</h1>
        <p class="hero-subtitle">发现最佳旅行路线和深度游玩建议</p>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 文章列表 -->
      <div class="article-list">
        <div 
          v-for="(item, index) in data.tableData" 
          :key="item.id" 
          class="article-card"
          :class="{ 'reverse': index % 2 !== 0 }"
        >
          <div class="card-image">
            <img :src="item.cover" :alt="item.title">
          </div>
          <div class="card-content">
            <h2 class="article-title">
              <a :href="'/front/articleDetail?id=' + item.id">{{ item.title }}</a>
            </h2>
            <p class="article-desc">{{ item.descr }}</p>
            <div class="article-meta">
              <div class="meta-left">
                <span class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  {{ item.date }}
                </span>
                <span class="meta-item">
                  <el-icon><View /></el-icon>
                  {{ item.readCount }} 阅读
                </span>
              </div>
              <el-button 
                type="primary" 
                class="read-more" 
                @click="goToDetail(item.id)"
                text
              >
                阅读全文
                <el-icon class="el-icon--right"><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页器 -->
      <div class="pagination-wrapper" v-if="data.total">
        <el-pagination
          v-model:current-page="data.pageNum"
          :page-size="data.pageSize"
          :total="data.total"
          @current-change="load"
          background
          layout="prev, pager, next"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import request from "@/utils/request.js";
import { Calendar, View, ArrowRight } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'



const data = reactive({
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
})
const router = useRouter()                      //Vue Router 的钩子函数，获取路由实例
const goToDetail = (id) => {
  router.push(`/front/articleDetail?id=${id}`)  //点击“阅读全文”按钮或文章标题时跳转到详情页
}

const load = () => {
  request.get('/article/selectPage', {
    params: {                                  //传递分页参数 pageNum 和 pageSize
      pageNum: data.pageNum,                   //当前页码为1
      pageSize: data.pageSize,                 //每页数量为10
    }
  }).then(res => {
    if (res.code === '200') {
      data.tableData = res.data?.list || []   //res.data?.list 和 res.data?.total 避免空值错误
      data.total = res.data?.total
    }
  })
}
load()                                        //  //在组件挂载时立即调用 load() 加载第一页数据
</script>

<style scoped>
.article-page {
  min-height: 100vh;
  background: #f5f7fa;
}

/* 顶部横幅 */
.hero-section {
  height: 400px;
  background-image: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url("@/assets/imgs/lygl.jpg");
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: white;
}

.hero-content {
  max-width: 800px;
  padding: 0 20px;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 20px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.hero-subtitle {
  font-size: 20px;
  opacity: 0.9;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

/* 主要内容区域 */
.main-content {
  max-width: 1200px;
  margin: -60px auto 60px;
  padding: 0 20px;
  position: relative;
}

/* 文章卡片 */
.article-card {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 30px;
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  margin-bottom: 30px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.article-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.article-card.reverse {
  grid-template-columns: 1fr 300px;
}

.article-card.reverse .card-image {
  order: 2;
}

.card-image {
  height: 100%;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.article-card:hover .card-image img {
  transform: scale(1.05);
}

.card-content {
  padding: 30px;
  display: flex;
  flex-direction: column;
}

.article-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 16px;
}

.article-title a {
  color: #2c3e50;
  text-decoration: none;
  transition: color 0.3s ease;
}

.article-title a:hover {
  color: #409eff;
}

.article-desc {
  color: #666;
  line-height: 1.6;
  margin-bottom: 20px;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #666;
}

.meta-left {
  display: flex;
  gap: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.read-more {
  font-weight: 500;
}

/* 分页器 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-section {
    height: 300px;
  }

  .hero-title {
    font-size: 36px;
  }

  .hero-subtitle {
    font-size: 18px;
  }

  .article-card,
  .article-card.reverse {
    grid-template-columns: 1fr;
  }

  .card-image {
    height: 200px;
  }

  .article-card.reverse .card-image {
    order: 0;
  }

  .card-content {
    padding: 20px;
  }

  .article-title {
    font-size: 20px;
  }

  .article-meta {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
}
</style>