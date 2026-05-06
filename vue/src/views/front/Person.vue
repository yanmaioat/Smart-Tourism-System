<template>
  <div class="person-container">
    <div class="profile-card">
      <h2 class="page-title">个人资料</h2>
      
      <el-form ref="user" :model="data.user" label-width="80px" class="profile-form">
        <!-- 头像上传区域 -->
        <div class="avatar-section">
          <el-upload
            :action="baseUrl + '/files/upload'"
            :on-success="handleFileUpload"
            :show-file-list="false"
            class="avatar-uploader"
          >
            <div class="avatar-wrapper">
              <img v-if="data.user.avatar" :src="data.user.avatar" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              <div class="avatar-hover">
                <el-icon><Camera /></el-icon>
                <span>更换头像</span>
              </div>
            </div>
          </el-upload>
        </div>

        <!-- 个人信息表单 -->
        <el-form-item prop="username" label="用户名">
          <el-input 
            disabled 
            v-model="data.user.username" 
            placeholder="请输入用户名"
            class="custom-input"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="name" label="姓名">
          <el-input 
            v-model="data.user.name" 
            placeholder="请输入姓名"
            class="custom-input"
          >
            <template #prefix>
              <el-icon><UserFilled /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="phone" label="电话">
          <el-input 
            v-model="data.user.phone" 
            placeholder="请输入电话"
            class="custom-input"
          >
            <template #prefix>
              <el-icon><Phone /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="email" label="邮箱">
          <el-input 
            v-model="data.user.email" 
            placeholder="请输入邮箱"
            class="custom-input"
          >
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <div class="form-actions">
          <el-button 
            type="primary" 
            @click="update"
            class="save-btn"
          >
            <el-icon><Check /></el-icon>
            保存修改
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";
import { User, UserFilled, Phone, Message, Camera, Check, Plus } from '@element-plus/icons-vue'

const baseUrl = import.meta.env.VITE_BASE_URL

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}')
})

const handleFileUpload = (res) => {
  data.user.avatar = res.data
  ElMessage.success('头像上传成功')
}

const emit = defineEmits(['updateUser'])
const update = () => {
  request.put('/user/update', data.user).then(res => {
    if (res.code === '200') {
      ElMessage.success('个人信息保存成功')
      localStorage.setItem('xm-user', JSON.stringify(data.user))
      emit('updateUser')
    } else {
      ElMessage.error(res.msg)
    }
  })
}
</script>

<style scoped>
.person-container {
  min-height: 100vh;
  padding: 40px 20px;
  background: #f5f7fa;
  display: flex;
  justify-content: center;
}

.profile-card {
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
  margin-bottom: 40px;
  font-weight: 600;
}

.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 40px;
}

.avatar-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.avatar-wrapper:hover .avatar-hover {
  opacity: 1;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-hover {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.avatar-hover .el-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.avatar-uploader :deep(.el-upload) {
  border: 2px dashed #e4e7ed;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: #42b983;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}

.profile-form {
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

.form-actions {
  margin-top: 40px;
  text-align: center;
}

.save-btn {
  width: 180px;
  height: 45px;
  font-size: 16px;
  border-radius: 8px;
  background: linear-gradient(135deg, #42b983 0%, #33a06f 100%);
  border: none;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}

.save-btn .el-icon {
  margin-right: 6px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-card {
    padding: 30px 20px;
  }

  .page-title {
    font-size: 24px;
    margin-bottom: 30px;
  }
}
</style>