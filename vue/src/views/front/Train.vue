<template>
  <div class="train-page">
    <!-- 顶部横幅 -->
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">火车票预订</h1>
        <p class="hero-subtitle">便捷的火车票购买服务</p>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 搜索区域 -->
      <div class="search-section">
        <div class="search-form">
          <div class="form-group">
            <el-input
              placeholder="出发地"
              prefix-icon="Location"
              clearable
            />
            <el-button circle class="exchange-btn">
              <el-icon><Switch /></el-icon>
            </el-button>
            <el-input
              placeholder="目的地"
              prefix-icon="Location"
              clearable
            />
          </div>
          <div class="form-group">
            <el-date-picker
              type="date"
              placeholder="出发日期"
              style="width: 240px"
            />
            <el-button type="primary" class="search-btn">
              <el-icon><Search /></el-icon>
              搜索车票
            </el-button>
          </div>
        </div>
      </div>

      <!-- 热门车票列表 -->
      <div class="ticket-section">
        <div class="section-header">
          <h2 class="section-title">热门车票</h2>
          <div class="sort-options">
            <span 
              @click="loadBySort('hot')" 
              :class="['sort-btn', { active: data.sort === 'hot' }]"
            >
              <el-icon><Star /></el-icon>
              最热门
            </span>
            <div class="divider"></div>
            <span 
              @click="loadBySort('new')" 
              :class="['sort-btn', { active: data.sort === 'new' }]"
            >
              <el-icon><Timer /></el-icon>
              最新发布
            </span>
          </div>
        </div>

        <!-- 车票列表 -->
        <div class="tickets-grid">
          <div 
            v-for="item in data.trainList" 
            :key="item.id" 
            class="ticket-card"
            @click="goPage('/front/trainDetail?id=' + item.id)"
          >
            <div class="card-image">
              <img :src="item.img" :alt="item.name">
              <div class="card-tag">
                <el-tag size="small" type="success">热门</el-tag>
              </div>
            </div>
            <div class="card-content">
              <h3 class="train-name">{{ item.name }}</h3>
              <div class="train-info">
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
  </div>
</template>

<script setup>
import { reactive } from "vue";
import request from "@/utils/request.js";
import router from "@/router/index.js";
import { Location, Search, Switch, Star, Timer } from '@element-plus/icons-vue'

const data = reactive({
  trainList: [],
  pageNum: 1,
  pageSize: 8,
  total: 0,
  sort: 'hot'
})

const goPage = (path) => {
  router.push(path)
}

const loadBySort = (sort) => {
  data.sort = sort
  load()
}

const load = () => {
  request.get('/train/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      sort: data.sort
    }
  }).then(res => {
    if (res.code === '200') {
      data.trainList = res.data?.list || []
      data.total = res.data?.total
    }
  })
}
load()
</script>

<style scoped>
.train-page {
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

/* 搜索区域 */
.search-section {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  margin-bottom: 30px;
}

.search-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  align-items: center;
  gap: 15px;
}

.exchange-btn {
  flex-shrink: 0;
}

.search-btn {
  height: 40px;
  padding: 0 30px;
  font-size: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 车票列表区域 */
.ticket-section {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
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

/* 车票卡片网格 */
.tickets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

.ticket-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
  border: 1px solid #eee;
}

.ticket-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.card-image {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.ticket-card:hover .card-image img {
  transform: scale(1.05);
}

.card-tag {
  position: absolute;
  top: 12px;
  right: 12px;
}

.card-content {
  padding: 20px;
}

.train-name {
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

.train-info {
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

  .search-form {
    gap: 15px;
  }

  .form-group {
    flex-direction: column;
    width: 100%;
  }

  .exchange-btn {
    transform: rotate(90deg);
  }

  .search-btn {
    width: 100%;
  }

  .section-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .tickets-grid {
    gap: 20px;
  }

  .card-image {
    height: 160px;
  }
}
</style>