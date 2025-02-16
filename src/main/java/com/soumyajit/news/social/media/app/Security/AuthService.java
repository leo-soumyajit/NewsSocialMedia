package com.soumyajit.news.social.media.app.Security;

import com.soumyajit.news.social.media.app.Exception.ResourceNotFound;
import com.soumyajit.news.social.media.app.Dtos.LoginDTOS;
import com.soumyajit.news.social.media.app.Dtos.SignUpRequestDTOS;
import com.soumyajit.news.social.media.app.Dtos.UserDTOS;
import com.soumyajit.news.social.media.app.Entities.Enums.Roles;
import com.soumyajit.news.social.media.app.Entities.User;
import com.soumyajit.news.social.media.app.Repository.UserRepository;
import com.soumyajit.news.social.media.app.Security.OTPServiceAndValidation.OtpService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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


    public String[] login(LoginDTOS loginDTOS){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTOS.getEmail(),loginDTOS.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        String[] arr = new String[2];
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        arr[0] = accessToken; //1st one is accessToken
        arr[1] = refreshToken; // 2nd one is refreshToken
        return arr;

    }
    public String refreshToken(String refreshToken) { //refreshToken method
        Long uerId = jwtService.getUserIdFromToken(refreshToken);  //refresh token is valid
        User user = userRepository.findById(uerId)
                .orElseThrow(()->
                        new ResourceNotFound("User not found with id : "+uerId));
        return jwtService.generateAccessToken(user);


    }

    private void sendWelcomeEmail(SignUpRequestDTOS signUpRequestDTOS){
        //Send welcome message to user
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(signUpRequestDTOS.getEmail());
        message.setSubject("Welcome Message");
        message.setText("Welcome To Our Website Dear "+signUpRequestDTOS.getName());
        mailSender.send(message);
    }

}
