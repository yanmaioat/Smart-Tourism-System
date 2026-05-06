<template>
  <div class="register-container">
    <div class="content-box">
      <!-- 左侧注册框 -->
      <div class="register-box">
        <div class="register-header">
          <div class="register-title">
            <i class="iconfont icon-travel"></i>
            开启旅程
          </div>
          <div class="register-subtitle">加入我们，探索更多精彩</div>
        </div>
        
        <el-form ref="formRef" :model="data.form" :rules="data.rules" class="register-form">
          <el-form-item prop="username">
            <el-input 
              :prefix-icon="User" 
              size="large" 
              v-model="data.form.username" 
              placeholder="请输入账号"
              class="custom-input"
            ></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input 
              show-password 
              :prefix-icon="Lock" 
              size="large" 
              v-model="data.form.password" 
              placeholder="请输入密码"
              class="custom-input"
            ></el-input>
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input 
              show-password 
              :prefix-icon="Lock" 
              size="large" 
              v-model="data.form.confirmPassword" 
              placeholder="请确认密码"
              class="custom-input"
            ></el-input>
          </el-form-item>
          <el-form-item prop="agreement">
            <el-checkbox v-model="data.form.agreement" class="agreement-checkbox">
              我已阅读并同意
              <el-button link type="primary" @click="showAgreement">《用户协议》</el-button>
              和
              <el-button link type="primary" @click="showPrivacy">《隐私政策》</el-button>
            </el-checkbox>
          </el-form-item>
          <el-form-item>
            <el-button 
              size="large" 
              type="primary" 
              class="register-btn" 
              @click="register"
            >立即注册</el-button>
          </el-form-item>
        </el-form>

        <div class="register-options">
          已有账号？
          <router-link to="/login" class="login-link">立即登录</router-link>
        </div>
      </div>
      
      <!-- 右侧宣传区 -->
      <div class="promo-box">
        <div class="promo-content">
          <h2 class="promo-title">加入我们的旅行社区</h2>
          <div class="feature-list">
            <div class="feature-item">
              <i class="el-icon-user"></i>
              <div class="feature-text">
                <h3>个性化服务</h3>
                <p>定制专属旅行计划</p>
              </div>
            </div>
            <div class="feature-item">
              <i class="el-icon-collection"></i>
              <div class="feature-text">
                <h3>旅行日记</h3>
                <p>记录精彩旅行时刻</p>
              </div>
            </div>
            <div class="feature-item">
              <i class="el-icon-chat-dot-round"></i>
              <div class="feature-text">
                <h3>结识同行者</h3>
                <p>分享旅行经验与故事</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, nextTick } from "vue";
import { User, Lock } from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import { ElMessage, ElMessageBox } from "element-plus";
import router from "@/router/index.js";

const validatePass = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请确认密码'))
  } else if (value !== data.form.password) {
    callback(new Error("确认密码跟原密码不一致!"))
  } else {
    callback()
  }
}

const data = reactive({
  form: { 
    role: 'USER',
    agreement: false 
  },
  rules: {
    username: [
      { required: true, message: '请输入账号', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' }
    ],
    confirmPassword: [
      { validator: validatePass, trigger: 'blur' }
    ],
    agreement: [
      { 
        validator: (rule, value, callback) => {
          if (!value) {
            callback(new Error('请阅读并同意用户协议和隐私政策'))
          } else {
            callback()
          }
        },
        trigger: 'change'
      }
    ]
  }
})

const formRef = ref()

const register = async () => {
  try {
    const valid = await formRef.value.validate()                                //调用表单的验证方法，返回 true 表示验证通过
    if (valid) {                                                                //确保用户勾选了 agreement，否则提示错误
      if (!data.form.agreement) {
        ElMessage.warning('请先同意用户协议和隐私政策')
        return
      }//协议校验
      const res = await request.post('/register', data.form)               //发送用户数据到后端
      console.log('注册响应:', res)
      if (res.code === '200') {                                                //后端返回200表示业务逻辑成功
        ElMessage.success('注册成功')
        console.log('准备跳转到登录页')
        await nextTick()                                                       // 等待当前更新周期完成，确保 DOM 更新完成。
        await new Promise(resolve => setTimeout(resolve, 100)) // 添加一个小延时
        await router.push({                                                    //跳转到登录页面，并传递 username 和 registered 参数用于登录页提示
          path: '/login',
          query: { 
            username: data.form.username,
            registered: 'true'
          }
        })
        console.log('跳转完成')
      } else {
        console.log('注册失败:', res.msg)
        ElMessage.error(res.msg)
      }
    }
  } catch (error) {
    console.error('注册失败:', error)
    ElMessage.error('注册失败，请查看提示')
  }
}

// 显示用户协议和隐私政策的函数可以从 Login.vue 复制过来
const showAgreement = () => {
  ElMessageBox.alert(
    // ... 从 Login.vue 复制用户协议内容
  )
}

const showPrivacy = () => {
  ElMessageBox.alert(
    // ... 从 Login.vue 复制隐私政策内容
  )
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: linear-gradient(rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.6)), url("@/assets/imgs/bg.jpg");
  background-size: cover;
  background-position: center;
  padding: 20px;
}

.content-box {
  display: flex;
  width: 1000px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.register-box {
  width: 400px;
  padding: 40px;
  background: white;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 500px;
}

.register-header {
  margin-bottom: 20px;
}

.register-title {
  font-size: 28px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 10px;
  color: #2c3e50;
}

.register-subtitle {
  text-align: center;
  color: #666;
  margin-bottom: 20px;
  font-size: 16px;
}

.register-form {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  margin: 20px 0;
}

.custom-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  height: 45px;
}

.register-btn {
  width: 100%;
  height: 45px;
  border-radius: 8px;
  font-size: 16px;
  background: linear-gradient(135deg, #42b983 0%, #33a06f 100%);
  border: none;
  transition: transform 0.2s;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}

.register-options {
  text-align: center;
  color: #666;
  font-size: 14px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.login-link {
  color: #42b983;
  text-decoration: none;
  font-weight: 500;
  margin-left: 5px;
}

.login-link:hover {
  text-decoration: underline;
}

/* 右侧宣传区样式 */
.promo-box {
  flex: 1;
  background: linear-gradient(135deg, #42b983 0%, #33a06f 100%);
  padding: 40px;
  color: white;
  display: flex;
  align-items: center;
}

.promo-content {
  width: 100%;
}

.promo-title {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 40px;
  text-align: center;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.feature-list {
  margin-bottom: 40px;
}

.feature-item {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(5px);
  transition: transform 0.3s;
}

.feature-item:hover {
  transform: translateX(10px);
}

.feature-item i {
  font-size: 24px;
  margin-right: 15px;
}

.feature-text h3 {
  font-size: 18px;
  margin-bottom: 5px;
}

.feature-text p {
  font-size: 14px;
  opacity: 0.9;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content-box {
    flex-direction: column;
    width: 100%;
    max-width: 400px;
  }
  
  .register-box {
    width: 100%;
  }
  
  .promo-box {
    padding: 30px;
  }
  
  .feature-item {
    margin-bottom: 20px;
  }
}

/* 协议相关样式 */
.agreement-checkbox {
  font-size: 14px;
  color: #666;
}

:deep(.el-checkbox__label) {
  color: #666;
}

:deep(.el-button--link) {
  padding: 0 3px;
  font-size: 14px;
  font-weight: 500;
}

/* 从 Login.vue 复制协议弹窗样式 */
:deep(.agreement-dialog),
:deep(.privacy-dialog) {
  /* ... 复制相关样式 */
}
</style>