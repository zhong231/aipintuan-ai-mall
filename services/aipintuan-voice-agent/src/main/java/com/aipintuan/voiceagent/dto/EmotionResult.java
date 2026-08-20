package com.aipintuan.voiceagent.dto;

import java.util.List;

public record EmotionResult(
        String speechText,
        List<RecommendedItem> displayBlocks
) {}