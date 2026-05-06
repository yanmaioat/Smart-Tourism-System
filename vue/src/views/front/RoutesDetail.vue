<template>
  <div class="route-detail">
    <!-- 顶部横幅 -->
    <div class="hero-banner" :style="{ backgroundImage: `url(${data.routes.img})` }">
      <!-- 返回按钮 -->
      <div class="back-button" @click="router.back()">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回列表</span>
      </div>

      <div class="banner-content">
        <h1 class="detail-title">{{ data.routes.name }}</h1>
        <div class="basic-info">
          <div class="info-badge">
            <el-icon><Location /></el-icon>
            <span>{{ data.routes.location }}</span>
          </div>
          <div class="info-badge">
            <el-icon><Timer /></el-icon>
            <span>建议游玩 {{ data.routes.days }} 天</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-container">
      <div class="content-wrapper">
        <!-- 左侧内容区 -->
        <div class="left-section">
          <!-- 小贴士卡片 -->
          <div class="info-card tips-card">
            <div class="card-header">
              <el-icon><Warning /></el-icon>
              <span>游玩小贴士</span>
            </div>
            <div class="card-content">
              {{ data.routes.tips }}
            </div>
          </div>

          <!-- 景点介绍 -->
          <div class="info-card">
            <div class="card-header">
              <el-icon><InfoFilled /></el-icon>
              <span>景点介绍</span>
            </div>
            <div class="card-content rich-content" v-html="data.routes.content"></div>
          </div>
        </div>

        <!-- 右侧路线规划 -->
        <div class="right-section">
          <div class="planner-card">
            <!-- AI助手按钮 -->
            <div class="ai-helper" @click="router.push('/front/aiChat')">
              <el-icon><ChatDotRound /></el-icon>
              <span>问问AI助手</span>
            </div>

            <div class="card-header">
              <h2>路线规划</h2>
              <p>规划您的出行路线</p>
            </div>

            <!-- 交通方式选择 -->
            <div class="transport-options">
              <div 
                v-for="mode in transportModes" 
                :key="mode.value"
                :class="['transport-option', { active: transportMode === mode.value }]"
                @click="transportMode = mode.value"
              >
                <el-icon>{{ mode.icon }}</el-icon>
                <span>{{ mode.label }}</span>
              </div>
            </div>

            <!-- 路线输入 -->
            <div class="route-inputs">
              <el-input
                v-model="origin"
                placeholder="请输入起点"
                :prefix-icon="Location"
              >
                <template #append>
                  <el-button @click="useCurrentLocation">
                    <el-icon><Aim /></el-icon>
                  </el-button>
                </template>
              </el-input>

              <el-input
                v-model="destination"
                :placeholder="data.routes.location"
                :prefix-icon="Location"
                disabled
              />

              <el-button 
                type="primary" 
                class="search-btn" 
                @click="getRoute"
                :loading="loading"
              >
                {{ loading ? '规划中...' : '开始规划' }}
              </el-button>
            </div>

            <!-- 地图容器 -->
            <div class="map-wrapper">
              <div id="container" class="map-container"></div>
            </div>

            <!-- 路线结果 -->
            <div id="results" class="route-results"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, nextTick } from "vue"; // Vue 响应式工具
import request from "@/utils/request.js"; // 请求工具
import router from "@/router/index.js"; // Vue Router 实例
import { Location, Timer, Warning, Aim, ArrowLeft, ChatDotRound } from '@element-plus/icons-vue'; // Element Plus 图标组件
import { ElMessage } from 'element-plus'; // 提示消息

// 数据响应式管理
const data = reactive({ // 定义响应式数据
  id: router.currentRoute.value.query.id, // 获取路由参数中的景点ID
  routes: {} // 存储景点详情
});

const transportMode = ref('driving'); // 当前选择的交通方式，默认为驾车
const origin = ref(''); // 起点地址
const destination = ref(''); // 终点地址
let map = null; // 百度地图实例

// 交通方式选项
const transportModes = [ // 定义交通方式数组
  { label: '驾车', value: 'driving', icon: 'Car' }, // 驾车
  { label: '步行', value: 'walking', icon: 'Walk' }, // 步行
  { label: '公交', value: 'transit', icon: 'Van' } // 公交
];

