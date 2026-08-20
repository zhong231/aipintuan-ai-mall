package com.aipintuan.voiceagent.event;

public record UserSpokenEvent(String sessionId, Long userId, String utterance, long timestamp) {
}