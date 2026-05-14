package com.ccp.WorkBridge.message.dto;

import com.ccp.WorkBridge.message.Message;
import java.time.Instant;

public record MessageResponse(
        Long id,
        Long orderId,
        String authorName,
        String content,
        Instant createdAt
) {
    public static MessageResponse from(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getOrder().getId(),
                message.getSender().getFullName(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
