package com.soumyajit.news.social.media.app.Dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class PostRequestDtos {
    private String title;
    private String description;
}