const loading = ref(false); // 是否正在加载路线规划

// 使用当前位置
const useCurrentLocation = () => { // 获取用户的当前位置
  if (navigator.geolocation) { //通过 navigator.geolocation 判断浏览器是否支持地理位置功能（如 Chrome、Firefox 等现代浏览器）
    navigator.geolocation.getCurrentPosition( // 调用浏览器的 getCurrentPosition 方法获取用户的当前经纬度
        position => {
          const { latitude, longitude } = position.coords; // 从 position 对象中解构出纬度 (latitude) 和经度 (longitude)
          const point = new BMapGL.Point(longitude, latitude); //使用百度地图 SDK 的 BMapGL.Point 类，将经纬度转换为百度地图的坐标点对象
          const geocoder = new BMapGL.Geocoder(); // 创建地理编码器实例

          geocoder.getLocation(point, result => { //调用 getLocation 方法，将经纬度点 (point) 转换为具体地址
            if (result) {
              origin.value = result.address; // 设置起点为当前位置，如果转换成功，将地址信息 (result.address) 赋值给 origin.value
            }
          });
        },
        error => { // 错误处理
          ElMessage.error('获取位置失败：' + error.message); // 显示错误提示
        }
    );
  } else { // 如果不支持地理定位，Element UI/Plus 的提示组件显示错误信息
    ElMessage.error('您的浏览器不支持地理定位');
  }
};

// 获取路线规划
const getRoute = () => { // 规划路线
  if (!origin.value) { // 检查起点是否为空
    ElMessage.warning('请输入起点位置'); // 提示用户输入起点
    return;
  }

  loading.value = true; // 开启加载状态

  const resultsPanel = document.getElementById('results'); // 获取路线结果显示区域
  if (resultsPanel) {
    resultsPanel.innerHTML = ''; // 清空之前的路线结果
  }

  map?.clearOverlays(); // 清除地图上的覆盖物（如之前的路线）

  const geocoder = new BMapGL.Geocoder(); // 创建地理编码器实例

  geocoder.getPoint(origin.value, startPoint => { // 根据起点地址解析经纬度
    if (!startPoint) { // 如果无法解析起点地址
      loading.value = false;
      ElMessage.error('无法解析起点地址');
      return;
    }

    const endPoint = new BMapGL.Point(data.routes.longitude, data.routes.latitude); // 创建终点点对象

    const createRoutePlanner = (type, label) => { // 创建路线规划实例
      const options = { // 配置选项
        renderOptions: {
          map: map, // 渲染到地图上
          panel: "results", // 显示在指定面板中
          autoViewport: true // 自动调整视图范围
        },
        onSearchComplete: function(results) { // 搜索完成回调
          loading.value = false; // 关闭加载状态
          if (results && results.getNumPlans && results.getNumPlans() > 0) { // 检查是否有可用路线
            const plan = results.getPlan(0); // 获取第一条路线
            const distance = plan.getDistance(true); // 获取总距离
            const duration = plan.getDuration(true); // 获取预计时间

            const routeResults = []; // 存储路线结果HTML
            routeResults.push(`
              <div class="route-summary">
                <h3>${label}路线</h3>
                <p>总距离：${distance}</p>
                <p>预计时间：${duration}</p>
              </div>
              <div class="route-steps">
            `);

            const route = plan.getRoute(0); // 获取路线详细信息
            const steps = route.getNumSteps(); // 获取步骤数量
            for (let i = 0; i < steps; i++) { // 遍历每个步骤
              const step = route.getStep(i);
              routeResults.push(`
                <div class="route-step">
                  ${i + 1}. ${step.getDescription()}
                </div>
              `);
            }

            routeResults.push('</div>');
            document.getElementById('results').innerHTML = routeResults.join(''); // 渲染到页面
            ElMessage.success('路线规划成功'); // 提示成功
          } else { // 如果没有找到合适的路线
            ElMessage.error('未找到合适的路线');
          }
        },
        onError: function() { // 错误处理
          loading.value = false;
          ElMessage.error('路线规划失败，请稍后重试');
        }
      };
      return new BMapGL[`${type}Route`](map, options); // 根据交通方式创建路线规划实例
    };

    let routePlanner; // 路线规划器实例
    switch (transportMode.value) { // 根据交通方式选择不同的路线规划
      case 'driving':
        routePlanner = createRoutePlanner('Driving', '驾车');
        break;
      case 'walking':
        routePlanner = createRoutePlanner('Walking', '步行');
        break;
      case 'transit':
        routePlanner = createRoutePlanner('Transit', '公交');
        break;
    }

    routePlanner.search(startPoint, endPoint); // 开始搜索路线
  });
};

