package com.example.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.AiChat;
import com.example.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service  // 声明该类为 Spring 的服务组件
public class AiChatService {

    // 日志记录器，用于记录操作日志
    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);

    // 阿里云 DashScope API 的 URL 地址
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    // 从配置文件中读取阿里云 DashScope 的 API 密钥
    @Value("${aliyun.dashscope.apiKey}")
    private String apiKey;

    /**
     * 用户与 AI 对话的核心方法
     * 1. 构建请求体并发送 HTTP 请求到阿里云 DashScope API。
     * 2. 解析返回结果并提取 AI 的回答。
     * 3. 如果发生错误，则抛出自定义异常。
     *
     * @param aiChat 包含用户提问信息的对象
     * @return AI 的回答文本
     */
    public String chat(AiChat aiChat) {
        try {
            // 记录日志：开始 AI 对话，并打印用户的问题
            log.info("开始AI对话，问题：{}", aiChat.getQuestion());

            // 构建消息列表（包含用户的角色和问题内容）
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");  // 角色为用户
            userMessage.put("content", aiChat.getQuestion());  // 用户的问题内容
            messages.add(userMessage);  // 将用户消息添加到消息列表

            // 构建输入参数（包含消息列表）
            Map<String, Object> input = new HashMap<>();
            input.put("messages", messages);

            // 构建额外参数（指定返回结果的格式为文本）
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("result_format", "text");

            // 构建完整的请求体（包含模型名称、输入参数和额外参数）
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", "qwen-turbo");  // 使用的 AI 模型
            payload.put("input", input);  // 输入参数
            payload.put("parameters", parameters);  // 额外参数

            // 发送 HTTP POST 请求到阿里云 DashScope API
            HttpResponse response = HttpRequest.post(API_URL)
                    .header("Authorization", "Bearer " + apiKey)  // 设置 Authorization 头部，携带 API 密钥
                    .header("Content-Type", "application/json")  // 设置 Content-Type 为 JSON 格式
                    .body(JSONUtil.toJsonStr(payload))  // 将请求体序列化为 JSON 字符串
                    .execute();  // 执行请求

            // 获取响应结果并记录日志
            String result = response.body();
            log.info("API返回结果：{}", result);

            // 解析响应结果为 JSON 对象
            JSONObject jsonResult = JSONUtil.parseObj(result);

            // 检查 API 是否返回错误
            if (jsonResult.containsKey("code") && !jsonResult.getStr("code").equals("0")) {
                log.error("API返回错误：{}", result);
                throw new CustomException(ResultCodeEnum.SYSTEM_ERROR.code,
                        "AI服务返回错误：" + jsonResult.getStr("message"));
            }

            // 提取 AI 的回答文本
            String answer = jsonResult.getJSONObject("output").getStr("text");
            log.info("AI回答完成：{}", answer);

            // 返回 AI 的回答
            return answer;

        } catch (Exception e) {
            // 捕获异常并记录日志
            log.error("AI服务调用失败", e);

            // 如果是自定义异常，则直接抛出；否则包装成系统错误异常抛出
            if (e instanceof CustomException) {
                throw e;
            }
            throw new CustomException(ResultCodeEnum.SYSTEM_ERROR.code,
                    "系统错误：" + e.getMessage());
        }
    }
}