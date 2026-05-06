<template>
  <footer class="site-footer">
    <div class="footer-content">
      <!-- 品牌介绍 -->
      <div class="footer-section">
        <h3 class="footer-title">旅游网</h3>
        <div class="brand-intro">
          <p class="slogan">中国年轻一代用得更多的旅游网站</p>
          <p class="sub-slogan">上亿旅行者共同打造的"旅行神器"</p>
          <div class="stats">
            <div class="stat-item">
              <span class="stat-number">60,000+</span>
              <span class="stat-text">全球旅游目的地</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">600,000+</span>
              <span class="stat-text">细分目的地玩法</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">7.6亿+</span>
              <span class="stat-text">攻略下载量</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">38,000+</span>
              <span class="stat-text">旅游产品供应商</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 关于我们 -->
      <div class="footer-section">
        <h3 class="footer-title">关于我们</h3>
        <ul class="footer-links">
          <li><a href="#">关于我们</a></li>
          <li><a href="#">联系我们</a></li>
          <li><a href="#">隐私政策</a></li>
          <li><a href="#">商标声明</a></li>
          <li><a href="#">服务协议</a></li>
          <li><a href="#">商城平台服务协议</a></li>
          <li><a href="#">网络信息侵权通知指引</a></li>
          <li><a href="#" class="highlight">旅游网服务监督员</a></li>
        </ul>
      </div>

      <!-- 旅行服务 -->
      <div class="footer-section">
        <h3 class="footer-title">旅行服务</h3>
        <ul class="footer-links">
          <li><a href="#">旅游攻略</a></li>
          <li><a href="#">酒店预订</a></li>
          <li><a href="#">旅游特价</a></li>
          <li><a href="#">旅游问答</a></li>
          <li><a href="#">旅游指南</a></li>
          <li><a href="#">旅游资讯</a></li>
          <li><a href="#" class="highlight">旅行商城全球商家入驻</a></li>
        </ul>
      </div>

      <!-- 联系方式 -->
      <div class="footer-section contact-section">
        <div class="logo-container">
          <img src="@/assets/imgs/logo.png" alt="旅游网 Logo" class="footer-logo">
        </div>
        <p class="contact-slogan">旅游前，先上旅游网！</p>
        <p class="contact-phone">TEL：010-65589988</p>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.site-footer {
  background-color: #2c3e50;
  color: #fff;
  padding: 60px 0;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1.5fr 1fr 1fr 1fr;
  gap: 40px;
  padding: 0 20px;
}

/* 标题样式 */
.footer-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #fff;
  position: relative;
}

.footer-title::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: -8px;
  width: 30px;
  height: 2px;
  background: #3498db;
}

/* 品牌介绍部分 */
.brand-intro {
  color: #a0aec0;
}

.slogan {
  font-size: 16px;
  margin-bottom: 12px;
}

.sub-slogan {
  font-size: 14px;
  margin-bottom: 24px;
}

.stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-number {
  color: #3498db;
  font-size: 16px;
  font-weight: 600;
}

.stat-text {
  color: #a0aec0;
  font-size: 13px;
}

/* 链接列表 */
.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin-bottom: 12px;
}

.footer-links a {
  color: #a0aec0;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
}

.footer-links a:hover {
  color: #3498db;
}

.footer-links .highlight {
  color: #3498db;
}

/* 联系方式部分 */
.contact-section {
  text-align: center;
}

.logo-container {
  margin-bottom: 20px;
}

.footer-logo {
  width: 60px;
  height: 60px;
  object-fit: contain;
}

.contact-slogan {
  font-size: 16px;
  margin-bottom: 12px;
  color: #fff;
}

.contact-phone {
  font-size: 14px;
  color: #a0aec0;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .site-footer {
    padding: 40px 0;
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: 32px;
  }

  .stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .contact-section {
    order: -1;
  }
}
</style>

<script lang="ts">
</script>