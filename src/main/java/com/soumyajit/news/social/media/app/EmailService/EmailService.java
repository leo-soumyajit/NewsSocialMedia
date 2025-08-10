package com.soumyajit.news.social.media.app.EmailService;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendPasswordResetEmail(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlTemplate = """
        <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                <div style="max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 10px; padding: 30px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);">
                    <h2 style="color: #4A90E2; text-align: center;">🔐 Password Reset Request</h2>
                    <p style="font-size: 16px; color: #333;">Hi there,</p>
                    <p style="font-size: 15px; color: #333;">
                        We received a request to reset your password. Please use the code below to proceed:
                    </p>
                    <div style="font-size: 20px; font-weight: bold; color: #4A90E2; text-align: center; margin: 20px 0;">
                        ${code}
                    </div>
                    <p style="font-size: 14px; color: #666; text-align: center;">
                        ⏳ This code is valid for 30 minutes.
                    </p>
                    <hr style="margin: 20px 0;">
                    <p style="font-size: 13px; color: #888; text-align: center;">
                        Didn't request this? You can safely ignore this message.
                    </p>
                    <p style="text-align: center; font-size: 14px; color: #999;">&copy; 2025 THE PEOPLE'S PRESS. All rights reserved.</p>
                </div>
            </body>
        </html>
        """;

            String htmlContent = htmlTemplate.replace("${code}", code);

            helper.setTo(to);
            helper.setSubject("🔐 Password Reset Code");
            helper.setText(htmlContent, true); // true enables HTML content

            mailSender.send(message);
            System.out.println("✅ Password reset email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send password reset email: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp);
        mailSender.send(message);
    }
}
