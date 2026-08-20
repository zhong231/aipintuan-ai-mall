package com.aipintuan.voiceagent.controller;

import com.aipintuan.voiceagent.memory.ShortTermMemory;
import com.aipintuan.voiceagent.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileDebugController {

    private final UserProfileService profileService;
    private final ShortTermMemory memory;

    @GetMapping("/{userId}")
    public Object getProfile(@PathVariable Long userId) {
        return profileService.load(userId);
    }

    @GetMapping("/memory/{sessionId}")
    public Object getMemory(@PathVariable String sessionId) {
        return memory.recent(sessionId, 10);
    }
}