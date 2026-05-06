<template>
  <div class="tourism-page">
    <!-- 顶部横幅 -->
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">清远旅游产品</h1>
        <p class="hero-subtitle">探索独特的旅游体验</p>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 导航栏 -->
      <div class="nav-section">
        <div class="sort-options">
          <span 
            @click="loadBySort('hot')" 
            :class="['sort-btn', { active: data.sort === 'hot' }]"
          >
            <el-icon><Star /></el-icon>
            最热
          </span>
          <div class="divider"></div>
          <span 
            @click="loadBySort('new')" 
            :class="['sort-btn', { active: data.sort === 'new' }]"
          >
            <el-icon><Timer /></el-icon>
            最新
          </span>
        </div>
      </div>

      <!-- 产品列表 -->
      <div class="products-grid">
        <div 
          v-for="item in data.tourismList" 
          :key="item.id" 
          class="product-card"
          @click="goPage('/front/tourismDetail?id=' + item.id)"
        >
          <div class="card-image">
            <img :src="item.img" :alt="item.name">
            <div class="card-overlay">
              <el-button type="primary" round>
                查看详情
              </el-button>
            </div>
          </div>
          <div class="card-content">
            <h3 class="product-name">{{ item.name }}</h3>
            <div class="product-info">
              <div class="price">
                <span class="currency">¥</span>
                <span class="amount">{{ item.price }}</span>
              </div>
              <div class="sales">
                已售 {{ item.orderNum }}
              </div>
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
import router from "@/router/index.js";
import { Star, Timer } from '@element-plus/icons-vue'

const data = reactive({
  tourismList: [],
  pageNum: 1,
  pageSize: 8,
  total: 0,
  sort: 'hot'
})
//跳转到指定路径
const goPage = (path) => {
  router.push(path)
}
//根据用户选择的排序规则（hot 或 new），更新排序类型并重新加载数据
const loadBySort = (sort) => {
  data.sort = sort
  load()
}
//从后端接口获取旅游产品数据，并更新分页和产品列表
const load = () => {
  request.get('/tourism/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      sort: data.sort
    }
  }).then(res => {
    if (res.code === '200') {
      data.tourismList = res.data?.list || []
      data.total = res.data?.total
    }
  })
}
load()
</script>

<style scoped>
.tourism-page {
  min-height: 100vh;
  background: #f5f7fa;
}

/* 顶部横幅 */
.hero-section {
  height: 400px;
  background-image: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url("@/assets/imgs/lysp.jpg");
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

/* 导航栏 */
.nav-section {
  background: white;
  border-radius: 16px;
  padding: 15px 20px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 15px;
}

.sort-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #666;
}

.sort-btn:hover {
  color: #409eff;
  background: #ecf5ff;
}

.sort-btn.active {
  color: #409eff;
  background: #ecf5ff;
  font-weight: 500;
}

.divider {
  width: 1px;
  height: 20px;
  background: #eee;
}

/* 产品列表 */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

.product-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.card-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .card-image img {
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

.product-card:hover .card-overlay {
  opacity: 1;
}

.card-content {
  padding: 20px;
}

.product-name {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.product-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #ff6b6b;
}

.currency {
  font-size: 14px;
  margin-right: 2px;
}

.amount {
  font-size: 24px;
  font-weight: 600;
}

.sales {
  color: #666;
  font-size: 14px;
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

  .nav-section {
    flex-direction: column;
    gap: 20px;
  }

  .sort-options {
    width: 100%;
    justify-content: center;
  }

  .products-grid {
    gap: 20px;
  }

  .card-image {
    height: 180px;
  }
}
</style>