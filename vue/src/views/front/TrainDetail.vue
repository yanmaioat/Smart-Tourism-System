<template>
  <div class="train-detail">
    <!-- 主要内容区域 -->
    <div class="main-container">
      <!-- 返回按钮 -->
      <div class="page-header">
        <div class="back-button" @click="router.back()">
          <el-icon><ArrowLeft /></el-icon>
          <span>返回列表</span>
        </div>
      </div>

      <div class="detail-card">
        <!-- 左侧图片区域 -->
        <div class="image-section">
          <img :src="data.train.img" :alt="data.train.name">
          <div class="image-overlay">
            <div class="tags-wrapper">
              <el-tag 
                v-for="item in JSON.parse(data.train.specials || '[]')"
                :key="item"
                effect="light"
                class="feature-tag"
              >
                {{ item }}
              </el-tag>
            </div>
          </div>
        </div>

        <!-- 右侧信息区域 -->
        <div class="info-section">
          <h1 class="train-title">{{ data.train.name }}</h1>
          <p class="train-desc">{{ data.train.descr }}</p>

          <!-- 车次信息 -->
          <div class="info-grid">
            <div class="info-item">
              <el-icon><Timer /></el-icon>
              <div class="info-content">
                <label>发车时间</label>
                <span>{{ data.train.departureTime }}</span>
              </div>
            </div>
            <div class="info-item">
              <el-icon><Clock /></el-icon>
              <div class="info-content">
                <label>停车时间</label>
                <span>{{ data.train.parkingTime }}</span>
              </div>
            </div>
            <div class="info-item">
              <el-icon><Location /></el-icon>
              <div class="info-content">
                <label>车站</label>
                <span>{{ data.train.station }}</span>
              </div>
            </div>
            <div class="info-item">
              <el-icon><Position /></el-icon>
              <div class="info-content">
                <label>起始站</label>
                <span>{{ data.train.startingStation }}</span>
              </div>
            </div>
            <div class="info-item">
              <el-icon><Place /></el-icon>
              <div class="info-content">
                <label>终点站</label>
                <span>{{ data.train.terminatingStation }}</span>
              </div>
            </div>
            <div class="info-item">
              <el-icon><Calendar /></el-icon>
              <div class="info-content">
                <label>日期</label>
                <span>{{ data.train.date }}</span>
              </div>
            </div>
          </div>

          <!-- 价格和库存信息 -->
          <div class="price-section">
            <div class="price-info">
              <span class="currency">¥</span>
              <span class="amount">{{ data.train.price }}</span>
              <span class="stock">库存: {{ data.train.store }}</span>
            </div>
            <el-button 
              type="primary" 
              class="buy-btn"
              @click="addOrder"
            >
              立即购买
            </el-button>
          </div>

          <!-- 预订须知 -->
          <div class="notice-section">
            <el-icon><InfoFilled /></el-icon>
            <p>预订须知：支付完成后商家最晚会在2个工作小时内（工作日9:00-18:00）确认是否预定成功</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 订单确认弹窗 -->
    <el-dialog 
      title="确认订单" 
      v-model="data.formVisible" 
      width="500px"
      destroy-on-close
      class="order-dialog"
    >
      <div class="order-form">
        <div class="form-item">
          <label>车票名称</label>
          <span>{{ data.train.name }}</span>
        </div>
        <div class="form-item">
          <label>车票单价</label>
          <span class="price">¥{{ data.train.price }}</span>
        </div>
        <div class="form-item">
          <label>购买数量</label>
          <el-input-number 
            v-model="data.form.num" 
            :min="1" 
            :max="10"
            @change="changeNum"
          />
        </div>
        <div class="form-item total">
          <label>商品总价</label>
          <span class="total-price">¥{{ data.form.total }}</span>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.formVisible = false">取 消</el-button>
          <el-button type="primary" @click="saveOrder">确认下单</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import { useRouter } from "vue-router";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";
