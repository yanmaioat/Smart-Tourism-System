<template>
  <div class="main-container">
    <!-- 选择交通方式 -->
    <div class="transport-options">
      <label :class="['transport-option', { selected: transportMode === 'driving' }]">
        <input type="radio" v-model="transportMode" value="driving" class="transport-radio" />
        <i class="icon driving"></i>
        <span>驾车</span>
      </label>
      <label :class="['transport-option', { selected: transportMode === 'walking' }]">
        <input type="radio" v-model="transportMode" value="walking" class="transport-radio" />
        <i class="icon walking"></i>
        <span>步行</span>
      </label>
      <label :class="['transport-option', { selected: transportMode === 'transit' }]">
        <input type="radio" v-model="transportMode" value="transit" class="transport-radio" />
        <i class="icon transit"></i>
        <span>公共交通</span>
      </label>
    </div>

    <!-- 输入起点和终点 -->
    <div class="inputs-container">
      <input v-model="origin" placeholder="请输入起点" class="input-field" />
      <input v-model="destination" placeholder="请输入终点" class="input-field" />
      <button @click="getRoute" class="search-button">查询路线</button>
    </div>

    <!-- 显示地图的容器 -->
    <div id="container" class="map-container"></div>

    <!-- 路线规划结果 -->
    <div id="results" class="results-panel"></div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';

const props = defineProps({
  origin: {
    type: String,
    default: ''
  },
  destination: {
    type: String,
    default: ''
  },
  transportMode: {
    type: String,
    default: 'driving'
  },
  map: {
    type: Object,
    default: null
  },
  route: {
    type: Object,
    default: null
  }
});

const handleMapError = () => {
  console.warn('百度地图资源加载失败，这可能是由于广告拦截器导致的，不影响主要功能')
}

onMounted(() => {
  try {
    // 原有的地图初始化代码
    window.init = () => {
      // 地图初始化代码
    }
    
    // 添加脚本加载错误处理
    const script = document.createElement('script')
    script.src = `https://api.map.baidu.com/api?v=3.0&ak=YOUR_API_KEY&callback=init`
    script.onerror = handleMapError
    document.body.appendChild(script)
  } catch (error) {
    handleMapError()
  }
})

const getRoute = () => {
  // 如果起点或终点为空，则提醒用户填写
  if (!props.origin || !props.destination) {
    alert("请填写完整的起点和终点！");
    return;
  }

  // 创建地理编码实例，用来解析起点和终点地址
  const geocoder = new BMap.Geocoder();

  // 获取起点的经纬度
  geocoder.getPoint(props.origin, (point) => {
    if (!point) {
      alert(`无法解析起点地址：${props.origin}，请检查地址格式`);
      return;
    }

    // 获取终点的经纬度
    geocoder.getPoint(props.destination, (destPoint) => {
      if (!destPoint) {
        alert(`无法解析终点地址：${props.destination}，请检查地址格式`);
        return;
      }

      // 根据选择的交通方式，调用相应的路线查询方法
      if (props.transportMode === "driving") {
        searchDrivingRoute(point, destPoint);
      } else if (props.transportMode === "walking") {
        searchWalkingRoute(point, destPoint);
      } else if (props.transportMode === "transit") {
        searchTransitRoute(point, destPoint);
      }
    });
  });
}

const searchDrivingRoute = (origin, destination) => {
  props.route = new BMap.DrivingRoute(props.map, {
    renderOptions: {
      map: props.map, // 在地图上显示路线
      panel: "results", // 在指定的元素中显示路线详情
      autoViewport: true // 自动调整视图，使整个路线都能显示
    },
    onSearchComplete: onRouteSearchComplete // 路线查询完成后的回调函数
  });
  props.route.search(origin, destination); // 发起驾车路线查询
}

const searchWalkingRoute = (origin, destination) => {
  props.route = new BMap.WalkingRoute(props.map, {
    renderOptions: {
      map: props.map, // 在地图上显示路线
      panel: "results", // 在指定的元素中显示路线详情
      autoViewport: true // 自动调整视图，使整个路线都能显示
    },
    onSearchComplete: onRouteSearchComplete // 路线查询完成后的回调函数
  });
  props.route.search(origin, destination); // 发起步行路线查询
}

