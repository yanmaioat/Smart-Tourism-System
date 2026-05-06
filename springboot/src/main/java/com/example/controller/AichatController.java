package com.example.controller;

import com.example.common.Result;
import com.example.entity.AiChat;
import com.example.service.AiChatService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/ai")
public class AichatController {

    @Resource
    private AiChatService aiChatService;

    @PostMapping("/chat")
    public Result chat(@RequestBody AiChat aiChat) {
        String answer = aiChatService.chat(aiChat);
        return Result.success(answer);
    }
}