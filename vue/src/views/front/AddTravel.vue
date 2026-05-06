<template>
  <div class="page-wrapper">
    <div class="add-travel-page">
      <!-- 主要内容区域 -->
      <div class="main-content">
        <div class="content-container">
          <!-- 左侧编辑区 -->
          <div class="edit-section">
            <!-- 操作栏 -->
            <div class="action-bar">
              <div class="left-actions">
                <el-button class="back-btn" @click="router.back()">
                  <el-icon><ArrowLeft /></el-icon>
                  返回列表
                </el-button>
              </div>
              <h1 class="page-title">写游记</h1>
              <div class="right-actions">
                <el-button
                  type="primary"
                  class="publish-btn"
                  @click="save"
                >
                  <el-icon><Check /></el-icon>
                  发布游记
                </el-button>
              </div>
            </div>

            <!-- 编辑表单 -->
            <el-form
              ref="form"
              :model="data.form"
              :rules="data.rules"
              class="travel-form"
            >
              <!-- 标题输入 -->
              <el-form-item prop="title">
                <el-input
                  v-model="data.form.title"
                  placeholder="请输入标题..."
                  class="title-input"
                  size="large"
                />
              </el-form-item>

              <!-- 编辑器 -->
              <div class="editor-wrapper">
                <Toolbar
                  style="border-bottom: 1px solid #dcdfe6"
                  :editor="editorRef"
                  :mode="mode"
                />
                <Editor
                  class="editor-content"
                  v-model="data.form.content"
                  :mode="mode"
                  :defaultConfig="editorConfig"
                  @onCreated="handleCreated"
                />
              </div>
            </el-form>
          </div>

          <!-- 右侧信息栏 -->
          <div class="info-section">
            <div class="info-card">
              <h3 class="info-title">游记信息</h3>
              <el-form
                ref="infoForm"
                :model="data.form"
                :rules="data.rules"
                label-position="top"
              >
                <!-- 封面上传 -->
                <el-form-item label="封面图片">
                  <el-upload
                    :action="baseUrl + '/files/upload'"
                    :headers="{ 'token': data.user.token }"
                    list-type="picture-card"
                    :limit="1"
                    :on-success="handleFileUpload"
                  >
                    <el-icon><Plus /></el-icon>
                  </el-upload>
                </el-form-item>

                <!-- 简介 -->
                <el-form-item prop="descr" label="游记简介">
                  <el-input
                    type="textarea"
                    :rows="4"
                    v-model="data.form.descr"
                    placeholder="简要描述此次旅行..."
                  />
                </el-form-item>

                <!-- 基本信息 -->
                <el-form-item prop="startDate" label="出行日期">
                  <el-date-picker
                    v-model="data.form.startDate"
                    type="datetime"
                    format="YYYY-MM-DD HH:mm"
                    value-format="YYYY-MM-DD HH:mm"
                    placeholder="选择日期"
                    style="width: 100%"
                  />
                </el-form-item>

                <el-form-item prop="location" label="出行地点">
                  <el-input
                    v-model="data.form.location"
                    placeholder="输入地点"
                  >
                    <template #prefix>
                      <el-icon><Location /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-row :gutter="12">
                  <el-col :span="12">
                    <el-form-item label="费用" prop="money">
                      <el-input-number
                        v-model="data.form.money"
                        :min="1"
                        :precision="2"
                        :step="100"
                        style="width: 100%"
                        placeholder="输入费用"
                      >
                        <template #prefix>¥</template>
                      </el-input-number>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item prop="days" label="出行天数">
                      <el-input-number
                        v-model="data.form.days"
                        :min="1"
                        :step="1"
                        style="width: 100%"
                        placeholder="输入天数"
                      >
                        <template #suffix>天</template>
                      </el-input-number>
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import router from "@/router/index.js";
import { reactive } from "vue";
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import {onBeforeUnmount, ref, shallowRef} from "vue";
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";

const data = reactive({
  id: router.currentRoute.value.query.id,
  user: JSON.parse(localStorage.getItem('xm-user')),
  form: {
    title: '', // 标题
    descr: '', // 简介
    content: '', // 游记内容
    cover: '', // 封面图片
    startDate: '', // 出行日期
    location: '', // 出行地点
    money: '', // 费用
    days: '', // 出行天数
  },
  rules: {
    title: [
      { required: true, message: '请输入标题', trigger: 'blur' }
    ],
    descr: [
      { required: true, message: '请输入简介', trigger: 'blur' }
    ],
    startDate: [
      { required: true, message: '请选择出行日期', trigger: 'blur' }
    ],
    money: [
      { required: true, message: '请输入花费金额', trigger: 'blur' }
    ],
    location: [
      { required: true, message: '请输入出行地点', trigger: 'blur' }
    ],
    days: [
      { required: true, message: '请输入出行天数', trigger: 'blur' }
    ],
  }
});

const baseUrl = import.meta.env.VITE_BASE_URL;
const form = ref();

// 初始化数据（如果存在id，则加载已有数据）
if (data.id) {
  request.get('/travels/selectById/' + data.id).then(res => {
    data.form = res.data;
  });
}

// 发布或更新游记
const save = () => {
  if (data.id) {
    update();
  } else {
    add();
  }
};

