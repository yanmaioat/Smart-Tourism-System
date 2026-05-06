<template>
  <div class="comment-section">
    <!-- 评论头部 -->
    <div class="comment-header">
      <h2 class="comment-title">
        <el-icon><ChatDotRound /></el-icon>
        评论 
        <span class="comment-count">{{ data.commentCount }}</span>
      </h2>
    </div>

    <!-- 评论输入框 -->
    <div class="comment-input-area">
      <el-input
        type="textarea"
        v-model="data.form.content"
        :rows="3"
        placeholder="写下您的评论..."
        resize="none"
      />
      <div class="input-footer">
        <el-button type="primary" @click="addComment(null)">
          <el-icon><Plus /></el-icon>
          发表评论
        </el-button>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list">
      <div v-for="item in data.commentList" :key="item.id" class="comment-item">
        <!-- 主评论 -->
        <div class="comment-main">
          <div class="avatar">
            <img :src="item.userAvatar" :alt="item.userName">
          </div>
          <div class="comment-content">
            <div class="comment-user">{{ item.userName }}</div>
            <div class="comment-text">{{ item.content }}</div>
            <div class="comment-actions">
              <span class="time">{{ item.time }}</span>
              <el-button 
                link 
                type="primary" 
                @click="handleReply(item)"
                :class="{ 'active-btn': item.showReply }"
              >
                <el-icon><ChatLineSquare /></el-icon>
                回复
              </el-button>
              <el-button 
                v-if="item.userId === data.user.id" 
                link 
                type="danger" 
                @click="del(item.id)"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
            <!-- 回复框 -->
            <div v-if="item.showReply" class="reply-input">
              <el-input
                type="textarea"
                v-model="item.replyContent"
                :rows="2"
                placeholder="写下您的回复..."
                resize="none"
              />
              <div class="reply-footer">
                <el-button type="primary" size="small" @click="addComment(item)">
                  <el-icon><Check /></el-icon>
                  确认回复
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 二级评论 -->
        <div v-if="item.children?.length" class="sub-comments">
          <div v-for="subItem in item.children" :key="subItem.id" class="sub-comment-item">
            <div class="avatar">
              <img :src="subItem.userAvatar" :alt="subItem.userName">
            </div>
            <div class="comment-content">
              <div class="comment-user">
                {{ subItem.userName }}
                <span v-if="subItem.parentUserName !== item.userName" class="reply-to">
                  回复 @{{ subItem.parentUserName }}
                </span>
              </div>
              <div class="comment-text">{{ subItem.content }}</div>
              <div class="comment-actions">
                <span class="time">{{ subItem.time }}</span>
                <el-button 
                  link 
                  type="primary" 
                  @click="handleReply(subItem)"
                  :class="{ 'active-btn': subItem.showReply }"
                >
                  <el-icon><ChatLineSquare /></el-icon>
                  回复
                </el-button>
                <el-button 
                  v-if="subItem.userId === data.user.id" 
                  link 
                  type="danger" 
                  @click="del(subItem.id)"
                >
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
              <!-- 回复框 -->
              <div v-if="subItem.showReply" class="reply-input">
                <el-input
                  type="textarea"
                  v-model="subItem.replyContent"
                  :rows="2"
                  placeholder="写下您的回复..."
                  resize="none"
                />
                <div class="reply-footer">
                  <el-button type="primary" size="small" @click="addComment(subItem)">
                    <el-icon><Check /></el-icon>
                    确认回复
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页器 -->
    <div v-if="data.total" class="pagination">
      <el-pagination
        v-model:current-page="data.pageNum"
        :page-size="data.pageSize"
        :total="data.total"
        @current-change="load"
        background
        layout="prev, pager, next"
      />
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import request from "@/utils/request.js";
import router from "@/router/index.js";
import {ElMessage, ElMessageBox} from "element-plus";

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  fid: router.currentRoute.value.query.id,
  commentCount: 0,
  form: {},
  commentList: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
})

const props = defineProps({
  module: String
})

const handleReply = (comment) => {
  comment.showReply = !comment.showReply
}

const del = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' }).then(res => {
    request.delete('/comment/delete/' + id).then(res => {
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

const load = () => {
  request.get('/comment/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      fid: data.fid,
      module: props.module
    }
  }).then(res => {
    data.commentList = res.data?.list
    data.total = res.data?.total
  })
  request.get('/comment/selectCount/' + data.fid + '/' + props.module).then(res => {
    data.commentCount = res.data
  })
}
load()

const addComment = (parentComment) => {
  if (parentComment) {
    data.form.pid = parentComment.id
    data.form.content = parentComment.replyContent
  }
  if (!data.form.content) {
    ElMessage.warning('请输入评论')
    return
  }
  data.form.fid = data.fid
  data.form.module = props.module
  request.post('/comment/add', data.form).then(res => {
    if (res.code === '200') {
      data.form = {}
      ElMessage.success('评论成功')
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}
</script>

<style scoped>
.comment-section {
  padding: 32px 0;
}

/* 评论头部 */
.comment-header {
  margin-bottom: 24px;
}

.comment-title {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.comment-count {
  color: #409eff;
}

/* 评论输入区域 */
.comment-input-area {
  margin-bottom: 32px;
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
}

.input-footer {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

/* 评论列表 */
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.comment-item {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-main {
  display: flex;
  gap: 16px;
}

.avatar {
  flex-shrink: 0;
}

.avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.comment-content {
  flex: 1;
  min-width: 0;
}

.comment-user {
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 8px;
}

.comment-text {
  color: #4a5568;
  line-height: 1.6;
  margin-bottom: 12px;
  text-align: justify;
}

.comment-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
}

.time {
  color: #a0aec0;
  font-size: 13px;
}

/* 回复区域 */
.reply-input {
  margin-top: 12px;
  background: #f8f9fa;
  padding: 16px;
  border-radius: 6px;
}

.reply-footer {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

/* 二级评论 */
.sub-comments {
  margin-left: 56px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.sub-comment-item {
  display: flex;
  gap: 16px;
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
}

.reply-to {
  color: #409eff;
  font-size: 13px;
  margin-left: 8px;
}

/* 分页器 */
.pagination {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .comment-section {
    padding: 20px 0;
  }

  .sub-comments {
    margin-left: 32px;
  }

  .comment-actions {
    flex-wrap: wrap;
  }
}
</style>