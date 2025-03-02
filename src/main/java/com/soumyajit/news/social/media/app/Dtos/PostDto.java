package com.soumyajit.news.social.media.app.Dtos;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
public class PostDto {
    private Long id;
    private String title;
    private String description;
    private List<String> images;
    private String userName;
    private Long userId;
    private Long likes;
    private LocalDateTime createdAt;
    private List<CommentDtos> comments;
}