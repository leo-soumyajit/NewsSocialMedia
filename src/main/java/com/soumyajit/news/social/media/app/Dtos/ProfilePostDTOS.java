package com.soumyajit.news.social.media.app.Dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProfilePostDTOS {
    private Long id;
    private String title;
    private String description;
    private List<String> images;
    private Long likes;
    private LocalDateTime createdAt;
    private List<CommentDtos> comments;
}
