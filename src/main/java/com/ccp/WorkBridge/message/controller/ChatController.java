package com.ccp.WorkBridge.message.controller;

import com.ccp.WorkBridge.dto.CustomUserDetails;
import com.ccp.WorkBridge.message.Message;
import com.ccp.WorkBridge.message.service.MessageService;
import com.ccp.WorkBridge.message.dto.SendMessageRequest;
import com.ccp.WorkBridge.message.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/order/{orderId}/send")
    public void sendMessage(
            @DestinationVariable Long orderId,
            SendMessageRequest request,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {

        Message savedMessage = messageService.createMessage(
                orderId,
                principal.user(),
                request.content()
        );

        messagingTemplate.convertAndSend(
                "/topic/order/" + orderId,
                MessageResponse.from(savedMessage)
        );
    }

    @GetMapping("/orders/{orderId}/messages")
    public List<MessageResponse> getHistory(@PathVariable Long orderId) {
        return messageService.getOrderMessages(orderId)
                .stream()
                .map(MessageResponse::from)
                .toList();
    }
}
