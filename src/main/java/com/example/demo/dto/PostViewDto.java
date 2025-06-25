// src/main/java/com/example/demo/dto/PostViewDto.java
package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostViewDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private UserViewDto author;
    private int likeCount = 0;
    private int commentCount = 0;
}