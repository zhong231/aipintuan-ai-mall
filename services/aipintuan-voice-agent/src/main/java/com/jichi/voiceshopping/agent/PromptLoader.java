package com.jichi.voiceshopping.agent;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class PromptLoader {

    public String load(String path) {
        ClassPathResource resource = new ClassPathResource(path, PromptLoader.class.getClassLoader());
        try (InputStream input = resource.getInputStream()) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("加载 prompt 失败：" + path, e);
        }
    }
}
