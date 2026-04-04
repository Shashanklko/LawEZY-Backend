package com.LawEZY.chat.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.LawEZY.chat.dto.ChatMessageResponse;
import com.LawEZY.chat.dto.SendMessageRequest;
import com.LawEZY.chat.dto.StartChatRequest;
import com.LawEZY.chat.enums.ChatStatus;
import com.LawEZY.chat.model.ChatMessage;
import com.LawEZY.chat.model.ChatSession;
import com.LawEZY.chat.repository.ChatMessageRepository;
import com.LawEZY.chat.repository.ChatSessionRepository;
import com.LawEZY.user.repository.ProfessionalProfileRepository;
import com.LawEZY.user.repository.UserRepository;

import com.LawEZY.common.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatServiceImp implements ChatService {

    @Autowired
    private ChatSessionRepository chatSessionRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired 
    private UserRepository userRepository;
    @Autowired 
    private ProfessionalProfileRepository professionalProfileRepository;


    @Override
    @NonNull
    public ChatSession startSession(@NonNull StartChatRequest request) {
        ChatSession session = new ChatSession();
        session.setUserId(request.getUserId());
        session.setProfessionalId(request.getProfessionalId());
        session.setStatus(ChatStatus.AWAITING_REPLY);
        
        session.setTokensGranted(0);
        session.setTokensConsumed(0);
        session.setCreatedAt(LocalDateTime.now());
        session.setLastUpdateAt(LocalDateTime.now());

        return chatSessionRepository.save(session);
    }

    @Override
    @NonNull
    public ChatMessageResponse sendMessage(@NonNull SendMessageRequest request) {
        String sId = request.getChatSessionId();
        if (sId == null) throw new RuntimeException("Session ID missing");

        ChatSession session = chatSessionRepository.findById(sId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat Session not found"));

        String content = request.getContent();
        if (content != null && (content.matches(".*\\d{10}.*") || content.toLowerCase().contains("call me"))) {
            throw new RuntimeException("Sharing contact details is blocked for security.");
        }

        ChatMessage message = new ChatMessage();
        message.setChatSessionId(sId);
        message.setSenderId(request.getSenderId());
        message.setReceiverId(request.getReceiverId());
        message.setContent(content);
        message.setType(request.getType());
        message.setTimestamp(LocalDateTime.now());

        // Token logic for User messages
        Long senderUserId = request.getSenderId();
        if (senderUserId != null && senderUserId.equals(session.getUserId())) {
            com.LawEZY.user.entity.User user = userRepository.findById(senderUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            Integer balance = user.getGlobalTokenBalance();
            if (balance == null || balance <= 0) {
                throw new RuntimeException("Insufficient tokens. Please unlock this reply to continue.");
            }
            
            user.setGlobalTokenBalance(balance - 1);
            Integer consumedCount = session.getTokensConsumed();
            session.setTokensConsumed(consumedCount != null ? consumedCount + 1 : 1);
            userRepository.save(user);
            log.info("Token deducted for User ID: {}. Remaining tokens: {}", user.getId(), user.getGlobalTokenBalance());
        }

        // Locked reply logic for Lawyer's initial response
        if (senderUserId != null && senderUserId.equals(session.getProfessionalId()) && session.getStatus() == ChatStatus.AWAITING_REPLY) {
            message.setIsLocked(true);
            session.setStatus(ChatStatus.LOCKED_REPLY);
        }

        session.setLastUpdateAt(LocalDateTime.now());
        chatSessionRepository.save(session);
        
        ChatMessage savedMsg = chatMessageRepository.save(message);
        return mapToResponse(savedMsg, session.getStatus());
    }

    @Override
    @NonNull
    public List<ChatMessageResponse> getChatHistory(@NonNull String chatSessionId) {
        ChatSession session = chatSessionRepository.findById(chatSessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        
        return chatMessageRepository.findByChatSessionIdOrderByTimestampAsc(chatSessionId)
                .stream()
                .map(msg -> mapToResponse(msg, session.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public void endChatByUser(@NonNull String sId) {
        ChatSession session = chatSessionRepository.findById(sId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        session.setStatus(ChatStatus.RESOLVED);
        
        // ROLLOVER LOGIC
        Integer granted = session.getTokensGranted();
        Integer consumed = session.getTokensConsumed();
        int leftover = (granted != null ? granted : 0) - (consumed != null ? consumed : 0);
        
        if (leftover > 0) {
            Long userId = session.getUserId();
            if (userId != null) {
                userRepository.findById(userId).ifPresent(user -> {
                    Integer balance = user.getGlobalTokenBalance();
                    user.setGlobalTokenBalance(balance != null ? balance + leftover : leftover);
                    userRepository.save(user);
                });
            }
        }
        
        chatSessionRepository.save(session);
    }

    @Override
    public void endChatByProfessional(@NonNull String sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        
        session.setProfessionalEndedChat(true);
        session.setStatus(ChatStatus.PENDING_RESOLUTION);
        chatSessionRepository.save(session);
    }

    @Override
    public void unlockReply(@NonNull String sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        if (session.getStatus() == ChatStatus.LOCKED_REPLY) {
            session.setStatus(ChatStatus.ACTIVE);
            Integer grantedCount = session.getTokensGranted();
            session.setTokensGranted(grantedCount != null ? grantedCount + 10 : 10);
            
            Long professionalId = session.getProfessionalId();
            if (professionalId != null) {
                professionalProfileRepository.findByUserId(professionalId).ifPresent(prof -> {
                    Double fee = prof.getChatUnlockFee();
                    double earnings = (fee != null ? fee : 99.0) * 0.8;
                    Double currentBalance = prof.getWalletBalance();
                    prof.setWalletBalance(currentBalance != null ? currentBalance + earnings : earnings);
                    professionalProfileRepository.save(prof);
                    log.info("Professional ID: {} credited with earnings: {} for Session ID: {}", professionalId, earnings, sessionId);
                });
            }

            chatSessionRepository.save(session);
            log.info("Chat Session ID: {} unlocked and set to ACTIVE.", sessionId);
        }
    }

    @NonNull
    private ChatMessageResponse mapToResponse(@NonNull ChatMessage msg, ChatStatus status) {
        ChatMessageResponse res = new ChatMessageResponse();
        res.setId(msg.getId());
        res.setChatSessionId(msg.getChatSessionId());
        res.setSenderId(msg.getSenderId());
        res.setReceiverId(msg.getReceiverId());
        res.setType(msg.getType());
        res.setContent(msg.getContent());
        res.setIsLocked(msg.getIsLocked());
        res.setTimestamp(msg.getTimestamp());
        res.setStatus(status);
        return res;
    }
}
