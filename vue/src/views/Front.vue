<template>
  <div class="app-container">
    <!-- 顶部通知栏 -->
    <div class="front-notice">
      <div class="notice-content">
        <div class="notice-left">
          <el-icon><Bell /></el-icon>
          <div class="notice-text">{{ '上清远旅游网，带您游览清远！' }}</div>
        </div>
        <iframe 
          class="weather-widget"
          scrolling="no" 
          src="https://widget.tianqiapi.com/?style=tg&skin=pitaya&city=清远"
          frameborder="0" 
          allowtransparency="true"
        ></iframe>
      </div>
    </div>

    <!-- 主导航栏 -->
    <div class="front-header" :class="{ 'header-shadow': isScrolled }">
      <div class="header-content">
        <!-- Logo区域 -->
        <router-link to="/front/home" class="header-logo">
          <div class="logo-wrapper">
            <img src="@/assets/imgs/logo.png" alt="恩施旅游网">
            <span class="logo-text">清远旅游网</span>
          </div>
        </router-link>

        <!-- 导航菜单 -->
        <el-menu 
          :default-active="router.currentRoute.value.path" 
          router 
          mode="horizontal"
          class="nav-menu"
        >
          <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            {{ item.label }}
          </el-menu-item>
        </el-menu>

        <!-- 用户区域 -->
        <div class="user-box">
          <template v-if="!data.user.id">
            <el-button type="primary" class="login-btn" @click="router.push('/login')">
              <el-icon><User /></el-icon>登录
            </el-button>
            <el-button class="register-btn" @click="router.push('/register')">注册</el-button>
          </template>
          <template v-else>
            <el-dropdown trigger="click">
              <div class="user-info">
                <el-avatar :size="36" :src="data.user.avatar" />
                <span class="username">{{ data.user.name }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu class="custom-dropdown">
                  <el-dropdown-item v-for="item in userMenuItems" 
                    :key="item.path" 
                    @click="handleMenuClick(item)"
                    :class="{ 'menu-divider': item.divider }"
                  >
                    <el-icon><component :is="item.icon" /></el-icon>
                    {{ item.label }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-body">
      <RouterView @updateUser="updateUser" />
    </div>
    <Footer />
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, onUnmounted } from "vue";
import router from "@/router/index.js";
import request from "@/utils/request.js";
import Footer from '@/components/Footer.vue'
import { ElMessage } from 'element-plus'
import { 
  House, Location, Reading, Ticket, MapLocation, Service, 
  ShoppingBag, Bell, User, ArrowDown, ShoppingCart,
  ChatDotRound, Notebook, Star, Key, SwitchButton
} from '@element-plus/icons-vue'

// 导航菜单配置
const menuItems = [
  { path: '/front/home', label: '首页', icon: 'House' },
  { path: '/front/tourism', label: '旅游产品', icon: 'ShoppingBag' },
  { path: '/front/routes', label: '旅游地点', icon: 'Location' },
  { path: '/front/article', label: '旅游攻略', icon: 'Reading' },
  { path: '/front/train', label: '火车票购买', icon: 'Ticket' },
  { path: '/front/feedback', label: '问题反馈', icon: 'Service' },
]

// 用户下拉菜单配置
const userMenuItems = [
  { path: '/front/orders', label: '我的订单', icon: 'ShoppingCart' },
  { path: '/front/sale', label: '已定车票', icon: 'Ticket' },
  { path: '/front/myFeedback', label: '我的反馈', icon: 'ChatDotRound' },
  { path: '/front/travels', label: '我的游记', icon: 'Notebook' },
  { path: '/front/collect', label: '我的收藏', icon: 'Star' },
  { path: '/front/person', label: '个人信息', icon: 'User', divider: true },
  { path: '/front/password', label: '修改密码', icon: 'Key' },
  { path: 'logout', label: '退出登录', icon: 'SwitchButton', divider: true },
]

const isScrolled = ref(false)

// 监听滚动事件
const handleScroll = () => {
  isScrolled.value = window.scrollY > 0
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  top: '',
  noticeData: [],
})

const handleMenuClick = (item) => {
  if (item.path === 'logout') {
    // 处理退出登录
    localStorage.removeItem('xm-user')
    data.user = {} // 清空当前用户状态
    ElMessage.success('退出登录成功')
    // 根据当前路径决定跳转目标
    const targetPath = router.currentRoute.value.path.includes('/manager/') ? '/adminLogin' : '/login'
    router.push(targetPath)
  } else {
    // 处理普通路由跳转
    router.push(item.path)
  }
}

const updateUser = () => {
  data.user = JSON.parse(localStorage.getItem('xm-user') || '{}')
}

const loadNotice = () => {
  request.get('/notice/selectAll').then(res => {
    data.noticeData = res.data
    let i = 0
    if (data.noticeData && data.noticeData.length) {
      data.top = data.noticeData[0].content
      setInterval(() => {
        data.top = data.noticeData[i].content
        i++
        if (i === data.noticeData.length) {
          i = 0
        }
      }, 2500)
    }
  })
}
loadNotice()
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 顶部通知栏 */
.front-notice {
  background: linear-gradient(45deg, #42b983, #2c3e50);
  color: white;
  padding: 8px 0;
}

.notice-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.notice-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.notice-text {
  font-size: 14px;
  opacity: 0.9;
}

.weather-widget {
  width: 470px;
  height: 40px;
  border-radius: 4px;
  overflow: hidden;
}

/* 主导航栏 */
.front-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-shadow {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  height: 70px;
  display: flex;
  align-items: center;
  gap: 40px;
}

/* Logo */
.header-logo {
  text-decoration: none;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.logo-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-logo img {
  height: 40px;
  width: auto;
  transition: transform 0.3s ease;
}

.logo-text {
  font-size: 22px;
  font-weight: bold;
  background: linear-gradient(45deg, #42b983, #2c3e50);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  white-space: nowrap;
}

/* 导航菜单 */
.nav-menu {
  flex: 1;
  border: none !important;
  --el-menu-hover-bg-color: transparent;
  --el-menu-active-color: #42b983;
}

.nav-menu :deep(.el-menu-item) {
  height: 70px;
  line-height: 70px;
  padding: 0 24px;
  font-size: 15px;
  transition: all 0.3s ease;
  border-bottom: none !important;
}

.nav-menu :deep(.el-menu-item.is-active) {
  font-weight: 600;
  color: #42b983;
}

.nav-menu :deep(.el-menu-item:not(.is-active):hover) {
  color: #42b983;
}

/* 用户区域 */
.user-box {
  display: flex;
  align-items: center;
  gap: 12px;
}

.login-btn {
  display: flex;
  align-items: center;
  gap: 6px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 30px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f5f7fa;
}

.user-info:hover {
  background: #ecf5ff;
}

.username {
  font-size: 15px;
  color: #2c3e50;
}

/* 下拉菜单 */
.custom-dropdown {
  min-width: 200px;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
}

:deep(.menu-divider) {
  border-top: 1px solid #eee;
  margin-top: 4px;
  padding-top: 12px;
}

/* 主体内容 */
.main-body {
  flex: 1;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .header-content {
    gap: 20px;
  }
  
  .nav-menu :deep(.el-menu-item) {
    padding: 0 16px;
  }
}

@media (max-width: 992px) {
  .nav-menu :deep(.el-menu-item) {
    padding: 0 12px;
    font-size: 14px;
  }
  
  .weather-widget {
    display: none;
  }
}
</style>