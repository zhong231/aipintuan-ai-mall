package com.jichi.voiceshopping.controller;

import com.jichi.voiceshopping.dto.*;
import com.jichi.voiceshopping.service.MallIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"http://127.0.0.1:8088", "http://localhost:8088"})
@RequestMapping("/api/v1/integration/mall")
@RequiredArgsConstructor
public class MallIntegrationController {

    private final MallIntegrationService service;

    @PostMapping("/session")
    public MallSessionResponse start(@RequestBody MallSessionRequest request) {
        return service.register(request);
    }

    @PostMapping("/catalog-session")
    public MallCatalogSessionResponse startCatalog(@RequestBody MallCatalogSessionRequest request) {
        return service.registerCatalog(request);
    }
}
