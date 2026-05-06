<template>
  <div class="feedback-page">
    <!-- 主要内容区域 -->
    <div class="main-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">
          <span class="emoji">💡</span>
          期待您的反馈
        </h1>
        <p class="page-subtitle">您的建议是我们进步的动力</p>
      </div>

      <!-- 反馈表单 -->
      <div class="feedback-card">
        <el-form 
          ref="formRef" 
          :model="data.form" 
          :rules="data.rules"
          label-position="top"
          class="feedback-form"
        >
          <el-form-item prop="title" label="反馈标题">
            <el-input 
              v-model="data.form.title" 
              placeholder="请用简短的语言描述您的问题或建议"
              :maxlength="50"
              show-word-limit
            >
              <template #prefix>
                <el-icon><Edit /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="content" label="反馈内容">
            <el-input 
              type="textarea" 
              v-model="data.form.content" 
              placeholder="请详细描述您遇到的问题或建议，这将帮助我们更好地改进服务"
              :rows="6"
              :maxlength="500"
              show-word-limit
              resize="none"
            />
          </el-form-item>

          <!-- 提交按钮 -->
          <div class="form-footer">
            <el-button 
              type="primary" 
              class="submit-btn"
              @click="add"
              :loading="data.submitting"
            >
              <template #icon>
                <el-icon><Check /></el-icon>
              </template>
              提交反馈
            </el-button>
          </div>
        </el-form>
      </div>

      <!-- 提示信息 -->
      <div class="tips-section">
        <div class="tip-card">
          <el-icon class="tip-icon"><Timer /></el-icon>
          <div class="tip-content">
            <h3>及时响应</h3>
            <p>我们会在24小时内处理您的反馈</p>
          </div>
        </div>
        <div class="tip-card">
          <el-icon class="tip-icon"><Lock /></el-icon>
          <div class="tip-content">
            <h3>隐私保护</h3>
            <p>您的反馈信息将被严格保密</p>
          </div>
        </div>
        <div class="tip-card">
          <el-icon class="tip-icon"><Star /></el-icon>
          <div class="tip-content">
            <h3>持续改进</h3>
            <p>您的建议将帮助我们提供更好的服务</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";
import { Edit, Check, Timer, Lock, Star } from '@element-plus/icons-vue'

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  form: {},
  submitting: false,
  rules: {
    title: [
      { required: true, message: '请输入反馈标题', trigger: 'blur' },
      { min: 2, max: 50, message: '标题长度应在 2 到 50 个字符之间', trigger: 'blur' }
    ],
    content: [
      { required: true, message: '请输入反馈内容', trigger: 'blur' },
      { min: 10, max: 500, message: '内容长度应在 10 到 500 个字符之间', trigger: 'blur' }
    ]
  }
})

const formRef = ref()

const add = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      data.submitting = true
      request.post('/feedback/add', data.form).then(res => {
        if (res.code === '200') {
          ElMessage.success('感谢您的反馈，我们会认真处理')
          data.form = {}
          formRef.value.resetFields()
        } else {
          ElMessage.error(res.msg)
        }
      }).finally(() => {
        data.submitting = false
      })
    }
  })
}
</script>

<style scoped>
.feedback-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 40px 0;
}

.main-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.emoji {
  font-size: 40px;
}

.page-subtitle {
  color: #666;
  font-size: 16px;
}

/* 反馈表单卡片 */
.feedback-card {
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  margin-bottom: 40px;
}

.feedback-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #2c3e50;
  font-size: 15px;
  padding-bottom: 8px;
}

.feedback-form :deep(.el-input__wrapper),
.feedback-form :deep(.el-textarea__inner) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.feedback-form :deep(.el-input__wrapper:hover),
.feedback-form :deep(.el-textarea__inner:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.form-footer {
  text-align: center;
  margin-top: 40px;
}

.submit-btn {
  padding: 12px 36px;
  font-size: 16px;
}

/* 提示信息区域 */
.tips-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 40px;
}

.tip-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: flex-start;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease;
}

.tip-card:hover {
  transform: translateY(-5px);
}

.tip-icon {
  font-size: 24px;
  color: #409eff;
  background: #ecf5ff;
  padding: 12px;
  border-radius: 12px;
}

.tip-content h3 {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.tip-content p {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .feedback-page {
    padding: 20px 0;
  }

  .page-title {
    font-size: 28px;
  }

  .emoji {
    font-size: 32px;
  }

  .feedback-card {
    padding: 20px;
  }

  .tips-section {
    grid-template-columns: 1fr;
  }

  .tip-card {
    padding: 20px;
  }
}
</style>