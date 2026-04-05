package com.ccp.WorkBridge.controllers;

import com.ccp.WorkBridge.dto.CreateOrderRequest;
import com.ccp.WorkBridge.dto.CustomUserDetails;
import com.ccp.WorkBridge.dto.OrderResponse;
import com.ccp.WorkBridge.models.FileEntity;
import com.ccp.WorkBridge.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public OrderResponse create(@RequestBody CreateOrderRequest createOrderRequest,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        return OrderResponse.toResponse(orderService.createOrder(createOrderRequest, userDetails.user()));
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/respond/{id}")
    public OrderResponse respondToOrder(@PathVariable Long id,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        return OrderResponse.toResponse(orderService.respondToOrder(id, userDetails.user()));
    }

}
