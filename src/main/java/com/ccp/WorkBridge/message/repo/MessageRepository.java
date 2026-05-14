package com.ccp.WorkBridge.message.repo;

import com.ccp.WorkBridge.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {


    List<Message> findByOrder_IdOrderByCreatedAtDesc(Long orderId);
}
