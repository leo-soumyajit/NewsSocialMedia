package com.soumyajit.news.social.media.app.Dtos;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.soumyajit.news.social.media.app.Entities.Comments;
import com.soumyajit.news.social.media.app.Entities.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

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
    @JsonIgnore
    private User user;
    private Long likes;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private List<CommentDtos> comments;
}