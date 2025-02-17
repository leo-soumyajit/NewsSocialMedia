package com.soumyajit.news.social.media.app.Service;

import com.soumyajit.news.social.media.app.Dtos.UserProfileDTOS;
import com.soumyajit.news.social.media.app.Entities.User;
import com.soumyajit.news.social.media.app.Exception.ResourceNotFound;
import com.soumyajit.news.social.media.app.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService{
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Transactional
    public UserProfileDTOS updateUserProfile(Long userId, Map<String, Object> updates) {
        // Get the currently authenticated user
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Fetch the user profile to be updated
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFound("User not found with this id "+userId));

        // Check if the authenticated user is the owner of the profile
        if (!user.getId().equals(authenticatedUser.getId())) {
            throw new RuntimeException("This is not your profile you cant update this profile");
        }

        List<String> restrictedFields = Arrays.asList("email", "password");

        // Update the profile with the provided updates using ReflectionUtils
        updates.forEach((key, value) -> {
            if (restrictedFields.contains(key)) {
                throw new IllegalArgumentException("email " + "password" + " cannot be updated through this method");
            }

            Field fieldTobeSaved = org.springframework.data.util.ReflectionUtils.findRequiredField(User.class,key);
            fieldTobeSaved.setAccessible(true);
            ReflectionUtils.setField(fieldTobeSaved,user,value);
        });

        // Save the updated user profile
        User updatedUser = userRepository.save(user);

        return modelMapper.map(updatedUser,UserProfileDTOS.class);

    }

    @Override
    public UserProfileDTOS getUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFound("User not found with this id "+userId));
        return modelMapper.map(user,UserProfileDTOS.class);
    }

    @Override
    public List<UserProfileDTOS> searchUserByName(String name) {
        List<User> user = userRepository.findByNameContainingIgnoreCase(name);
        return user.stream()
                .map(user1 -> modelMapper.map(user1, UserProfileDTOS.class))
                .collect(Collectors.toList());
    }

}
