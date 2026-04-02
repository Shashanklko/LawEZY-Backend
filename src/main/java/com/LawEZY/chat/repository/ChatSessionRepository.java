package com.LawEZY.chat.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.LawEZY.chat.enums.ChatStatus;
import com.LawEZY.chat.model.ChatSession;


public interface ChatSessionRepository extends MongoRepository<ChatSession, String>{

    List<ChatSession> findByUserIdAndStatus(Long userId, ChatStatus status);
    List<ChatSession> findByLawyerId(Long lawyerId);
}
