<template>
  <div class="collect-page">
    <div class="main-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">
          <el-icon><Star /></el-icon>
          我的收藏
        </h1>
        <p class="page-subtitle">您收藏的所有精彩攻略</p>
      </div>

      <!-- 搜索和操作区域 -->
      <div class="action-section">
        <!-- 搜索区域 -->
        <div class="search-form">
          <el-input 
            v-model="data.title" 
            placeholder="请输入攻略标题查询"
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

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button type="danger" @click="delBatch">
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
        </div>
      </div>

      <!-- 收藏列表 -->
      <div class="collect-card">
        <el-table 
          :data="data.tableData" 
          stripe 
          @selection-change="handleSelectionChange"
          class="collect-table"
        >
          <el-table-column type="selection" width="55" />
          
          <el-table-column prop="title" label="攻略" min-width="300">
            <template #default="scope">
              <div class="article-title">
                <el-icon><Document /></el-icon>
                <a 
                  :href="'/front/articleDetail?id=' + scope.row.fid"
                  target="_blank" 
                  class="article-link"
                >
                  {{ scope.row.title }}
                </a>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="userName" label="收藏自" width="180">
            <template #default="scope">
              <div class="user-info">
                <el-icon><User /></el-icon>
                <span>{{ scope.row.userName }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="time" label="收藏时间" width="180">
            <template #default="scope">
              <div class="time-info">
                <el-icon><Timer /></el-icon>
                <span>{{ scope.row.time }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="100" fixed="right">
            <template #default="scope">
              <el-button 
                type="danger" 
                circle 
                @click="del(scope.row.id)"
                class="delete-btn"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
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
import { 
  Search, 
  Refresh, 
  Delete, 
  Star,
  Document,
  User,
  Timer 
} from '@element-plus/icons-vue'

const data = reactive({
  formVisible: false,
  form: {},
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  title: null,
  userName: null,
  ids: []
})

const load = () => {
  request.get('/collect/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      title: data.title,
      userName: data.userName
    }
  }).then(res => {
    if (res.code === '200') {
      data.tableData = res.data?.list || []
      data.total = res.data?.total
    }
  })
}
const handleAdd = () => {
  data.form = {}
  data.formVisible = true
}
const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
}
const add = () => {
  request.post('/collect/add', data.form).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      data.formVisible = false
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const update = () => {
  request.put('/collect/update', data.form).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      data.formVisible = false
      load()
    }
  })
}

const save = () => {
  data.form.id ? update() : add()
}

const del = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' }).then(res => {
    request.delete('/collect/delete/' + id).then(res => {
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
    request.delete("/collect/delete/batch", {data: data.ids}).then(res => {
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
  data.title = null
  data.userName = null
  load()
}

load()
</script>

<style scoped>
.collect-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 40px 0;
}

.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 页面标题 */
.page-header {
  margin-bottom: 32px;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title .el-icon {
  color: #f7ba2a;
}

.page-subtitle {
  color: #666;
  font-size: 14px;
  margin: 0;
}

/* 操作区域 */
.action-section {
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  gap: 20px;
  flex-wrap: wrap;
}

.search-form {
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-input {
  width: 300px;
}

.search-buttons,
.action-buttons {
  display: flex;
  gap: 12px;
}

/* 收藏列表卡片 */
.collect-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 表格样式 */
.collect-table {
  margin-bottom: 24px;
}

.collect-table :deep(.el-table__header) {
  background: #f8f9fa;
}

.collect-table :deep(.el-table__row) {
  transition: all 0.3s ease;
}

.collect-table :deep(.el-table__row:hover) {
  background: #f8f9fa;
}

/* 文章标题 */
.article-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.article-title .el-icon {
  color: #409eff;
}

.article-link {
  color: #409eff;
  text-decoration: none;
  transition: color 0.3s ease;
}

.article-link:hover {
  color: #337ecc;
  text-decoration: underline;
}

/* 用户信息 */
.user-info,
.time-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
}

.user-info .el-icon,
.time-info .el-icon {
  color: #409eff;
}

/* 删除按钮 */
.delete-btn {
  transition: all 0.3s ease;
}

.delete-btn:hover {
  transform: scale(1.1);
}

/* 分页器 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .collect-page {
    padding: 20px 0;
  }

  .page-title {
    font-size: 24px;
  }

  .action-section {
    flex-direction: column;
  }

  .search-form {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    width: 100%;
  }

  .collect-card {
    padding: 16px;
    border-radius: 12px;
  }
}
</style>