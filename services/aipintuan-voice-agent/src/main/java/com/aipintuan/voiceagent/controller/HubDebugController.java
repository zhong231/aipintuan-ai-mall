package com.aipintuan.voiceagent.controller;

import com.aipintuan.voiceagent.dto.RecommendResult;
import com.aipintuan.voiceagent.service.PerspectiveHubService;
import com.aipintuan.voiceagent.service.RecommendOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/hub")
@RequiredArgsConstructor
public class HubDebugController {

    private final RecommendOrchestrator recommendOrchestrator;
    private final PerspectiveHubService perspectiveHub;

    @PostMapping("/perspective")
    public Map<String, Object> perspective(@RequestParam String sessionId,
                                           @RequestParam Long userId,
                                           @RequestBody Map<String, Object> body) {
        String utterance = (String) body.getOrDefault("utterance", "");
        @SuppressWarnings("unchecked")
        Map<String, Object> slots = (Map<String, Object>) body.getOrDefault("slots", Map.of());

        RecommendResult rec = recommendOrchestrator.recommend(sessionId, userId, utterance, slots);
        String digest = perspectiveHub.discuss(sessionId, utterance, rec.items());

        return Map.of(
                "recommendation", rec,
                "perspectiveDigest", digest
        );
    }
}