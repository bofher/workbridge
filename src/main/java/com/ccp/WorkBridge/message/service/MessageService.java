package com.ccp.WorkBridge.message.service;

import com.ccp.WorkBridge.message.Message;
import com.ccp.WorkBridge.message.repo.MessageRepository;
import com.ccp.WorkBridge.order.Order;
import com.ccp.WorkBridge.order.repo.OrderRepository;
import com.ccp.WorkBridge.user.User;
import com.ccp.WorkBridge.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final OrderRepository orderRepository;


    public Message createMessage(Long orderId, User sender, String content) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Message message = Message.builder()
                .order(order)
                .sender(sender)
                .content(content)
                .build();

        return messageRepository.save(message);
    }

    public List<Message> getOrderMessages(Long orderId) {
        return messageRepository.findByOrder_IdOrderByCreatedAtDesc(orderId);
    }
}

