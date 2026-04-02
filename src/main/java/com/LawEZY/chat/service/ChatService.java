package com.LawEZY.chat.service;

import java.util.List;

import com.LawEZY.chat.dto.ChatMessageResponse;
import com.LawEZY.chat.dto.SendMessageRequest;
import com.LawEZY.chat.dto.StartChatRequest;
import com.LawEZY.chat.model.ChatSession;

public interface ChatService {
    
    public ChatSession startSession(StartChatRequest request);
    public ChatMessageResponse sendMessage(SendMessageRequest request);
    public List<ChatMessageResponse> getChatHistory(String chatSessionId);
    void endChatByUser(String sessionId);
    void endChatByLawyer(String sessionId);
    void unlockReply(String sessionId);
}
