package com.soumyajit.news.social.media.app.Dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CommentRequestDtos {
    private Long id;
    private String content;
}
