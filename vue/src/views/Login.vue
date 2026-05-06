<template>
  <div class="login-container">
    <div class="content-box">
      <!-- 左侧登录框 -->
      <div class="login-box">
        <div class="login-header">
          <div class="login-title">
            <i class="iconfont icon-travel"></i>
            探索世界
          </div>
          <div class="login-subtitle">开启您的旅行之旅</div>
        </div>
        
        <el-form ref="formRef" :model="data.form" :rules="data.rules" class="login-form">
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
          <el-form-item prop="verify" class="verify-item">
            <div class="slider-container" :style="sliderStyle">
              <el-slider
                v-model="sliderValue"
                :show-tooltip="false"
                :min="0"
                :max="100"
                @change="handleSliderChange"
              >
                <template #button>
                  <div class="slider-button">
                    <i class="el-icon-right"></i>
                  </div>
                </template>
              </el-slider>
              <div class="slider-text" :class="{ 'verified': isVerified }">
                {{ isVerified ? '验证通过' : '向右滑动验证' }}
              </div>
            </div>
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
              class="login-btn" 
              @click="login"
            >立即登录</el-button>
          </el-form-item>
        </el-form>

        <div class="login-options">
          <div class="left-options">
            <router-link to="/front/home" class="option-link">游客访问</router-link>
            <router-link to="/adminLogin" class="option-link">管理员登录</router-link>
          </div>
          <div class="right-options">
            还没有账号？
            <router-link to="/register" class="register-link">立即注册</router-link>
          </div>
        </div>
      </div>
      
      <!-- 右侧宣传区 -->
      <div class="promo-box">
        <div class="promo-content">
          <h2 class="promo-title">发现精彩旅程</h2>
          <div class="feature-list">
            <div class="feature-item">
              <i class="el-icon-location"></i>
              <div class="feature-text">
                <h3>探索全球目的地</h3>
                <p>超过1000+精选旅游景点</p>
              </div>
            </div>
            <div class="feature-item">
              <i class="el-icon-star-on"></i>
              <div class="feature-text">
                <h3>专业旅行建议</h3>
                <p>来自全球旅行达人的深度攻略</p>
              </div>
            </div>
            <div class="feature-item">
              <i class="el-icon-shopping-cart-full"></i>
              <div class="feature-text">
                <h3>优质旅行产品</h3>
                <p>严选品质酒店与精品路线</p>
              </div>
            </div>
          </div>
          <div class="promo-footer">
            <div class="stats">
              <div class="stat-item">
                <span class="stat-number">1000+</span>
                <span class="stat-label">旅游目的地</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">50万+</span>
                <span class="stat-label">用户信赖</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">99%</span>
                <span class="stat-label">好评率</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from "vue";
import { User, Lock } from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import { ElMessage, ElMessageBox } from "element-plus";
import router from "@/router/index.js";
import { useRoute } from 'vue-router'

