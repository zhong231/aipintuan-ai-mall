package com.aipintuan.voiceagent.controller;

import com.aipintuan.voiceagent.entity.OrderEntity;
import com.aipintuan.voiceagent.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/mine")
    public List<OrderEntity> mine() {
        return orderService.listMine();
    }

    @GetMapping("/{orderId}")
    public OrderEntity detail(@PathVariable Long orderId) {
        return orderService.getForUser(orderId);
    }
}