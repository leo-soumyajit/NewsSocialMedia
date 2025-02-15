package com.soumyajit.news.social.media.app.Dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginDTOS {
    private String email;
    private String password;
}
