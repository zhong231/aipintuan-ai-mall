package com.aipintuan.voiceagent.controller;

import com.aipintuan.voiceagent.dto.IntentResult;
import com.aipintuan.voiceagent.service.IntentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agent")
@RequiredArgsConstructor
public class IntentDebugController {

    private final IntentService intentService;

    @PostMapping("/intent")
    public IntentResult classify(@RequestParam String sessionId,
                                 @RequestBody String utterance) {
        return intentService.classify(sessionId, utterance);
    }
}