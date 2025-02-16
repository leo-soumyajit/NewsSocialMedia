package com.soumyajit.news.social.media.app.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SignUpRequestDTOS {
    @NotNull
    @Size(min=2, max=30)
    private String name;

    @Email
    private String email;

    private String password;
}
