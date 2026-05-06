<template>
  <div class="password-container">
    <div class="password-card">
      <h2 class="page-title">修改密码</h2>
      <p class="page-subtitle">请设置一个安全的新密码</p>

      <el-form 
        ref="formRef" 
        :rules="data.rules" 
        :model="data.user" 
        label-width="100px" 
        class="password-form"
      >
        <el-form-item label="原密码" prop="password">
          <el-input 
            v-model="data.user.password" 
            placeholder="请输入原密码" 
            show-password
            class="custom-input"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="data.user.newPassword" 
            placeholder="请输入新密码" 
            show-password
            class="custom-input"
          >
            <template #prefix>
              <el-icon><Key /></el-icon>
            </template>
          </el-input>
          <div class="password-tips">
            <el-icon><InfoFilled /></el-icon>
            密码长度至少6位，建议包含字母和数字
          </div>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="data.user.confirmPassword" 
            placeholder="请确认新密码" 
            show-password
            class="custom-input"
          >
            <template #prefix>
              <el-icon><Check /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <div class="form-actions">
          <el-button 
            type="primary" 
            class="save-btn"
            @click="updatePassword"
          >
            <el-icon><Check /></el-icon>
            确认修改
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";
import router from "@/router/index.js";
import { Lock, Key, Check, InfoFilled } from '@element-plus/icons-vue'

const formRef = ref()

const validatePass = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请确认密码'))
  } else if (value !== data.user.newPassword) {
    callback(new Error("两次输入的密码不一致"))
  } else {
    callback()
  }
}

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  rules: {
    password: [
      { required: true, message: '请输入原密码', trigger: 'blur' },
      { min: 6, message: '密码长度至少6位', trigger: 'blur' }
    ],
    newPassword: [
      { required: true, message: '请输入新密码', trigger: 'blur' },
      { min: 6, message: '密码长度至少6位', trigger: 'blur' }
    ],
    confirmPassword: [
      { validator: validatePass, trigger: 'blur' }
    ]
  }
})

const updatePassword = () => {
  formRef.value.validate(valid => {
    if (valid) {
      request.put('/updatePassword', data.user).then(res => {
        if (res.code === '200') {
          ElMessage.success('密码修改成功，请重新登录')
          logout()
        } else {
          ElMessage.error(res.msg)
        }
      })
    }
  })
}

const logout = () => {
  localStorage.removeItem('xm-user')
  router.push('/login')
}
</script>

<style scoped>
.password-container {
  min-height: 100vh;
  padding: 40px 20px;
  background: #f5f7fa;
  display: flex;
  justify-content: center;
}

.password-card {
  width: 100%;
  max-width: 600px;
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
}

.page-title {
  text-align: center;
  font-size: 28px;
  color: #2c3e50;
  margin-bottom: 10px;
  font-weight: 600;
}

.page-subtitle {
  text-align: center;
  color: #666;
  font-size: 16px;
  margin-bottom: 40px;
}

.password-form {
  margin-top: 20px;
}

.custom-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  padding: 0 15px;
  height: 45px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);
}

.custom-input :deep(.el-input__prefix) {
  margin-right: 10px;
  color: #909399;
}

.password-tips {
  margin-top: 8px;
  font-size: 14px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 6px;
}

.form-actions {
  margin-top: 40px;
  display: flex;
  justify-content: center;
  gap: 20px;
}

.save-btn, .cancel-btn {
  width: 140px;
  height: 45px;
  font-size: 16px;
  border-radius: 8px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.save-btn {
  background: linear-gradient(135deg, #42b983 0%, #33a06f 100%);
  border: none;
}

.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}

.cancel-btn {
  border: 1px solid #dcdfe6;
}

.cancel-btn:hover {
  border-color: #42b983;
  color: #42b983;
}

.save-btn .el-icon {
  margin-right: 6px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .password-card {
    padding: 30px 20px;
  }

  .page-title {
    font-size: 24px;
  }

  .page-subtitle {
    font-size: 14px;
    margin-bottom: 30px;
  }

  .form-actions {
    flex-direction: column;
    gap: 10px;
  }

  .save-btn, .cancel-btn {
    width: 100%;
  }
}
</style>