// 初始化地图
onMounted(() => { // 页面加载完成后执行
  request.get('/routes/selectById/' + data.id).then(res => { // 请求景点详情
    data.routes = res.data; // 更新景点数据

    nextTick(() => { // 确保DOM已更新
      map = new BMapGL.Map('container'); // 初始化百度地图实例
      const point = new BMapGL.Point(data.routes.longitude, data.routes.latitude); // 创建中心点
      map.centerAndZoom(point, 15); // 设置中心点和缩放级别
      map.enableScrollWheelZoom(); // 启用鼠标滚轮缩放

      const marker = new BMapGL.Marker(point); // 创建标记
      map.addOverlay(marker); // 添加标记到地图

      const infoWindow = new BMapGL.InfoWindow( // 创建信息窗口
          `<div style="padding: 8px">
          <h4 style="margin: 0 0 5px 0">${data.routes.name}</h4>
          <p style="margin: 0">${data.routes.location}</p>
        </div>`,
          {
            width: 250,
            height: 80,
            title: '景点信息'
          }
      );

      marker.addEventListener('click', () => { // 点击标记时打开信息窗口
        map.openInfoWindow(infoWindow, point);
      });
    });
  });
});
</script>

<style scoped>
.route-detail {
  min-height: 100vh;
  background: #f5f7fa;
}

/* 顶部横��� */
.hero-banner {
  height: 400px;
  background-size: cover;
  background-position: center;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  text-align: center;
}

.hero-banner::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
}

.banner-content {
  position: relative;
  z-index: 1;
}

.detail-title {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 20px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.basic-info {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.info-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.2);
  padding: 8px 16px;
  border-radius: 20px;
  backdrop-filter: blur(10px);
}

/* 主要内容区域 */
.main-container {
  margin-top: -60px;
  padding: 0 20px;
  position: relative;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 30px;
}

/* 左侧内容区 */
.left-section {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.info-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.card-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-content {
  padding: 20px;
}

.tips-card {
  background: #fff8e1;
}

.tips-card .card-header {
  color: #ff9800;
  border-color: rgba(255, 152, 0, 0.2);
}

.rich-content {
  color: #2c3e50;
  line-height: 1.8;
}

/* 右侧路线规划 */
.planner-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 20px;
}

.transport-options {
  display: flex;
  gap: 12px;
  margin: 20px 0;
}

.transport-option {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f5f7fa;
}

.transport-option.active {
  background: #ecf5ff;
  color: #409eff;
}

.route-inputs {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.map-wrapper {
  position: relative;
  height: 300px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
}

.map-container {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
}

.route-results {
  max-height: 300px;
  overflow-y: auto;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .content-wrapper {
    grid-template-columns: 1fr;
  }
  
  .planner-card {
    position: static;
    margin-bottom: 30px;
  }
}

@media (max-width: 768px) {
  .hero-banner {
    height: 300px;
  }
  
  .detail-title {
    font-size: 32px;
  }
  
  .basic-info {
    flex-direction: column;
    align-items: center;
  }
  
  .main-container {
    margin-top: -40px;
  }
}

/* 路线结果样式 */
.route-summary {
  background: white;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 15px;
}

.route-summary h3 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 16px;
}

.route-summary p {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

.route-steps {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.route-step {
  background: white;
  padding: 12px;
  border-radius: 8px;
  color: #2c3e50;
  font-size: 14px;
  line-height: 1.4;
}

/* 导航按钮容器 */
.nav-buttons {
  display: none;
}

/* 返回按钮 */
.back-button {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 10;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
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
  transform: translateX(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

/* AI助手按钮 */
.ai-helper {
  margin-bottom: 20px;
  padding: 12px;
  background: #ecf5ff;
  color: #409eff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.ai-helper:hover {
  background: #409eff;
  color: white;
}

.ai-helper .el-icon {
  font-size: 18px;
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