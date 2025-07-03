package com.soumyajit.news.social.media.app.Security.OTPServiceAndValidation;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.util.HashMap;
import java.util.Map;


@Service
public class OtpService {

    @Autowired
    private JavaMailSender emailSender;

    private final Map<String, OTPDetails> otpStorage = new HashMap<>();
    private final Map<String, Boolean> otpVerified = new HashMap<>();

    public String generateOTP() {
        int otp = (int)(Math.random() * 9000) + 1000;
        return String.valueOf(otp);
    }

    @Async
    public void sendOTPEmail(String email, String otp) {
        try {
            // Validate email address format
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("newssocialmedia2025@gmail.com");
            helper.setTo(email);
            helper.setSubject("🔐 Your OTP for Newsly");

            String htmlContent = """
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f5f7fa; padding: 20px; color: #333;">
                <div style="max-width: 600px; margin: auto; background: #ffffff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05);">
                    <div style="background-color: #1e88e5; padding: 20px; border-radius: 8px 8px 0 0; color: white;">
                        <h2 style="margin: 0;">📰 Welcome to Newsly</h2>
                        <p style="margin: 0;">Your daily dose of real-time news & opinions.</p>
                    </div>
                    <div style="padding: 30px;">
                        <p>Hello there 👋,</p>
                        <p>Use the following One-Time Password (OTP) to verify your account:</p>
                        <div style="background-color: #f0f4f8; padding: 15px; text-align: center; font-size: 24px; font-weight: bold; letter-spacing: 2px; color: #1e88e5; border-radius: 6px;">
                            %s
                        </div>
                        <p style="margin-top: 20px;">⏱ <strong>This OTP is valid for only 10 minutes.</strong></p>
                        <p>If you didn’t request this, you can safely ignore this email.</p>
                        <hr style="margin: 30px 0; border: none; border-top: 1px solid #ddd;">
                        <p style="font-size: 12px; color: #888;">
                            You’re receiving this email because your email was used to register on <strong>Newsly</strong>.
                        </p>
                        <p style="font-size: 12px; color: #888;">Need help? Contact us at newssocialmedia2025@gmail.com</p>
                    </div>
                </div>
            </body>
            </html>
        """.formatted(otp);

            helper.setText(htmlContent, true); // true = HTML

            emailSender.send(message);
            System.out.println("✅ OTP email sent successfully to: " + email);
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send OTP: " + e.getMessage());
            e.printStackTrace();
        } catch (jakarta.mail.MessagingException e) {
            System.err.println("❌ Invalid email address: " + e.getMessage());
        }
    }


    public void saveOTP(String email, String otp) {
        otpStorage.put(email, new OTPDetails(otp, System.currentTimeMillis()));
        otpVerified.put(email, false); // Set OTP verification status to false
    }

    public OTPDetails getOTPDetails(String email) {
        return otpStorage.get(email);
    }

    public void deleteOTP(String email) {
        otpStorage.remove(email);
        // Do not remove verification status here to retain it for signup
    }

    public void clearOTPVerified(String email) {
        otpVerified.remove(email); // Clear OTP verification status
    }

    public boolean isOTPExpired(long timestamp) {
        long currentTime = System.currentTimeMillis();
        return (currentTime - timestamp) > (10 * 60 * 1000); // 10 minutes in milliseconds
    }

    public void markOTPVerified(String email) {
        otpVerified.put(email, true); // Mark OTP as verified
    }

    public boolean isOTPVerified(String email) {
        return otpVerified.getOrDefault(email, false);
    }
}
