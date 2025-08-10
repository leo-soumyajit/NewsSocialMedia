package com.soumyajit.news.social.media.app.EmailService;

import com.soumyajit.news.social.media.app.Advices.ApiError;
import com.soumyajit.news.social.media.app.Advices.ApiResponse;
import com.soumyajit.news.social.media.app.Entities.User;
import com.soumyajit.news.social.media.app.Service.UserServiceImpl;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
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
                sendPasswordResetSuccessEmail(email.get());
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

    @Async
    private void sendPasswordResetSuccessEmail(String to) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlTemplate = """
        <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                <div style="max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 10px; padding: 30px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);">
                    <h2 style="color: #4A90E2; text-align: center;">✅ Password Reset Successful</h2>
                    <p style="font-size: 16px; color: #333;">Hi there,</p>
                    <p style="font-size: 15px; color: #333;">
                        Your password has been successfully reset. You can now log in using your new password.
                    </p>
                    <p style="font-size: 15px; color: #333;">
                        If you didn’t perform this action, please contact our support team immediately.
                    </p>
                    <hr style="margin: 20px 0;">
                    <p style="font-size: 13px; color: #888; text-align: center;">
                        Stay secure and thank you for using THE PEOPLE'S PRESS.
                    </p>
                    <p style="text-align: center; font-size: 14px; color: #999;">&copy; 2025 THE PEOPLE'S PRESS. All rights reserved.</p>
                </div>
            </body>
        </html>
        """;

            helper.setTo(to);
            helper.setSubject("✅ Your Password Was Successfully Reset");
            helper.setText(htmlTemplate, true);

            mailSender.send(message);
            System.out.println("✅ Password reset success email sent to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send password reset success email: " + e.getMessage());
            e.printStackTrace();
        }
    }




}