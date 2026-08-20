package com.jichi.voiceshopping.config;

import io.agentscope.core.model.DashScopeChatModel;
import io.agentscope.core.model.Model;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentScopeConfig {

    @Value("${agentscope.dashscope.api-key}")
    private String apiKey;

    @Value("${aipintuan.voice-agent.llm.main-model}")
    private String mainModelName;

    @Value("${aipintuan.voice-agent.llm.light-model}")
    private String lightModelName;

    /**
     * 主对话模型（推荐、情感）
     */
    @Bean("mainChatModel")
    public Model mainChatModel() {
        return DashScopeChatModel.builder()
                .apiKey(apiKey)
                .modelName(mainModelName)
                .build();
    }

    /**
     * 轻量模型（意图识别、分类任务）
     */
    @Bean("lightChatModel")
    public Model lightChatModel() {
        return DashScopeChatModel.builder()
                .apiKey(apiKey)
                .modelName(lightModelName)
                .build();
    }
}