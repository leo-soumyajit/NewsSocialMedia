package com.soumyajit.news.social.media.app.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.soumyajit.news.social.media.app.Dtos.UserProfileDTOS;
import com.soumyajit.news.social.media.app.Entities.User;
import com.soumyajit.news.social.media.app.Exception.ResourceNotFound;
import com.soumyajit.news.social.media.app.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final Cloudinary cloudinary;


    @CacheEvict(value = "userProfiles", key = "#result.email", condition = "#result != null")
    public UserProfileDTOS updateUserProfile(Map<String, Object> updates, MultipartFile profilePicture) throws IOException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<String> restrictedFields = Arrays.asList("email", "password");

        updates.forEach((key, value) -> {
            if (restrictedFields.contains(key)) {
                throw new IllegalArgumentException("email and password cannot be updated through this method");
            }
            Field fieldToBeSaved = org.springframework.data.util.ReflectionUtils.findRequiredField(User.class, key);
            fieldToBeSaved.setAccessible(true);
            ReflectionUtils.setField(fieldToBeSaved, user, value);
        });

        if (profilePicture != null && !profilePicture.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(profilePicture.getBytes(), ObjectUtils.emptyMap());
            String profilePictureUrl = uploadResult.get("url").toString();
            user.setProfileImage(profilePictureUrl);
        }

        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserProfileDTOS.class);
    }

    @Override
    @Cacheable(value = "userProfiles", key = "#username")
    public UserProfileDTOS getCurrentUserProfile(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFound("User not found with username " + username));
        return modelMapper.map(user, UserProfileDTOS.class);
    }


    @Override
    public List<UserProfileDTOS> searchUserByName(String name) {
        List<User> user = userRepository.findByNameContainingIgnoreCase(name);
        return user.stream()
                .map(user1 -> modelMapper.map(user1, UserProfileDTOS.class))
                .collect(Collectors.toList());
    }

}
