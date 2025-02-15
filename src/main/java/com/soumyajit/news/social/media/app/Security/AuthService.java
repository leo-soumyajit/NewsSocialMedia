package com.soumyajit.news.social.media.app.Security;

import com.soumyajit.news.social.media.app.Exception.ResourceNotFound;
import com.soumyajit.news.social.media.app.Dtos.LoginDTOS;
import com.soumyajit.news.social.media.app.Dtos.SignUpRequestDTOS;
import com.soumyajit.news.social.media.app.Dtos.UserDTOS;
import com.soumyajit.news.social.media.app.Entities.Enums.Roles;
import com.soumyajit.news.social.media.app.Entities.User;
import com.soumyajit.news.social.media.app.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

    public UserDTOS signUp(SignUpRequestDTOS signUpRequestDTOS){  // signUp method for user
        Optional<User> user = userRepository.findByEmail(signUpRequestDTOS.getEmail());
        if(user.isPresent()){
            throw new BadCredentialsException("User with this Email is already present");
        }
        User newUser = modelMapper.map(signUpRequestDTOS,User.class);
        newUser.setRoles(Set.of(Roles.USER)); // by default all user are guests
        newUser.setPassword(passwordEncoder.encode(signUpRequestDTOS.getPassword())); // bcrypt the pass
        User savedUser = userRepository.save(newUser); // save the user
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

}
