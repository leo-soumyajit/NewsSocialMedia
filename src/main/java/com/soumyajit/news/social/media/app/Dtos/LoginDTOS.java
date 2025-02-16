package com.soumyajit.news.social.media.app.Dtos;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginDTOS {

    @Email
    private String email;
    private String password;
}