// 添加游记
const add = () => {
  form.value.validate((valid) => {
    if (valid) { // 表单校验通过
      // 检查是否填写了所有必填字段
      if (!data.form.title || !data.form.descr || !data.form.content || !data.form.cover || !data.form.startDate || !data.form.location || !data.form.money || !data.form.days) {
        ElMessage.warning('请完整填写标题、简介、内容、封面图片、出行日期、出行地点、费用和出行天数');
        return;
      }

      // 提交数据
      request.post('/travels/add', data.form).then(res => {
        if (res.code === '200') {
          ElMessage.success('发布成功');
          if (res.data.id) {
            router.replace('/front/addTravel?id=' + res.data.id);
          }
        } else {
          ElMessage.error(res.msg);
        }
      });
    }
  });
};

// 更新游记
const update = () => {
  form.value.validate((valid) => {
    if (valid) { // 表单校验通过
      // 检查是否填写了所有必填字段
      if (!data.form.title || !data.form.descr || !data.form.content || !data.form.cover || !data.form.startDate || !data.form.location || !data.form.money || !data.form.days) {
        ElMessage.warning('请完整填写标题、简介、内容、封面图片、出行日期、出行地点、费用和出行天数');
        return;
      }

      // 提交数据
      request.put('/travels/update', data.form).then(res => {
        if (res.code === '200') {
          ElMessage.success('更新成功');
        } else {
          ElMessage.error(res.msg);
        }
      });
    }
  });
};

// 处理文件上传
const handleFileUpload = (res) => {
  data.form.cover = res.data;
};

/* wangEditor5 初始化开始 */
const editorRef = shallowRef(); // 编辑器实例，必须用 shallowRef
const mode = 'default';
const editorConfig = { MENU_CONF: {} };

// 图片上传配置
editorConfig.MENU_CONF['uploadImage'] = {
  headers: {
    token: data.user.token,
  },
  server: baseUrl + '/files/wang/upload', // 服务端图片上传接口
  fieldName: 'file', // 服务端图片上传接口参数
};

// 组件销毁时，也及时销毁编辑器，否则可能会造成内存泄漏
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor == null) return;
  editor.destroy();
});

// 记录 editor 实例，重要！
const handleCreated = (editor) => {
  editorRef.value = editor;
};
/* wangEditor5 初始化结束 */
</script>
<style scoped>
.page-wrapper {
  position: relative;
  width: 100%;
  min-height: 100vh;
}

.add-travel-page {
  position: relative;
  width: 100%;
  min-height: 100vh;
  padding-top: 60px; /* 为固定导航栏预留空间 */
  background: #f5f7fa;
}

/* 导航栏 */
.nav-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000; /* 提高层级 */
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  height: 60px;
}

.nav-container {
  max-width: 1600px;
  height: 100%;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 8px;
}

.publish-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 24px;
}

/* 主要内容区域 */
.main-content {
  position: relative;
  width: 100%;
  min-height: calc(100vh - 60px);
  padding-top: 24px; /* 调整内容区域的上边距 */
}

.content-container {
  max-width: 1600px;
  margin: 0 auto;
  padding: 24px;
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 24px;
}

/* 左侧编辑区 */
.edit-section {
  background: white;
  border-radius: 8px;
  padding: 24px;
  height: calc(100vh - 48px);
  overflow-y: auto;
}

.title-input :deep(.el-input__wrapper) {
  padding: 12px 20px;
  margin-bottom: 20px;
}

.title-input :deep(.el-input__inner) {
  font-size: 24px;
  font-weight: 500;
  border: none;
}

.editor-wrapper {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.editor-content {
  height: calc(100vh - 300px);
  overflow-y: hidden;
}

/* 右侧信息栏 */
.info-section {
  position: sticky;
  top: 108px; /* 调整固定位置，避免与导航栏重叠 */
  height: fit-content;
}

.info-card {
  background: white;
  border-radius: 8px;
  padding: 24px;
}

.info-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 20px 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .content-container {
    grid-template-columns: 1fr;
  }

  .info-section {
    position: static;
  }

  .editor-content {
    height: 600px;
  }
}

@media (max-width: 768px) {
  .nav-container {
    padding: 0 16px;
  }

  .content-container {
    padding: 16px;
  }

  .edit-section,
  .info-card {
    padding: 16px;
  }

  .editor-content {
    height: 400px;
  }

  .publish-btn {
    padding: 8px 16px;
  }

  .nav-header {
    height: 56px; /* 移动端导航栏稍微降低高度 */
  }

  .main-content {
    padding-top: 76px; /* 相应调整内容区域的上边距 */
  }

  .add-travel-page {
    padding-top: 56px; /* 移动端导航栏高度调整 */
  }

  .nav-header {
    height: 56px;
  }

  .main-content {
    min-height: calc(100vh - 56px);
  }
}

/* 操作栏样式 */
.action-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.left-actions,
.right-actions {
  flex: 1;
}

.right-actions {
  display: flex;
  justify-content: flex-end;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  text-align: center;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
}

.publish-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 24px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .action-bar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
    text-align: center;
  }

  .left-actions,
  .right-actions {
    display: flex;
    justify-content: center;
  }

  .page-title {
    order: -1;
    margin-bottom: 16px;
  }

  .edit-section {
    height: auto;
    min-height: calc(100vh - 48px);
  }

  .editor-content {
    height: 400px;
  }
}
</style>