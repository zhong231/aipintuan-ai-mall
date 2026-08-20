package com.aipintuan.voiceagent.controller;

import com.aipintuan.voiceagent.service.ChitchatTemplates;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/debug/chitchat")
@RequiredArgsConstructor
public class ChitchatDebugController {

    private final ChitchatTemplates templates;

    @GetMapping("/match")
    public Map<String, Object> match(@RequestParam String text) {
        Optional<String> hit = templates.match(text);
        return Map.of("text", text, "hit", hit.isPresent(),
                "reply", hit.orElse(""));
    }
}