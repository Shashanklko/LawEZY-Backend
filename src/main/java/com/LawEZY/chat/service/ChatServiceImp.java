package com.LawEZY.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LawEZY.chat.repository.ChatMessageRepository;
import com.LawEZY.chat.repository.ChatSessionRepository;
import com.LawEZY.user.repository.LawyerProfileRepository;
import com.LawEZY.user.repository.UserRepository;

@Service
public class ChatServiceImp implements ChatService {

@Autowired
private ChatSessionRepository chatSessionRepository;
@Autowired
private ChatMessageRepository chatMessageRepository;
@Autowired 
private UserRepository userRepository;
@Autowired 
private LawyerProfileRepository lawyerProfileRepository;
    
}
