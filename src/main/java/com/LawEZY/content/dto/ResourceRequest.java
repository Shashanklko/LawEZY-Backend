package com.LawEZY.content.dto;

import lombok.Data;

@Data
public class ResourceRequest {
    private String title;
    private String content;
    private String category;
    private Long authorId;
}
