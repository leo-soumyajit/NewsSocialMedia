package com.soumyajit.news.social.media.app.Service;

import com.soumyajit.news.social.media.app.Dtos.UserProfileDTOS;

import java.util.Map;

public interface UserProfileService {
    UserProfileDTOS updateUserProfile(Long userId, Map<String, Object> updates);

    UserProfileDTOS getUserProfile(Long userId);
}
