<template>
  <div class="home-container">
    <!-- 顶部大图轮播 -->
    <div class="hero-section">
      <el-carousel :interval="5000" type="card" height="500px">
        <el-carousel-item v-for="(item, index) in data.imgs" :key="index">
          <div class="carousel-card">
            <img :src="item" alt="" class="carousel-img">
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧游记区域 -->
      <div class="travels-section">
        <div class="section-header">
          <div class="tab-group">
            <div 
              class="tab-item" 
              :class="{ active: data.sort === 'hot' }" 
              @click="loadBySort(
                  'hot')"
            >
              <i class="el-icon-hot-water"></i>热门游记
            </div>
            <div 
              class="tab-item" 
              :class="{ active: data.sort === 'new' }" 
              @click="loadBySort('new')"
            >
              <i class="el-icon-time"></i>最新发表
            </div>
          </div>
          <el-button 
            type="primary" 
            @click="goPage('/front/addTravel')" 
            class="write-btn"
          >
            <el-icon><Edit /></el-icon>写游记
          </el-button>
        </div>

        <!-- 游记列表 -->
        <div class="travels-list">
          <div v-for="item in data.travelList" :key="item.id" class="travel-card">
            <img :src="item.cover" alt="" class="travel-img">
            <div class="travel-content">
              <a :href="'/front/travelDetail?id=' + item.id" class="travel-title">
                {{ item.title }}
              </a>
              <p class="travel-desc">{{ item.descr }}</p>
              <div class="travel-meta">
                <span class="meta-item">
                  <el-icon><Location /></el-icon>
                  {{ item.location }}
                </span>
                <span class="meta-item">
                  <el-icon><User /></el-icon>
                  {{ item.userName }}
                </span>
                <span class="meta-item">
                  <el-icon><View /></el-icon>
                  {{ item.readCount }}
                </span>
                <span class="meta-item">
                  <el-icon><Clock /></el-icon>
                  {{ item.time }}
                </span>
                <div class="likes-count">
                  <span class="likes-number">{{ item.praiseCount }}</span>
                  <el-button 
                    class="like-btn" 
                    size="small" 
                    @click="top(item.id)"
                  >
                    点赞
                  </el-button>
                </div>
              </div>
            </div>
          </div>

          <!-- 分页器 -->
          <div class="pagination" v-if="data.total">
            <el-pagination
              @current-change="loadTravels"
              :current-page="data.pageNum"
              :page-size="data.pageSize"
              :total="data.total"
              background
              layout="prev, pager, next"
            />
          </div>
        </div>
      </div>

      <!-- 右侧边栏 -->
      <div class="sidebar">
        <!-- 攻略推荐 -->
        <div class="sidebar-section">
          <div class="section-title">
            <h3>旅游攻略推荐</h3>
            <a href="/front/article" class="more-link">更多 <i class="el-icon-arrow-right"></i></a>
          </div>
          <div class="article-list">
            <div v-for="item in data.articleList" :key="item.id" class="article-card">
              <img :src="item.cover" alt="" class="article-img">
              <a :href="'/front/articleDetail?id=' + item.id" class="article-title">
                {{ item.title }}
              </a>
            </div>
          </div>
        </div>

        <!-- 最新公告 -->
        <div class="sidebar-section">
          <div class="section-title">
            <h3>最新公告</h3>
          </div>
          <div class="notice-list">
            <div v-for="item in data.noticeList" :key="item.id" class="notice-item">
              {{ item.content }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import img1 from '@/assets/imgs/1.jpg'
import img2 from '@/assets/imgs/2.jpg'
import img3 from '@/assets/imgs/3.jpg'
import { Edit, Location, View, Clock } from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user')),
  imgs: [img1, img2, img3],
  sort: 'hot',
  pageNum: 1,
  pageSize: 5,
  total: 0,
  travelList: [],
  articleList: [],
  noticeList: []
});

const top = (id) => {
  request.post('/praise/add', { fid: id, userId: data.user.id }).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功');
      loadTravels();
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const goPage = (path) => {
  location.href = path;
};

// 获取公告
request.get('/notice/selectAll').then(res => {
  data.noticeList = res.data.splice(0, 3);
});

// 获取推荐的旅游攻略
request.get('/article/selectRecommend').then(res => {
  data.articleList = res.data;
});

// 加载游记数据
const loadBySort = (sort) => {
  data.sort = sort;
  loadTravels();
};

const loadTravels = () => {
  request.get('/travels/selectFrontPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      sort: data.sort
    }
  }).then(res => {
    if (res.code === '200') {
      data.travelList = res.data?.list || [];
      data.total = res.data?.total;
    }
  });
};

loadTravels();
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: #f5f7fa;
}

/* 轮播图区域 */
.hero-section {
  margin-bottom: 40px;
  background: linear-gradient(to bottom, #fff, #f5f7fa);
  padding: 20px 0;
}

.carousel-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.carousel-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.carousel-card:hover .carousel-img {
  transform: scale(1.05);
}

/* 主要内容区域 */
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 40px;
}

/* 游记区域 */
.travels-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  padding: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
}

.tab-group {
  display: flex;
  gap: 20px;
}

.tab-item {
  font-size: 16px;
  padding: 8px 16px;
  cursor: pointer;
  border-radius: 20px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.tab-item:hover {
  color: #42b983;
  background: rgba(66, 185, 131, 0.1);
}

.tab-item.active {
  color: #42b983;
  background: rgba(66, 185, 131, 0.1);
  font-weight: 500;
}

.write-btn {
  padding: 8px 20px;
  border-radius: 20px;
}

/* 游记卡片 */
.travel-card {
  display: grid;
  grid-template-columns: 220px 1fr;
  gap: 20px;
  padding: 20px;
  border-bottom: 1px solid #eee;
  transition: all 0.3s ease;
}

.travel-card:hover {
  background: #f9f9f9;
}

.travel-img {
  width: 100%;
  height: 160px;
  object-fit: cover;
  border-radius: 8px;
  transition: transform 0.3s ease;
}

.travel-card:hover .travel-img {
  transform: translateY(-2px);
}

.travel-content {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.travel-title {
  font-size: 18px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 12px;
  text-decoration: none;
}

.travel-desc {
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.travel-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  color: #999;
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.likes-count {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 8px;
}

.likes-number {
  font-size: 18px;
  color: #ff9800;
  font-weight: 500;
}

.like-btn {
  padding: 4px 12px;
  border-radius: 15px;
}

/* 右侧边栏 */
.sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sidebar-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.section-title h3 {
  font-size: 16px;
  font-weight: 500;
  color: #2c3e50;
}

.more-link {
  color: #999;
  text-decoration: none;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 攻略卡片 */
.article-card {
  margin-bottom: 20px;
}

.article-img {
  width: 100%;
  height: 160px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 10px;
}

.article-title {
  color: #2c3e50;
  text-decoration: none;
  font-size: 15px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 公告列表 */
.notice-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notice-item {
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

/* 分页器 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .main-content {
    grid-template-columns: 1fr;
  }
  
  .sidebar {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  }
}

@media (max-width: 768px) {
  .travel-card {
    grid-template-columns: 1fr;
  }
  
  .travel-img {
    height: 200px;
  }
}
</style>
