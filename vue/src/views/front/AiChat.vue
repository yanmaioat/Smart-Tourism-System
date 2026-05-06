<template>
  <div class="chat-container">
    <div class="back-section">
      <el-button @click="$router.back()" class="back-btn">
        <i class="el-icon-arrow-left"></i> 返回
      </el-button>
    </div>

    <div class="chat-header">
      <h2>智慧清远旅游小助手</h2>
      <div class="status">
        <span class="status-dot" :class="{ active: !loading }"></span>
        {{ loading ? '思考中...' : '在线' }}
      </div>
    </div>

    <div class="chat-box">
      <div class="chat-messages" ref="messageBox">
        <div v-if="messages.length === 0" class="welcome-message">
          <h3>您好！我是智慧清远旅游小助手</h3>
          <p>我可以帮您解答关于景点、路线、商品、交易、账户等问题</p>
          <div class="quick-actions">
            <el-button
                v-for="(action, index) in quickActions"
                :key="index"
                type="primary"
                plain
                round
                size="small"
                @click="quickAsk(action)">
              {{ action }}
            </el-button>
          </div>
        </div>

        <div v-for="(msg, index) in messages"
             :key="index"
             :class="['message-wrapper', msg.type === 'user' ? 'user' : 'ai']">
          <div class="message-content">
            <div class="message-bubble">{{ msg.content }}</div>
            <div class="message-time">{{ formatTime(msg.time || Date.now()) }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="input-area">
      <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          :placeholder="loading ? '等待回复中...' : '请输入您的问题...（Enter发送，Ctrl+Enter换行）'"
          :disabled="loading"
          resize="none"
          @keyup.enter.native.exact="sendMessage"
          @keyup.ctrl.enter.native="addNewLine"
      />
      <div class="input-actions">
        <el-button
            type="primary"
            :loading="loading"
            round
            @click="sendMessage">
          {{ loading ? '思考中...' : '发送' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'
import { friendlyTime } from '@/utils/date'

export default {
  name: 'AiChat',
  data() {
    return {
      messages: [],
      inputMessage: '',
      loading: false,
      quickActions: [
        '清远景点位置',
        '清远景点详情',
        '清远景点路线',
        '如何购买商品？',
        '如何修改个人信息？',
        '退款政策是什么？'
      ]
    }
  },
  created() {
    // 添加初始欢迎消息
    this.messages.push({
      type: 'ai',
      content: `欢迎来到清远旅游网！
我可以帮您解答以下问题：
1. 清远景点详情
2. 清远景点位置
3. 清远景点路线
4. 账户和个人信息
5. 平台规则和政策

请问有什么可以帮您？`,
      time: Date.now()
    })
  },
  methods: {
    async sendMessage() {
      if (!this.inputMessage.trim() || this.loading) return

      // 添加用户消息
      const userMessage = this.inputMessage.trim()
      this.messages.push({
        type: 'user',
        content: userMessage,
        time: Date.now()
      })

      this.inputMessage = ''
      this.loading = true

      try {
        // 在发送给AI之前，添加上下文提示
        const contextPrompt = `你是一个智慧清远旅游网的小助手，请用专业、友善的语气回答用户的问题。
用户问题：${userMessage}`

        const res = await request.post('/ai/chat', {
          question: contextPrompt
        })

        if (res.code === '200') {
          this.messages.push({
            type: 'ai',
            content: res.data,
            time: Date.now()
          })
        } else {
          this.$message.error(res.msg || '获取回答失败')
        }
      } catch (error) {
        this.$message.error('获取回答失败：' + error.message)
      } finally {
        this.loading = false
      }

      this.scrollToBottom()
    },

    scrollToBottom() {
      this.$nextTick(() => {
        const messageBox = this.$refs.messageBox
        messageBox.scrollTop = messageBox.scrollHeight
      })
    },



  formatTime(timestamp) {
    return friendlyTime(timestamp)
  },

  addNewLine(e) {
    e.preventDefault()
    this.inputMessage += '\n'
  },

  quickAsk(question) {
    this.inputMessage = question
    this.sendMessage()
  }
  }
  }
  </script>

  <style scoped>
  .chat-container {
    max-width: 1000px;
    margin: 20px auto;
    padding: 20px;
    height: calc(100vh - 40px);
    display: flex;
    flex-direction: column;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  }

  .back-section {
    margin-bottom: 20px;
  }

  .back-btn {
    padding: 10px 20px;
    font-size: 14px;
    border-radius: 20px;
    transition: all 0.3s;
  }

  .back-btn:hover {
    transform: translateX(-5px);
  }

  .chat-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 10px 20px 10px;
    border-bottom: 2px solid #f0f2f5;
  }

  .chat-header h2 {
    margin: 0;
    color: #303133;
    font-size: 24px;
  }

  .status {
    display: flex;
    align-items: center;
    color: #666;
    font-size: 14px;
  }

  .status-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: #999;
    margin-right: 6px;
  }

  .status-dot.active {
    background: #67C23A;
  }

  .chat-box {
    flex: 1;
    margin: 20px 0;
    overflow: hidden;
  }

  .chat-messages {
    height: 100%;
    overflow-y: auto;
    padding: 10px;
  }

  .welcome-message {
    text-align: center;
    padding: 40px 0;
    color: #909399;
  }

  .welcome-message h3 {
    color: #409EFF;
    margin-bottom: 10px;
  }

  .quick-actions {
    margin-top: 20px;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    justify-content: center;
  }

  .quick-actions .el-button {
    margin: 4px;
  }

  .message-wrapper {
    display: flex;
    margin-bottom: 16px;
  }

  .message-wrapper.user {
    justify-content: flex-end;
  }

  .message-content {
    max-width: 70%;
  }

  .message-bubble {
    padding: 12px 16px;
    border-radius: 8px;
    font-size: 14px;
    line-height: 1.6;
    white-space: pre-wrap;
    word-break: break-word;
  }

  .user .message-bubble {
    background: #409EFF;
    color: white;
  }

  .ai .message-bubble {
    background: #f4f4f5;
    color: #333;
  }

  .message-time {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
    text-align: right;
  }

  .input-area {
    padding: 20px;
    background: #fff;
    border-top: 1px solid #eee;
  }

  .input-actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 12px;
  }

  /* 响应式设计 */
  @media (max-width: 768px) {
    .chat-container {
      margin: 0;
      height: 100vh;
      border-radius: 0;
      padding: 15px;
    }

    .back-section {
      margin-bottom: 15px;
    }

    .chat-header h2 {
      font-size: 20px;
    }

    .message-bubble {
      max-width: 85%;
    }
  }
  </style>