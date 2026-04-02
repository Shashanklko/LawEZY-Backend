package com.LawEZY.chat.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.LawEZY.chat.enums.ChatStatus;

import lombok.Data;

@Data
@Document(collection = "chat_sessions")
public class ChatSession {
    @Id
    private String id;
    
    private Long userId;
    private Long lawyerId;
    private ChatStatus status;
    private Integer tokensGranted;
    private Integer tokensConsumed;
    private Boolean lawyerEndedChat;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdateAt;
    
}
