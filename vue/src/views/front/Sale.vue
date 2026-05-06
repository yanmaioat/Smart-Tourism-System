<template>
  <div class="sale-page">
    <div class="main-container">
      <!-- 搜索区域 -->
      <div class="search-section">
        <div class="search-form">
          <el-input 
            v-model="data.orderNo" 
            placeholder="请输入订单编号查询"
            class="search-input"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <div class="search-buttons">
            <el-button type="primary" @click="load">
              <el-icon><Search /></el-icon>
              查询
            </el-button>
            <el-button @click="reset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </div>
        </div>
      </div>

      <!-- 订单列表 -->
      <div class="orders-card">
        <el-table 
          :data="data.tableData" 
          stripe 
          @selection-change="handleSelectionChange"
          class="orders-table"
        >
          <el-table-column 
            prop="orderNo" 
            label="订单编号" 
            min-width="150"
            show-overflow-tooltip
          >
            <template #default="scope">
              <span class="order-no">{{ scope.row.orderNo }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="name" label="火车名称" min-width="200" show-overflow-tooltip>
            <template #default="scope">
              <div class="product-info">
                <el-image 
                  :src="scope.row.trainImg" 
                  :preview-src-list="[scope.row.trainImg]" 
                  preview-teleported
                  class="product-image"
                />
                <a 
                  :href="'/front/trainDetail?id=' + scope.row.trainId"
                  target="_blank" 
                  class="product-name"
                >
                  {{ scope.row.name }}
                </a>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="trainPrice" label="票价" width="120">
            <template #default="scope">
              <span class="price">¥{{ scope.row.trainPrice }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="num" label="数量" width="100">
            <template #default="scope">
              <span class="quantity">x{{ scope.row.num }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="total" label="总价" width="120">
            <template #default="scope">
              <span class="total-price">¥{{ scope.row.total }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="trainTime" label="发车时间" width="180">
            <template #default="scope">
              <div class="time-info">
                <el-icon><Timer /></el-icon>
                <span>{{ scope.row.trainTime }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="status" label="状态" width="120">
            <template #default="scope">
              <el-tag 
                :type="getStatusType(scope.row.status)"
                class="status-tag"
              >
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="time" label="下单时间" width="180">
            <template #default="scope">
              <span class="time">{{ scope.row.time }}</span>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <div class="action-buttons">
                <el-button 
                  v-if="scope.row.status === '待支付'"
                  type="primary" 
                  @click="pay(scope.row)"
                  size="small"
                >
                  立即支付
                </el-button>
                <el-button 
                  v-if="scope.row.status === '待发货'"
                  type="danger" 
                  @click="refund(scope.row)"
                  size="small"
                >
                  申请退款
                </el-button>
                <el-button 
                  v-if="scope.row.status === '待支付'"
                  type="info" 
                  @click="changeStatus(scope.row)"
                  size="small"
                >
                  取消订单
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页器 -->
        <div class="pagination-wrapper" v-if="data.total">
          <el-pagination
            v-model:current-page="data.pageNum"
            :page-size="data.pageSize"
            :total="data.total"
            @current-change="load"
            background
            layout="total, prev, pager, next"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import request from "@/utils/request.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Refresh, Timer } from '@element-plus/icons-vue'

const data = reactive({
  formVisible: false,
  form: {},
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  orderNo: null,
  ids: []
})

// 获取状态对应的类型
const getStatusType = (status) => {
  const statusMap = {
    '已取消': 'info',
    '待支付': 'warning',
    '待发货': 'primary',
    '已完成': 'success',
    '已退款': 'danger'
  }
  return statusMap[status] || 'info'
}

const changeStatus = (row) => {
  ElMessageBox.confirm('取消订单后无法恢复，您确认取消订单吗？', '取消订单', { type: 'warning' }).then(res => {
    let form = JSON.parse(JSON.stringify(row))  // 防止表格数据被修改
    form.status = '已取消'
    request.put('/sale/update', form).then(res => {
      if (res.code === '200') {
        ElMessage.success('操作成功')
        data.formVisible = false
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {})
}

const refund = (row) => {
  ElMessageBox.confirm('退款后无法恢复，您确认退款吗？', '退款确认', { type: 'warning' }).then(res => {
    request.put('/alipay/refunds?orderNo=' + row.orderNo).then(res => {
      if (res.code === '200') {
        ElMessage.success('操作成功')
        data.formVisible = false
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {})
}

const pay = (row) => {
  let baseUrl = import.meta.env.VITE_BASE_URL
  window.open(baseUrl + '/alipay/pay?orderNo=' + row.orderNo)
}


const load = () => {
  request.get('/sale/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      orderNo: data.orderNo
    }
  }).then(res => {
    if (res.code === '200') {
      data.tableData = res.data?.list || []
      data.total = res.data?.total
    }
  })
}

const update = () => {
  request.put('/sale/update', data.form).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      data.formVisible = false
      load()
    }
  })
}

const del = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' }).then(res => {
    request.delete('/sale/delete/' + id).then(res => {
      if (res.code === '200') {
        ElMessage.success("删除成功")
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {
    console.error(err)
  })
}
const delBatch = () => {
  if (!data.ids.length) {
    ElMessage.warning("请选择数据")
    return
  }
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' }).then(res => {
    request.delete("/sale/delete/batch", {data: data.ids}).then(res => {
      if (res.code === '200') {
        ElMessage.success('操作成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {
    console.error(err)
  })
}
const handleSelectionChange = (rows) => {
  data.ids = rows.map(v => v.id)
}

const reset = () => {
  data.orderNo = null
  load()
}

load()
</script>

<style scoped>
.sale-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 40px 0;
}

.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 搜索区域 */
.search-section {
  margin-bottom: 24px;
}

.search-form {
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-input {
  max-width: 300px;
}

.search-buttons {
  display: flex;
  gap: 12px;
}

/* 订单列表卡片 */
.orders-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 表格样式 */
.orders-table {
  margin-bottom: 24px;
}

.orders-table :deep(.el-table__header) {
  background: #f8f9fa;
}

.orders-table :deep(.el-table__row) {
  transition: all 0.3s ease;
}

.orders-table :deep(.el-table__row:hover) {
  background: #f8f9fa;
}

/* 商品信息 */
.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-image {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
}

.product-name {
  color: #409eff;
  text-decoration: none;
  transition: color 0.3s ease;
}

.product-name:hover {
  color: #337ecc;
}

/* 价格样式 */
.price {
  color: #666;
}

.total-price {
  color: #ff6b6b;
  font-weight: 600;
}

.quantity {
  color: #666;
  font-size: 14px;
}

/* 订单编号 */
.order-no {
  font-family: monospace;
  color: #666;
  white-space: nowrap;
  display: inline-block;
}

/* 发车时间 */
.time-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
}

.time-info .el-icon {
  color: #409eff;
}

/* 状态标签 */
.status-tag {
  font-weight: 500;
}

/* 时间显示 */
.time {
  color: #666;
  font-size: 14px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
}

/* 分页器 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sale-page {
    padding: 20px 0;
  }

  .search-form {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    max-width: none;
  }

  .orders-card {
    padding: 16px;
    border-radius: 12px;
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style>