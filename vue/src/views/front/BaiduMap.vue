<template>
  <div class="main-container">
    <!-- 选择交通方式 -->
    <div class="transport-options">
      <label :class="['transport-option', { selected: localTransportMode === 'driving' }]">
        <input type="radio" v-model="localTransportMode" value="driving" class="transport-radio" />
        <i class="icon driving"></i>
        <span>驾车</span>
      </label>
      <label :class="['transport-option', { selected: localTransportMode === 'walking' }]">
        <input type="radio" v-model="localTransportMode" value="walking" class="transport-radio" />
        <i class="icon walking"></i>
        <span>步行</span>
      </label>
      <label :class="['transport-option', { selected: localTransportMode === 'transit' }]">
        <input type="radio" v-model="localTransportMode" value="transit" class="transport-radio" />
        <i class="icon transit"></i>
        <span>公共交通</span>
      </label>
    </div>

    <!-- 输入起点和终点 -->
    <div class="inputs-container">
      <input v-model="localOrigin" placeholder="请输入起点" class="input-field" />
      <input v-model="localDestination" placeholder="请输入终点" class="input-field" />
      <button @click="getRoute" class="search-button">查询路线</button>
    </div>

    <!-- 显示地图的容器 -->
    <div id="container" class="map-container"></div>

    <!-- 路线规划结果 -->
    <div id="results" class="results-panel"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';

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

const emit = defineEmits(['update:origin', 'update:destination', 'update:transportMode', 'update:route']);

// 本地状态
const localOrigin = ref(props.origin);
const localDestination = ref(props.destination);
const localTransportMode = ref(props.transportMode);
const localRoute = ref(props.route);

// 监听 props 变化
watch(() => props.origin, (val) => localOrigin.value = val);
watch(() => props.destination, (val) => localDestination.value = val);
watch(() => props.transportMode, (val) => localTransportMode.value = val);
watch(() => props.route, (val) => localRoute.value = val);

// 监听本地变化，emit 给父组件
watch(localOrigin, (val) => emit('update:origin', val));
watch(localDestination, (val) => emit('update:destination', val));
watch(localTransportMode, (val) => emit('update:transportMode', val));
watch(localRoute, (val) => emit('update:route', val));

const handleMapError = () => {
  console.warn('百度地图资源加载失败，这可能是由于广告拦截器导致的，不影响主要功能')
}

onMounted(() => {
  try {
    window.init = () => {
      // 地图初始化代码
    }
    
    const script = document.createElement('script')
    script.src = `https://api.map.baidu.com/api?v=3.0&ak=YOUR_API_KEY&callback=init`
    script.onerror = handleMapError
    document.body.appendChild(script)
  } catch (error) {
    handleMapError()
  }
})

const getRoute = () => {
  if (!localOrigin.value || !localDestination.value) {
    alert("请填写完整的起点和终点！");
    return;
  }

  const geocoder = new BMap.Geocoder();

  geocoder.getPoint(localOrigin.value, (point) => {
    if (!point) {
      alert(`无法解析起点地址：${localOrigin.value}，请检查地址格式`);
      return;
    }

    geocoder.getPoint(localDestination.value, (destPoint) => {
      if (!destPoint) {
        alert(`无法解析终点地址：${localDestination.value}，请检查地址格式`);
        return;
      }

      if (localTransportMode.value === "driving") {
        searchDrivingRoute(point, destPoint);
      } else if (localTransportMode.value === "walking") {
        searchWalkingRoute(point, destPoint);
      } else if (localTransportMode.value === "transit") {
        searchTransitRoute(point, destPoint);
      }
    });
  });
}

const searchDrivingRoute = (origin, destination) => {
  localRoute.value = new BMap.DrivingRoute(props.map, {
    renderOptions: {
      map: props.map,
      panel: "results",
      autoViewport: true
    },
    onSearchComplete: onRouteSearchComplete
  });
  localRoute.value.search(origin, destination);
}

const searchWalkingRoute = (origin, destination) => {
  localRoute.value = new BMap.WalkingRoute(props.map, {
    renderOptions: {
      map: props.map,
      panel: "results",
      autoViewport: true
    },
    onSearchComplete: onRouteSearchComplete
  });
  localRoute.value.search(origin, destination);
}

const searchTransitRoute = (origin, destination) => {
  localRoute.value = new BMap.TransitRoute(props.map, {
    renderOptions: {
      map: props.map,
      panel: "results",
      autoViewport: true
    },
    onSearchComplete: onRouteSearchComplete
  });
  localRoute.value.search(origin, destination);
}

const onRouteSearchComplete = (results) => {
  if (localRoute.value.getStatus() === BMAP_STATUS_SUCCESS) {
    let steps = [];
    if (localTransportMode.value === "walking") {
      const plan = results.getPlan(0);
      const route = plan.getRoute(0);
      steps.push(`步行：${route.getDistance()} 米`);
    } else if (localTransportMode.value === "transit") {
      const plan = results.getPlan(0);
      for (let i = 0; i < plan.getNumRoutes(); i++) {
        const walk = plan.getRoute(i);
        if (walk.getDistance() > 0) {
          steps.push(`步行: ${walk.getDistance()} 米`);
        }
      }
    }

    document.getElementById("results").innerHTML = steps.join("<br>");
  } else {
    alert("路线查询失败，请检查起点和终点！");
  }
}
</script>

<style scoped>
/* 样式保持不变 */
.main-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 20px;
}

.map-container {
  width: 80%;
  height: 600px;
  margin-top: 20px;
}

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

.transport-options {
  display: flex;
  justify-content: center;
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
  display: none;
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

.results-panel {
  margin-top: 20px;
  padding: 15px;
  border: 1px solid #ddd;
  background-color: #f9f9f9;
  width: 80%;
  max-height: 400px;
  overflow-y: auto;
  font-size: 14px;
  line-height: 1.6;
  border-radius: 8px;
}
</style>