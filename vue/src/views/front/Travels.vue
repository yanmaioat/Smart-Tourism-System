<template>
  <div class="travels-page">
    <div class="main-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">
          <el-icon><Collection /></el-icon>
          我的游记
        </h1>
        <p class="page-subtitle">记录您的精彩旅程</p>
      </div>

      <!-- 搜索和操作区域 -->
      <div class="action-section">
        <!-- 搜索区域 -->
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

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button type="success" @click="goPage('/front/addTravel')">
            <el-icon><Edit /></el-icon>
            发布游记
          </el-button>
          <el-button type="danger" @click="delBatch">
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
        </div>
      </div>

      <!-- 游记列表 -->
      <div class="travels-card">
        <el-table 
          :data="data.tableData" 
          stripe 
          @selection-change="handleSelectionChange"
          class="travels-table"
        >
          <el-table-column type="selection" width="55" />
          
          <el-table-column prop="title" label="标题" min-width="200">
            <template #default="scope">
              <div class="travel-title">
                <el-icon><Document /></el-icon>
                <span>{{ scope.row.title }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="descr" label="简介" min-width="250" show-overflow-tooltip>
            <template #default="scope">
              <div class="travel-desc">{{ scope.row.descr }}</div>
            </template>
          </el-table-column>

          <el-table-column prop="cover" label="封面" width="120">
            <template #default="scope">
              <el-image 
                :src="scope.row.cover" 
                class="cover-image"
                :preview-src-list="[scope.row.cover]" 
                preview-teleported
              />
            </template>
          </el-table-column>

          <el-table-column prop="readCount" label="阅读量" width="100">
            <template #default="scope">
              <div class="read-count">
                <el-icon><View /></el-icon>
                <span>{{ scope.row.readCount }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="location" label="出行地点" width="150">
            <template #default="scope">
              <div class="location-info">
                <el-icon><Location /></el-icon>
                <span>{{ scope.row.location }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="days" label="出行时长" width="120">
            <template #default="scope">
              <div class="duration-info">
                <el-icon><Timer /></el-icon>
                <span>{{ scope.row.days }}天</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="money" label="费用" width="120">
            <template #default="scope">
              <span class="cost">¥{{ scope.row.money }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="status" label="审核状态" width="120">
            <template #default="scope">
              <el-tag 
                :type="getStatusType(scope.row.status)"
                class="status-tag"
              >
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <div class="row-actions">
                <el-button 
                  type="primary" 
                  circle 
                  @click="preview(scope.row.content)"
                  class="action-btn"
                >
                  <el-icon><View /></el-icon>
                </el-button>
                <el-button 
                  type="warning" 
                  circle 
                  @click="handleEdit(scope.row.id)"
                  class="action-btn"
                >
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button 
                  type="danger" 
                  circle 
                  @click="del(scope.row.id)"
                  class="action-btn"
                >
                  <el-icon><Delete /></el-icon>
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

    <!-- 内容预览弹窗 -->
    <el-dialog 
      title="游记内容预览" 
      v-model="data.formVisibleContent" 
      width="60%" 
      destroy-on-close
      class="preview-dialog"
    >
      <div class="preview-content">
        <div v-html="data.content"></div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.formVisibleContent = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>

import {reactive} from "vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {Delete, Edit} from "@element-plus/icons-vue";
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import {onBeforeUnmount, ref, shallowRef} from "vue";
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@/assets/css/wangeditor.css'

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user')),
  formVisible: false,
  form: {},
  tableData: [],
  pageNum: 1,
  pageSize: 4,
  total: 0,
  title: null,
  ids: [],
  content: null,
  formVisibleContent: false
})

const goPage = (path) => {
  location.href = path
}

const changeStatus = (row, status) => {
  row.status = status
  request.put('/travels/update', row).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      data.formVisible = false
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const preview = (content) => {
  data.content = content
  data.formVisibleContent = true
}

const baseUrl = import.meta.env.VITE_BASE_URL
const handleFileUpload = (res) => {
  data.form.cover = res.data
}

/* wangEditor5 初始化开始 */
const editorRef = shallowRef()  // 编辑器实例，必须用 shallowRef
const mode = 'default'
const editorConfig = { MENU_CONF: {} }
// 图片上传配置
editorConfig.MENU_CONF['uploadImage'] = {
  headers: {
    token: data.user.token,
  },
  server: baseUrl + '/files/wang/upload',  // 服务端图片上传接口
  fieldName: 'file'  // 服务端图片上传接口参数
}
// 组件销毁时，也及时销毁编辑器，否则可能会造成内存泄漏
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})
// 记录 editor 实例，重要！
const handleCreated = (editor) => {
  editorRef.value = editor
}
/* wangEditor5 初始化结束 */

const load = () => {
  request.get('/travels/selectPage', {
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
const handleEdit = (id) => {
  location.href = '/front/addTravel?id=' + id
}
const add = () => {
  request.post('/travels/add', data.form).then(res => {
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
  request.put('/travels/update', data.form).then(res => {
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
    request.delete('/travels/delete/' + id).then(res => {
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
    request.delete("/travels/delete/batch", {data: data.ids}).then(res => {
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

// 获取状态对应的类型
const getStatusType = (status) => {
  const statusMap = {
    '待审核': 'warning',
    '通过': 'success',
    '拒绝': 'danger'
  }
  return statusMap[status] || 'info'
}

load()
</script>

<style scoped>
.travels-page {
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

/* 游记列表卡片 */
.travels-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 表格样式 */
.travels-table {
  margin-bottom: 24px;
}

.travels-table :deep(.el-table__header) {
  background: #f8f9fa;
}

.travels-table :deep(.el-table__row) {
  transition: all 0.3s ease;
}

.travels-table :deep(.el-table__row:hover) {
  background: #f8f9fa;
}

/* 游记标题 */
.travel-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #2c3e50;
  font-weight: 500;
}

.travel-title .el-icon {
  color: #409eff;
}

/* 游记简介 */
.travel-desc {
  color: #666;
  line-height: 1.5;
}

/* 封面图片 */
.cover-image {
  width: 80px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.cover-image:hover {
  transform: scale(1.05);
}

/* 阅读量 */
.read-count {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
}

/* 地点信息 */
.location-info,
.duration-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
}

.location-info .el-icon,
.duration-info .el-icon {
  color: #409eff;
}

/* 费用 */
.cost {
  color: #ff6b6b;
  font-weight: 600;
}

/* 状态标签 */
.status-tag {
  font-weight: 500;
}

/* 操作按钮 */
.row-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: scale(1.1);
}

/* 预览弹窗 */
.preview-dialog {
  :deep(.el-dialog__body) {
    padding: 20px;
  }
}

.preview-content {
  max-height: 60vh;
  overflow-y: auto;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
}

/* 分页器 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .travels-page {
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

  .travels-card {
    padding: 16px;
    border-radius: 12px;
  }

  .row-actions {
    flex-wrap: wrap;
  }
}
</style>