package com.soumyajit.news.social.media.app.EmailService;

import com.soumyajit.news.social.media.app.Advices.ApiError;
import com.soumyajit.news.social.media.app.Advices.ApiResponse;
import com.soumyajit.news.social.media.app.Entities.User;
import com.soumyajit.news.social.media.app.Service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
@RestController
@RequestMapping("/password-reset")
public class PasswordResetController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<String>> requestPasswordReset(@RequestParam String email) {
        Optional<User> user = userService.findByEmail(email);
        ApiResponse<String> response = new ApiResponse<>();
        if (user.isPresent()) {
            String token = tokenService.createResetCode(email);
            emailService.sendPasswordResetEmail(email, token);
            response.setData("Password reset email sent");
            return ResponseEntity.ok(response);
        } else {
            ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, "User not found");
            response.setApiError(apiError);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        Optional<String> email = tokenService.getEmailByCode(token);
        ApiResponse<String> response = new ApiResponse<>();
        if (email.isPresent()) {
            Optional<User> user = userService.findByEmail(email.get());
            if (user.isPresent()) {
                userService.updateUserPassword(user.get(), newPassword); // Password encoded in service
                tokenService.invalidateCode(token);
                response.setData("Password reset successful");

                //send email that your password is Successfully reset
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email.get());
                message.setSubject("Successfully reset password");
                message.setText("Your password is successfully reset now you can login with your new password ");
                mailSender.send(message);

                return ResponseEntity.ok(response);

            } else {
                ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, "User not found");
                response.setApiError(apiError);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } else {
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Invalid token");
            response.setApiError(apiError);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }



}