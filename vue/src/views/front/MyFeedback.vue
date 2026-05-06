<template>
  <div class="feedback-page">
    <div class="main-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">
          <el-icon><ChatLineSquare /></el-icon>
          我的反馈
        </h1>
        <p class="page-subtitle">查看您提交的所有反馈记录</p>
      </div>

      <!-- 搜索区域 -->
      <div class="search-section">
        <div class="search-form">
          <el-input 
            v-model="data.title" 
            placeholder="请输入标题查询"
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

      <!-- 反馈列表 -->
      <div class="feedback-card">
        <el-table 
          :data="data.tableData" 
          stripe 
          class="feedback-table"
        >
          <el-table-column prop="title" label="标题" min-width="200">
            <template #default="scope">
              <div class="feedback-title">
                <el-icon><Document /></el-icon>
                <span>{{ scope.row.title }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip>
            <template #default="scope">
              <div class="feedback-content">{{ scope.row.content }}</div>
            </template>
          </el-table-column>

          <el-table-column prop="reply" label="回复" min-width="250">
            <template #default="scope">
              <div class="reply-content" v-if="scope.row.reply">
                <el-icon><ChatDotRound /></el-icon>
                <span>{{ scope.row.reply }}</span>
              </div>
              <el-tag size="small" type="info" v-else>暂无回复</el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="time" label="提交时间" width="180">
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
import { ref, reactive } from "vue";
import request from "@/utils/request.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { 
  Search, 
  Refresh, 
  Delete, 
  Document, 
  ChatLineSquare, 
  ChatDotRound,
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
  ids: [],
  rules: {
    reply: [
      { required: true, message: '请输入回复', trigger: 'blur' }
    ]
  }
})

const formRef = ref()

const load = () => {
  request.get('/feedback/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      title: data.title
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
  request.post('/feedback/add', data.form).then(res => {
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
  request.put('/feedback/update', data.form).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      data.formVisible = false
      load()
    }
  })
}

const save = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      data.form.id ? update() : add()
    }
  })
}

const del = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' }).then(res => {
    request.delete('/feedback/delete/' + id).then(res => {
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
    request.delete("/feedback/delete/batch", {data: data.ids}).then(res => {
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
  load()
}

load()
</script>

<style scoped>
.feedback-page {
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

.page-subtitle {
  color: #666;
  font-size: 14px;
  margin: 0;
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

/* 反馈列表卡片 */
.feedback-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 表格样式 */
.feedback-table {
  margin-bottom: 24px;
}

.feedback-table :deep(.el-table__header) {
  background: #f8f9fa;
}

.feedback-table :deep(.el-table__row) {
  transition: all 0.3s ease;
}

.feedback-table :deep(.el-table__row:hover) {
  background: #f8f9fa;
}

/* 反馈标题 */
.feedback-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #2c3e50;
  font-weight: 500;
}

.feedback-title .el-icon {
  color: #409eff;
}

/* 反馈内容 */
.feedback-content {
  color: #666;
  line-height: 1.5;
}

/* 回复内容 */
.reply-content {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  color: #67c23a;
  font-size: 14px;
}

.reply-content .el-icon {
  margin-top: 2px;
}

/* 时间信息 */
.time-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  font-size: 14px;
}

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
  .feedback-page {
    padding: 20px 0;
  }

  .page-title {
    font-size: 24px;
  }

  .search-form {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    max-width: none;
  }

  .feedback-card {
    padding: 16px;
    border-radius: 12px;
  }
}
</style>