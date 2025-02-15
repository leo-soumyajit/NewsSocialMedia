package com.soumyajit.news.social.media.app.Dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserDTOS {
    private Long id;
    private String email;
    private String name;
}