const searchTransitRoute = (origin, destination) => {
  props.route = new BMap.TransitRoute(props.map, {
    renderOptions: {
      map: props.map, // 在地图上显示路线
      panel: "results", // 在指定的元素中显示路线详情
      autoViewport: true // 自动调整视图，使整个路线都能显示
    },
    onSearchComplete: onRouteSearchComplete // 路线查询完成后的回调函数
  });
  props.route.search(origin, destination); // 发起公共交通路线查询
}

const onRouteSearchComplete = (results) => {
  // 如果路线查询成功
  if (props.route.getStatus() === BMAP_STATUS_SUCCESS) {
    let steps = []; // 存储步骤的数组
    if (props.transportMode === "walking") {
      // 如果是步行路线，获取第一条方案
      const plan = results.getPlan(0);
      const route = plan.getRoute(0);
      steps.push(`步行：${route.getDistance()} 米`);
    } else if (props.transportMode === "transit") {
      // 如果是公共交通路线
      const plan = results.getPlan(0);
      for (let i = 0; i < plan.getNumRoutes(); i++) {
        const walk = plan.getRoute(i);
        if (walk.getDistance() > 0) {
          steps.push(`步行: ${walk.getDistance()} 米`);
        }
      }
    }

    // 显示路线的步骤
    document.getElementById("results").innerHTML = steps.join("<br>");
  } else {
    alert("路线查询失败，请检查起点和终点！");
  }
}
</script>

<style scoped>
/* 设置主容器的布局 */
.main-container {
  display: flex;
  flex-direction: column;
  align-items: center; /* 水平居中 */
  justify-content: center; /* 垂直居中 */
  min-height: 100vh; /* 满屏显示 */
  padding: 20px;
}

/* 设置地图容器的样式 */
.map-container {
  width: 80%; /* 将地图宽度调整为 70% */
  height: 600px; /* 地图的高度保持 500px */
  margin-top: 20px;
}


/* 输入框和按钮的样式 */
.input-field {
  width: 250px;
  padding: 10px;
  margin-right: 10px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.input-field:focus {
  border-color: #4CAF50;
}

.search-button {
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.search-button:hover {
  background-color: #45a049;
}

/* 交通方式选择的样式 */
.transport-options {
  display: flex;
  justify-content: center; /* 交通方式选择居中 */
  align-items: center;
  padding: 0;
  margin-bottom: 20px;
}

.transport-option {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 12px;
  margin-right: 20px;
  border: 1px solid #ddd;
  border-radius: 30px;
  transition: background-color 0.3s, border-color 0.3s;
  font-size: 14px;
}

.transport-option.selected {
  background-color: #4CAF50;
  border-color: #4CAF50;
  color: white;
}

.transport-option:hover {
  background-color: #f0f0f0;
}

.transport-radio {
  display: none; /* 隐藏原始单选框 */
}

.transport-option .icon {
  width: 18px;
  height: 18px;
  margin-right: 6px;
}

.transport-option .driving {
  background: url('https://img.icons8.com/ios-filled/50/4CAF50/car.png') no-repeat center center;
  background-size: 18px 18px;
}

.transport-option .walking {
  background: url('https://img.icons8.com/ios-filled/50/4CAF50/walk.png') no-repeat center center;
  background-size: 18px 18px;
}

.transport-option .transit {
  background: url('https://img.icons8.com/ios-filled/50/4CAF50/bus.png') no-repeat center center;
  background-size: 18px 18px;
}

/* 路线规划结果样式 */
.results-panel {
  margin-top: 20px;
  padding: 15px;
  border: 1px solid #ddd;
  background-color: #f9f9f9;
  width: 80%; /* 增加显示宽度 */
  max-height: 400px; /* 设置最大高度 */
  overflow-y: auto; /* 如果内容超出，允许垂直滚动 */
  font-size: 14px; /* 调整字体大小 */
  line-height: 1.6; /* 增加行间距，便于阅读 */
  border-radius: 8px; /* 增加圆角，提升美观 */
}

</style>
