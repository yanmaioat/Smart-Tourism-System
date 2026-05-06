<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <img src="@/assets/imgs/logo.png" alt="Logo" class="login-logo">
        <h2 class="login-title">管理员登录</h2>
        <p class="login-subtitle">欢迎使用清远旅游网管理系统</p>
      </div>

      <el-form ref="formRef" :model="data.form" :rules="data.rules" class="login-form">
        <el-form-item prop="username">
          <el-input 
            :prefix-icon="User" 
            size="large" 
            v-model="data.form.username" 
            placeholder="请输入管理员账号"
            class="custom-input"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            show-password 
            :prefix-icon="Lock" 
            size="large" 
            v-model="data.form.password" 
            placeholder="请输入密码"
            class="custom-input"
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            size="large" 
            type="primary" 
            class="login-btn" 
            @click="login"
          >
            登 录
          </el-button>
        </el-form-item>

        <div class="login-options">
          <router-link to="/login" class="option-link">普通用户登录</router-link>
          <router-link to="/register" class="option-link">注册账号</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { User, Lock } from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";
import router from "@/router/index.js";

const data = reactive({
  form: { role: 'ADMIN' },
  rules: {
    username: [
      { required: true, message: '请输入账号', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' }
    ]
  }
})

const formRef = ref()

const login = () => {
  formRef.value.validate(valid => {
    if (valid) {
      request.post('/login', data.form).then(res => {
        if (res.code === '200') {
          ElMessage.success('登录成功')
          localStorage.setItem('xm-user', JSON.stringify(res.data))
          router.push('/manager/home')
        } else {
          ElMessage.error(res.msg)
        }
      }).catch(err => {
        console.error('登录失败:', err)
        ElMessage.error('登录失败，请稍后重试')
      })
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  padding: 20px;
}

.login-box {
  width: 400px;
  padding: 40px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-logo {
  width: 80px;
  height: 80px;
  margin-bottom: 20px;
}

.login-title {
  font-size: 28px;
  font-weight: bold;
  color: #2a5298;
  margin-bottom: 10px;
}

.login-subtitle {
  color: #666;
  font-size: 16px;
}

.login-form {
  margin-top: 20px;
}

.custom-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  height: 45px;
}

.login-btn {
  width: 100%;
  height: 45px;
  border-radius: 8px;
  font-size: 16px;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  border: none;
  transition: transform 0.2s;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(42, 82, 152, 0.3);
}

.login-options {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.option-link {
  color: #2a5298;
  text-decoration: none;
  transition: color 0.3s;
}

.option-link:hover {
  color: #1e3c72;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-box {
    width: 100%;
    padding: 30px 20px;
  }

  .login-title {
    font-size: 24px;
  }

  .login-subtitle {
    font-size: 14px;
  }
}
</style>