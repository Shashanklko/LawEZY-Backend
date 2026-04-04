package com.LawEZY.blog.dto;

import com.LawEZY.blog.enums.PostType;
import lombok.Data;

@Data
public class PostRequest {
    private Long authorId;
    private String title;
    private String content;
    private PostType type;
}