import { 
  ArrowLeft, Timer, Clock, Location, Position, Place, 
  Calendar, InfoFilled 
} from '@element-plus/icons-vue'

const router = useRouter()
const data = reactive({
  id: router.currentRoute.value.query.id,
  train: {},
  formVisible: false,
  form: {}
})

const changeNum = () => {
  data.form.total = data.train.price * data.form.num
}

const saveOrder = () => {
  request.post('/sale/add', data.form).then(res => {
    if (res.code === '200') {
      ElMessage.success('下单成功');
      router.push('/front/sale');
    } else {
      ElMessage.error(res.msg);
    }
  })
}

const addOrder = () => {
  data.formVisible = true;
  data.form.trainId = data.train.id;
  data.form.departureTime = data.train.departureTime;
  data.form.parkingTime = data.train.parkingTime;
  data.form.station = data.train.station;
  data.form.startingStation = data.train.startingStation;
  data.form.terminatingStation = data.train.terminatingStation;
  data.form.trainImg = data.train.img;
  data.form.name = data.train.name;
  data.form.trainPrice = data.train.price;
  data.form.num = 1;
  data.form.total = data.train.price;
}

const load = () => {
  request.get('/train/selectById/' + data.id).then(res => {
    if (res.code === '200') {
      data.train = res.data;
    }
  })
}

load();
</script>

<style scoped>
.train-detail {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 40px 0;
}

/* 页面头部 */
.page-header {
  margin-bottom: 20px;
}

/* 返回按钮 */
.back-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #2c3e50;
  font-size: 14px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.back-button:hover {
  transform: translateX(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

/* 主要内容区域 */
.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.detail-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
}

/* 图片区域 */
.image-section {
  position: relative;
  height: 100%;
}

.image-section img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  padding: 20px;
  background: linear-gradient(to bottom, rgba(0,0,0,0.4), transparent);
}

.tags-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.feature-tag {
  font-size: 12px;
  padding: 4px 12px;
}

/* 信息区域 */
.info-section {
  padding: 40px;
}

.train-title {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 16px;
}

.train-desc {
  color: #666;
  line-height: 1.6;
  margin-bottom: 30px;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-bottom: 40px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.info-item .el-icon {
  color: #409eff;
  font-size: 20px;
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-content label {
  color: #666;
  font-size: 14px;
}

.info-content span {
  color: #2c3e50;
  font-weight: 500;
}

/* 价格区域 */
.price-section {
  background: #f8f9fa;
  padding: 24px;
  border-radius: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.price-info {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.currency {
  color: #ff6b6b;
  font-size: 18px;
}

.amount {
  color: #ff6b6b;
  font-size: 36px;
  font-weight: 700;
}

.stock {
  color: #666;
  font-size: 14px;
  margin-left: 16px;
}

.buy-btn {
  padding: 12px 36px;
  font-size: 16px;
}

/* 预订须知 */
.notice-section {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: #fff8e1;
  border-radius: 8px;
}

.notice-section .el-icon {
  color: #ffa000;
  font-size: 20px;
}

.notice-section p {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
}

/* 订单弹窗 */
.order-form {
  padding: 20px 0;
}

.form-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.form-item label {
  color: #666;
  font-size: 15px;
}

.form-item.total {
  border-top: 1px solid #eee;
  padding-top: 20px;
  margin-top: 30px;
}

.total-price {
  color: #ff6b6b;
  font-size: 24px;
  font-weight: 700;
}

/* 响应式设计 */
@media (max-width: 992px) {
  .detail-card {
    grid-template-columns: 1fr;
  }

  .image-section {
    height: 300px;
  }

  .info-section {
    padding: 30px;
  }
}

@media (max-width: 768px) {
  .train-detail {
    padding: 20px 0;
  }

  .back-button {
    top: 10px;
    left: 10px;
    padding: 8px 16px;
  }

  .info-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .price-section {
    flex-direction: column;
    gap: 20px;
  }

  .buy-btn {
    width: 100%;
  }
}
</style>
