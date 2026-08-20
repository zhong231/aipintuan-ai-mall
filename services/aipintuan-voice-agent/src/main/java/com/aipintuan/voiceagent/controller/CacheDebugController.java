package com.aipintuan.voiceagent.controller;

import com.aipintuan.voiceagent.service.UtteranceFingerprint;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/debug/cache")
public class CacheDebugController {

    @GetMapping("/fingerprint")
    public Map<String, String> fingerprint(@RequestParam String text) {
        return Map.of("text", text, "fp", UtteranceFingerprint.compute(text));
    }
}