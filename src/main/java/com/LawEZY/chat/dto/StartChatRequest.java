package com.LawEZY.chat.dto;

import lombok.Data;

@Data
public class StartChatRequest {
    private Long userId;
    private Long lawyerId;
}