const data = reactive({
  form: { 
    role: 'USER',
    agreement: false,
    verified: false,
    verify: ''
  },
  rules: {
    username: [
      { required: true, message: '请输入账号', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' }
    ],
    verify: [
      { 
        validator: (rule, value, callback) => {
          if (!data.form.verified) {
            callback(new Error('请完成滑块验证'))
          } else {
            callback()
          }
        },
        trigger: 'change'
      }
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

const formRef = ref(null)

// 滑块相关
const sliderValue = ref(0)
const isVerified = ref(false)

const sliderStyle = computed(() => {
  return {
    width: '320px',
    height: '40px',
    backgroundColor: isVerified.value ? '#42b983' : '#f5f5f5',
    borderRadius: '8px',
    transition: 'background-color 0.3s'
  }
})

const handleSliderChange = (value) => {
  if (value >= 100) {
    isVerified.value = true
    data.form.verified = true
    data.form.verify = 'verified'
    ElMessage.success('验证通过')
  }
}

const resetSlider = () => {
  sliderValue.value = 0
  isVerified.value = false
  data.form.verified = false
  data.form.verify = ''
}

// 修改登录逻辑
const login = async () => {
  try {
    const valid = await formRef.value.validate()
    if (valid) {
      if (!data.form.agreement) {
        ElMessage.warning('请先同意用户协议和隐私政策')
        return
      }
      if (!data.form.verified) {
        ElMessage.warning('请完成滑块验证')
        return
      }
      const res = await request.post('/login', data.form)    // 发送登录请求，调用后端 /login 接口。
      if (res.code === '200') {                                  //返回200表示接口的业务逻辑成功，登录成功
        ElMessage.success('登录成功')
        localStorage.setItem('xm-user', JSON.stringify(res.data))// 将用户信息存储到本地存储（用于保持登录状态）
        await router.push('/front/home')                         // 跳转到首页
      } else {
        ElMessage.error(res.msg)                                 // 显示错误信息
        resetSlider()                                            // 重置滑块状态
      }
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error('登录失败，请稍后重试')
    resetSlider()
  }
}

// 显示用户协议
const showAgreement = () => {
  ElMessageBox.alert(
    `
    <div class="agreement-content">
      <h3>用户协议</h3>
      <p>欢迎使用我们的旅游推荐平台！请仔细阅读以下条款：</p>
      
      <h4>1. 服务说明</h4>
      <p>本平台为用户提供旅游目的地推荐、行程规划、旅游攻略等服务。我们致力于为用户提供优质、真实的旅游信息。</p>
      
      <h4>2. 用户责任</h4>
      <ul>
        <li>用户需确保注册信息真实、准确</li>
        <li>遵守相关法律法规</li>
        <li>不得发布虚假、违法或侵权内容</li>
        <li>保护账号安全，对账号行为负责</li>
      </ul>
      
      <h4>3. 内容规范</h4>
      <p>用户发布的评论、攻略等内容需：</p>
      <ul>
        <li>真实可信，无虚假宣传</li>
        <li>遵守公序良俗</li>
        <li>尊重他人知识产权</li>
        <li>不含违法违规信息</li>
      </ul>
      
      <h4>4. 服务变更与终止</h4>
      <p>平台保留根据实际情况调整服务内容、范围、规则的权利。如遇重大变更，将提前通知用户。</p>
      
      <h4>5. 免责声明</h4>
      <p>对因不可抗力、第三方原因等导致的服���中断或用户损失，平台不承担责任。</p>
    </div>
    `,
    '用户协议',
    {
      confirmButtonText: '我知道了',
      dangerouslyUseHTMLString: true,
      customClass: 'agreement-dialog'
    }
  )
}

// 显示隐私政策
const showPrivacy = () => {
  ElMessageBox.alert(
    `
    <div class="privacy-content">
      <h3>隐私政策</h3>
      <p>我们重视您的隐私保护，请了解我们如何处理您的个人信息：</p>
      
      <h4>1. 信息收集</h4>
      <p>我们收集的信息包括：</p>
      <ul>
        <li>账号信息（用户名、密码等）</li>
        <li>个人资料（昵称、头像等）</li>
        <li>使用记录（浏览历史、搜索记录等）</li>
        <li>设备信息（设备型号、操作系统等）</li>
      </ul>
      
      <h4>2. 信息使用</h4>
      <p>我们使用收集的信息用于：</p>
      <ul>
        <li>提供和改进服务</li>
        <li>个性化推荐</li>
        <li>安全防护</li>
        <li>数据分析</li>
      </ul>
      
      <h4>3. 信息保护</h4>
      <p>我们采取的保护措施：</p>
      <ul>
        <li>数据加密存储</li>
        <li>访问权限控制</li>
        <li>安全审计</li>
        <li>应急响应机制</li>
      </ul>
      
      <h4>4. 信息共享</h4>
      <p>除以下情况外，我们不会共享您的个人信息：</p>
      <ul>
        <li>获得您的明确授权</li>
        <li>法律法规要求</li>
        <li>维护平台安全</li>
      </ul>
      
      <h4>5. 您的权利</h4>
      <p>您对个人信息享有：</p>
      <ul>
        <li>访问、更正权</li>
        <li>删除权</li>
        <li>撤回授权权</li>
        <li>注销账号权</li>
      </ul>
    </div>
    `,
    '隐私政策',
    {
      confirmButtonText: '我知道了',
      dangerouslyUseHTMLString: true,
      customClass: 'privacy-dialog'
    }
  )
}

const route = useRoute()

onMounted(() => {
  // 如果是从注册页面跳转来的，自动填充用户名
  if (route.query.registered === 'true' && route.query.username) {
    data.form.username = route.query.username
    ElMessage.success('注册成功，请登录')
  }
})
</script>

<style scoped>
.login-container {
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

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 500px;
}

.login-header {
  margin-bottom: 20px;
}

.login-title {
  font-size: 28px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 10px;
  color: #2c3e50;
}

.login-subtitle {
  text-align: center;
  color: #666;
  margin-bottom: 20px;
  font-size: 16px;
}

.login-form {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  margin: 20px 0;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.login-form :deep(.el-form-item:last-child) {
  margin-bottom: 0;
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
  background: linear-gradient(135deg, #42b983 0%, #33a06f 100%);
  border: none;
  transition: transform 0.2s;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}

.login-options {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.left-options {
  display: flex;
  gap: 15px;
}

.option-link {
  color: #666;
  text-decoration: none;
  transition: color 0.3s;
}

.option-link:hover {
  color: #42b983;
}

.register-link {
  color: #42b983;
  text-decoration: none;
  font-weight: 500;
  margin-left: 5px;
}

.register-link:hover {
  text-decoration: underline;
}

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

.promo-footer {
  margin-top: 40px;
}

.stats {
  display: flex;
  justify-content: space-around;
  text-align: center;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
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
  
  .login-box {
    width: 100%;
  }
  
  .promo-box {
    padding: 30px;
  }
  
  .feature-item {
    margin-bottom: 20px;
  }
}

.verify-item {
  margin-bottom: 20px;
}

.slider-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.slider-text {
  position: absolute;
  width: 100%;
  text-align: center;
  color: #666;
  font-size: 14px;
  pointer-events: none;
  transition: color 0.3s;
}

.slider-text.verified {
  color: #fff;
}

:deep(.el-slider) {
  width: 100%;
  margin: 0;
}

:deep(.el-slider__runway) {
  height: 40px;
  margin: 0;
  background: transparent;
}

:deep(.el-slider__bar) {
  height: 40px;
  background: #42b983;
  border-radius: 8px;
  transition: opacity 0.3s;
}

:deep(.el-slider__button-wrapper) {
  top: 5px;
  width: 30px;
  height: 30px;
}

.slider-button {
  width: 30px;
  height: 30px;
  background: white;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  color: #666;
}

:deep(.el-slider__button) {
  display: none;
}

/* 移除����轨道的边框 */
:deep(.el-slider__runway.show-input) {
  margin-right: 0;
}

:deep(.el-slider__input) {
  display: none;
}

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

/* 添加协议弹窗样式 */
:deep(.agreement-dialog),
:deep(.privacy-dialog) {
  max-width: 600px;
  
  .el-message-box__content {
    max-height: 60vh;
    overflow-y: auto;
    padding: 20px;
  }
  
  h3 {
    font-size: 20px;
    margin-bottom: 20px;
    color: #2c3e50;
    text-align: center;
  }
  
  h4 {
    font-size: 16px;
    margin: 20px 0 10px;
    color: #42b983;
  }
  
  p {
    margin: 10px 0;
    line-height: 1.6;
    color: #666;
  }
  
  ul {
    margin: 10px 0;
    padding-left: 20px;
    
    li {
      line-height: 1.8;
      color: #666;
    }
  }
}
</style>