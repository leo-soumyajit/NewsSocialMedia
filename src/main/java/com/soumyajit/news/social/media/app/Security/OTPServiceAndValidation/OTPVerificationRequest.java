package com.soumyajit.news.social.media.app.Security.OTPServiceAndValidation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTPVerificationRequest {

    private String email;
    private String otp;

}
