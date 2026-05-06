<template>
  <div class="tourism-detail">
    <!-- 主要内容区域 -->
    <div class="main-container">
      <!-- 返回按钮 -->
      <div class="back-button" @click="router.back()">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回列表</span>
      </div>

      <!-- 产品详情卡片 -->
      <div class="detail-card">
        <!-- 左侧图片区域 -->
        <div class="image-section">
          <img :src="data.tourism.img" :alt="data.tourism.name">
          <div class="image-overlay">
            <div class="tags-wrapper">
              <el-tag 
                v-for="item in JSON.parse(data.tourism.specials || '[]')"
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
          <h1 class="product-title">{{ data.tourism.name }}</h1>
          <p class="product-desc">{{ data.tourism.descr }}</p>

          <!-- AI助手按钮 -->
          <div class="ai-helper" @click="router.push('/front/aiChat')">
            <el-icon><ChatDotRound /></el-icon>
            <span>问问AI助手</span>
          </div>

          <!-- 价格和库存信息 -->
          <div class="price-section">
            <div class="price-info">
              <span class="currency">¥</span>
              <span class="amount">{{ data.tourism.price }}</span>
              <span class="stock">库存: {{ data.tourism.store }}</span>
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

      <!-- 图文详情 -->
      <div class="content-section">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon><PictureFilled /></el-icon>
            图文介绍
          </h2>
        </div>
        <div class="rich-content" v-html="data.tourism.content"></div>
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
          <label>商品名称</label>
          <span>{{ data.tourism.name }}</span>
        </div>
        <div class="form-item">
          <label>商品单价</label>
          <span class="price">¥{{ data.tourism.price }}</span>
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
import { ArrowLeft, InfoFilled, PictureFilled, ChatDotRound } from '@element-plus/icons-vue'

const router = useRouter()

const data = reactive({
  id: router.currentRoute.value.query.id,
  tourism: {},
  formVisible: false,
  form: {}  // 表示订单的对象
})
//当用户调整购买数量时，重新计算商品总价。
const changeNum = () => {
  data.form.total = data.tourism.price * data.form.num
}
//提交订单数据，并跳转到订单列表页面。
const saveOrder = () => {
  request.post('/orders/add', data.form).then(res => {
    if (res.code === '200') {          //后端返回200表示业务逻辑成功
      ElMessage.success('下单成功')
      router.push('/front/orders')   // 跳转到订单列表页
    } else {
      ElMessage.error(res.msg)
    }
  })
}
//打开订单确认弹窗，并初始化订单数据。
const addOrder = () => {
  data.formVisible = true
  data.form.tourismId = data.tourism.id       // 填充产品 ID
  data.form.tourismImg = data.tourism.img     // 填充产品封面图
  data.form.name = data.tourism.name          // 填充产品名称
  data.form.tourismPrice = data.tourism.price // 填充单价
  data.form.num = 1                           // 默认数量为 1
  data.form.total = data.tourism.price        // 初始总价
}
//根据产品 ID 从后端接口获取产品的详细信息。
const load = () => {
  request.get('/tourism/selectById/' + data.id).then(res => {
    if (res.code === '200') {
      data.tourism = res.data                 // 更新产品详情
    }
  })
}
load()
</script>

<style scoped>
.tourism-detail {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 40px 0;
}

/* 页面头部 */
.page-header {
  margin-bottom: 20px;
}

/* 主要内容区域 */
.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 详情卡片 */
.detail-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  margin-bottom: 30px;
}

/* 图片区域 */
.image-section {
  position: relative;
  height: 100%;
  min-height: 400px;
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

.product-title {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 16px;
}

.product-desc {
  color: #666;
  line-height: 1.6;
  margin-bottom: 30px;
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

/* 图文详情区域 */
.content-section {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 8px;
}

.rich-content {
  color: #2c3e50;
  line-height: 1.8;
}

.rich-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 10px 0;
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
    min-height: auto;
  }

  .info-section {
    padding: 30px;
  }
}

@media (max-width: 768px) {
  .tourism-detail {
    padding: 20px 0;
  }

  .product-title {
    font-size: 24px;
  }

  .price-section {
    flex-direction: column;
    gap: 20px;
  }

  .buy-btn {
    width: 100%;
  }

  .content-section {
    padding: 20px;
  }
}

/* AI助手按钮样式 */
.ai-helper {
  margin: 20px 0;
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

/* 移除之前的固定定位样式 */
.nav-buttons {
  display: none;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .ai-helper {
    margin: 15px 0;
  }
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
  color: #2c3e50;
  font-size: 14px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  margin-bottom: 20px;
}

.back-button:hover {
  transform: translateX(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.back-button .el-icon {
  font-size: 16px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .back-button {
    padding: 8px 16px;
    margin-bottom: 15px;
  }
  
  /* ... 其他响应式样式 ... */
}
</style>