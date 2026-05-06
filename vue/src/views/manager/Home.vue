<template>
  <div class="dashboard">
    <!-- 数据概览卡片 -->
    <div class="stat-cards">
      <div class="stat-card">
        <div class="card-icon">
          <img src="@/assets/imgs/游记.png" alt="游记">
        </div>
        <div class="card-content">
          <h3 class="card-title">游记总数</h3>
          <div class="card-value">{{ data.count.travelsCount }}</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="card-icon">
          <img src="@/assets/imgs/攻略.png" alt="攻略">
        </div>
        <div class="card-content">
          <h3 class="card-title">攻略总数</h3>
          <div class="card-value">{{ data.count.articleCount }}</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="card-icon">
          <img src="@/assets/imgs/商品.png" alt="商品">
        </div>
        <div class="card-content">
          <h3 class="card-title">商品总数</h3>
          <div class="card-value">{{ data.count.tourismCount }}</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="card-icon">
          <img src="@/assets/imgs/订单.png" alt="订单">
        </div>
        <div class="card-content">
          <h3 class="card-title">订单总数</h3>
          <div class="card-value">{{ data.count.ordersCount }}</div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="chart-container">
      <div class="chart-card">
        <div id="line" class="chart"></div>
      </div>
      <div class="chart-card">
        <div id="line1" class="chart"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from "vue";
import request from "@/utils/request.js";
import * as echarts from 'echarts'

// 订单趋势图配置
const lineOption = {
  title: {
    text: '订单销量趋势图',
    subtext: '折线图',
    left: 'center',
    top: 10,
    textStyle: {
      fontSize: 18,
      fontWeight: 'bold',
      color: '#2c3e50'
    },
    subtextStyle: {
      fontSize: 14,
      color: '#666'
    }
  },
  tooltip: {
    trigger: 'axis',
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    borderColor: '#eee',
    borderWidth: 1,
    textStyle: {
      color: '#333'
    },
    axisPointer: {
      type: 'shadow'
    }
  },
  grid: {
    top: 80,
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: [],
    axisLine: {
      lineStyle: {
        color: '#ddd'
      }
    },
    axisLabel: {
      color: '#666'
    }
  },
  yAxis: {
    type: 'value',
    splitLine: {
      lineStyle: {
        color: '#eee'
      }
    },
    axisLabel: {
      color: '#666'
    }
  },
  series: [
    {
      data: [],
      type: 'line',
      smooth: true,
      lineStyle: {
        width: 3,
        color: '#42b983'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(66, 185, 131, 0.3)' },
          { offset: 1, color: 'rgba(66, 185, 131, 0.1)' }
        ])
      },
      itemStyle: {
        color: '#42b983',
        borderWidth: 2
      }
    }
  ]
}

// 游记趋势图配置
const lineOption1 = {
  title: {
    text: '游记发布数量趋势图',
    subtext: '折线图',
    left: 'center',
    top: 10,
    textStyle: {
      fontSize: 18,
      fontWeight: 'bold',
      color: '#2c3e50'
    },
    subtextStyle: {
      fontSize: 14,
      color: '#666'
    }
  },
  tooltip: {
    trigger: 'axis',
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    borderColor: '#eee',
    borderWidth: 1,
    textStyle: {
      color: '#333'
    },
    axisPointer: {
      type: 'shadow'
    }
  },
  grid: {
    top: 80,
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: [],
    axisLine: {
      lineStyle: {
        color: '#ddd'
      }
    },
    axisLabel: {
      color: '#666'
    }
  },
  yAxis: {
    type: 'value',
    splitLine: {
      lineStyle: {
        color: '#eee'
      }
    },
    axisLabel: {
      color: '#666'
    }
  },
  series: [
    {
      data: [],
      type: 'line',
      smooth: true,
      lineStyle: {
        width: 3,
        color: '#3498db'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(52, 152, 219, 0.3)' },
          { offset: 1, color: 'rgba(52, 152, 219, 0.1)' }
        ])
      },
      itemStyle: {
        color: '#3498db',
        borderWidth: 2
      }
    }
  ]
}

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  count: {}
})

// 加载数据
request.get('/count').then(res => {
  data.count = res.data
})

onMounted(() => {
  // 初始化图表
  const line = echarts.init(document.getElementById('line'))
  const line1 = echarts.init(document.getElementById('line1'))
  
  // 加载订单数据
  request.get('/ordersData').then(res => {
    lineOption.xAxis.data = res.data.map(v => v.name)
    lineOption.series[0].data = res.data.map(v => v.value)
    line.setOption(lineOption)
  })

  // 加载游记数据
  request.get('/travelsData').then(res => {
    lineOption1.xAxis.data = res.data.map(v => v.name)
    lineOption1.series[0].data = res.data.map(v => v.value)
    line1.setOption(lineOption1)
  })

  // 响应式调整
  window.addEventListener('resize', () => {
    line.resize()
    line1.resize()
  })
})
</script>

<style scoped>
.dashboard {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.card-icon {
  width: 60px;
  height: 60px;
  margin-right: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-icon img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.card-content {
  flex: 1;
}

.card-title {
  color: #666;
  font-size: 16px;
  margin-bottom: 8px;
  font-weight: normal;
}

.card-value {
  color: #2c3e50;
  font-size: 24px;
  font-weight: bold;
}

.chart-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.chart {
  height: 450px;
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 1200px) {
  .chart-container {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stat-cards {
    grid-template-columns: 1fr;
  }

  .chart {
    height: 350px;
  }
}
</style>