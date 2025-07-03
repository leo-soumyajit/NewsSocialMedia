package com.soumyajit.news.social.media.app.Security;

import com.soumyajit.news.social.media.app.Dtos.LoginResponseDTO;
import com.soumyajit.news.social.media.app.Exception.ResourceNotFound;
import com.soumyajit.news.social.media.app.Dtos.LoginDTOS;
import com.soumyajit.news.social.media.app.Dtos.SignUpRequestDTOS;
import com.soumyajit.news.social.media.app.Dtos.UserDTOS;
import com.soumyajit.news.social.media.app.Entities.Enums.Roles;
import com.soumyajit.news.social.media.app.Entities.User;
import com.soumyajit.news.social.media.app.Repository.UserRepository;
import com.soumyajit.news.social.media.app.Security.OTPServiceAndValidation.OtpService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final OtpService otpService;
    @Autowired
    private JavaMailSender mailSender;


    //signup function with otp validation

    public UserDTOS signUp(SignUpRequestDTOS signUpRequestDTOS){  // signUp method for user
        Optional<User> user = userRepository.findByEmail(signUpRequestDTOS.getEmail());
        if (user.isPresent()) {
            throw new BadCredentialsException("User with this Email is already present");
        }

        // Check if OTP was verified
        if (!otpService.isOTPVerified(signUpRequestDTOS.getEmail())) {
            throw new BadCredentialsException("OTP not verified");
        }

        User newUser = modelMapper.map(signUpRequestDTOS, User.class);
        newUser.setRoles(Set.of(Roles.USER)); // by default all users are USER
        newUser.setPassword(passwordEncoder.encode(signUpRequestDTOS.getPassword())); // bcrypt the password
        User savedUser = userRepository.save(newUser); // save the user

        // Clear OTP verification status after successful signup
        otpService.clearOTPVerified(signUpRequestDTOS.getEmail());

        sendWelcomeEmail(signUpRequestDTOS);
        return modelMapper.map(savedUser, UserDTOS.class);

    }


    public LoginResponseDTO login(LoginDTOS loginDTOS){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTOS.getEmail(),loginDTOS.getPassword())
        );

        User userEntities = (User) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(userEntities);
        String refreshToken = jwtService.generateRefreshToken(userEntities);

        return new LoginResponseDTO(userEntities.getId(),accessToken,refreshToken);

    }
    public String refreshToken(String refreshToken) { //refreshToken method
        Long uerId = jwtService.getUserIdFromToken(refreshToken);  //refresh token is valid
        User user = userRepository.findById(uerId)
                .orElseThrow(()->
                        new ResourceNotFound("User not found with id : "+uerId));
        return jwtService.generateAccessToken(user);


    }

    @Async
    private void sendWelcomeEmail(SignUpRequestDTOS signUpRequestDTOS) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlTemplate = """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                .email-container {
                    font-family: Arial, sans-serif;
                    max-width: 600px;
                    margin: auto;
                    background: #ffffff;
                    border-radius: 12px;
                    box-shadow: 0 5px 20px rgba(0,0,0,0.05);
                    padding: 30px;
                    color: #333333;
                }
                .header {
                    text-align: center;
                    color: #4A90E2;
                }
                .button {
                    display: inline-block;
                    padding: 12px 24px;
                    background: linear-gradient(to right, #4A90E2, #357ABD);
                    color: white;
                    text-decoration: none;
                    border-radius: 8px;
                    margin-top: 20px;
                    font-weight: bold;
                }
                .footer {
                    font-size: 12px;
                    color: #999999;
                    text-align: center;
                    margin-top: 40px;
                    border-top: 1px solid #eeeeee;
                    padding-top: 20px;
                }
            </style>
        </head>
        <body style="background-color:#f5f7fb;">
            <div class="email-container">
                <h2 class="header">🎉 Welcome to Newsly!</h2>
                <p style="text-align:center; font-size: 16px;">Your social hub for the latest news 📰</p>

                <p>Hi <strong>{{name}}</strong>,</p>

                <p>Welcome to <strong>Newsly</strong>! We're happy to have you on board. This is a place to discover and share the latest updates happening around the world.</p>

                <ul>
                    <li>📰 Post and discover current events</li>
                    <li>📢 Share your views</li>
                    <li>👥 Connect with the community</li>
                </ul>

                <p>Start your journey now:</p>
                <a class="button" href="https://newsly.com">🌐 Explore Newsly</a>

                <p style="margin-top: 30px;">Cheers,<br>The Newsly Team 💙</p>

                <div class="footer">
                    You received this email because you joined Newsly.<br>
                    If you didn’t sign up, you can safely ignore this message.
                </div>
            </div>
        </body>
        </html>
        """;

            String htmlContent = htmlTemplate.replace("{{name}}", signUpRequestDTOS.getName());

            helper.setTo(signUpRequestDTOS.getEmail());
            helper.setSubject("🎉 Welcome to Newsly!");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("✅ Welcome email sent successfully to: " + signUpRequestDTOS.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Failed to send welcome email: " + e.getMessage());
            e.printStackTrace();
        }
    }




}
