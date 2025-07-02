package com.soumyajit.news.social.media.app.Service;

import com.soumyajit.news.social.media.app.Dtos.UserProfileDTOS;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UserProfileService {
    UserProfileDTOS updateUserProfile(Map<String, Object> updates, MultipartFile image) throws IOException;

    public UserProfileDTOS getCurrentUserProfile(String username);

    List<UserProfileDTOS> searchUserByName(String name);
}
