<template>
  <div class="routes-page">
    <!-- 顶部横幅区域 -->
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">探索清远之美</h1>
        <p class="hero-subtitle">发现令人惊叹的自然风光和人文景观</p>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <div class="section-header">
        <h2 class="section-title">
          <i class="el-icon-location"></i>
          精选目的地
        </h2>
        <div class="filter-box">
          <el-input
            v-model="data.name"
            placeholder="搜索目的地..."
            prefix-icon="el-icon-search"
            clearable
            @input="handleSearch"
          />
        </div>
      </div>

      <!-- 景点卡片网格 -->
      <div class="routes-grid">
        <div 
          v-for="item in data.routesList" 
          :key="item.id" 
          class="route-card"
          @click="goToDetail(item.id)"
        >
          <div class="card-image">
            <img :src="item.img" :alt="item.name">
            <div class="card-overlay">
              <el-button class="view-btn" type="primary" round>
                查看详情
              </el-button>
            </div>
          </div>
          <div class="card-content">
            <h3 class="card-title">{{ item.name }}</h3>
            <div class="card-info">
              <span class="location">
                <el-icon><Location /></el-icon>
                清远
              </span>
              <span class="rating">
                <el-rate
                  v-model="item.rating"
                  disabled
                  text-color="#ff9900"
                  score-template="{value}"
                />
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页器 -->
      <div class="pagination-container" v-if="data.total">
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
import { reactive, ref } from "vue";
import request from "@/utils/request.js";
import { Location, Search } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const searchTimeout = ref(null)

const data = reactive({
  routesList: [],
  pageNum: 1,
  pageSize: 9,
  total: 0,
  name: ''
})

const handleSearch = () => {
  if (searchTimeout.value) clearTimeout(searchTimeout.value)  //使用 setTimeout 实现防抖，防止频繁触发搜索请求。
  searchTimeout.value = setTimeout(() => {
    data.pageNum = 1                                          //搜索时重置页码为第一页
    load()
  }, 300)
}
const goToDetail = (id) => {
  router.push(`/front/routesDetail?id=${id}`)                 // 跳转详情页
}
const load = () => {
  request.get('/routes/selectPage', {
    params: {
      pageNum: data.pageNum,                                   //搜索时重置页码为第一页
      pageSize: data.pageSize,                                 //每页显示数量定位9
      name: data.name                                          //搜索关键词
    }
  }).then(res => {
    if (res.code === '200') {                                  //后端返回200表示业务成功
      data.routesList = res.data?.list.map(item => ({
        ...item,
        rating: (Math.random() * 2 + 3).toFixed(1) // 模拟评分 3-5分
      })) || []
      data.total = res.data?.total                             //安全地访问嵌套对象的属性，避免因中间属性不存在导致的运行时错误
    }
  })
}

load()
</script>

<style scoped>
.routes-page {
  min-height: 100vh;
  background: #f5f7fa;
}

/* 顶部横幅样式 */
.hero-section {
  height: 400px;
  background-image: linear-gradient(rgba(0, 0, 0, 0.3), rgba(0, 0, 0, 0.3)),
    url("@/assets/imgs/lxbg.jpg");
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: white;
  margin-bottom: 40px;
}

.hero-content {
  max-width: 800px;
  padding: 0 20px;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 16px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.hero-subtitle {
  font-size: 20px;
  opacity: 0.9;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

/* 主要内容区域 */
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.section-title {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-box {
  width: 250px;
}

/* 景点卡片网格 */
.routes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

.route-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  cursor: pointer;
}

.route-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.card-image {
  position: relative;
  height: 300px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.route-card:hover .card-image img {
  transform: scale(1.05);
}

.card-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.route-card:hover .card-overlay {
  opacity: 1;
}

.view-btn {
  transform: translateY(20px);
  transition: transform 0.3s ease;
}

.route-card:hover .view-btn {
  transform: translateY(0);
}

.card-content {
  padding: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #666;
}

.location {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 分页器样式 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin: 40px 0;
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

  .section-header {
    flex-direction: column;
    gap: 20px;
  }

  .filter-box {
    width: 100%;
  }

  .routes-grid {
    gap: 20px;
  }

  .card-image {
    height: 250px;
  }
}
</style>