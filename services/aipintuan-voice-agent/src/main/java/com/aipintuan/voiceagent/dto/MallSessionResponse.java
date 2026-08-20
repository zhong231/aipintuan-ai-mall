package com.aipintuan.voiceagent.dto;

public record MallSessionResponse(
        String sessionId,
        Long agentUserId,
        Long agentProductId
) {}
