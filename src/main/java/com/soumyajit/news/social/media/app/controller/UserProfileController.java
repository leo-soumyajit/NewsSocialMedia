package com.soumyajit.news.social.media.app.controller;

import com.soumyajit.news.social.media.app.Dtos.UserProfileDTOS;
import com.soumyajit.news.social.media.app.Service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user-profile")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;


    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDTOS> getUserProfile(@PathVariable Long userId) {

        return ResponseEntity.ok(userProfileService.getUserProfile(userId));
    }



    @PatchMapping("/update/{userId}")
    public ResponseEntity<UserProfileDTOS> updateUserProfile(@PathVariable Long userId, @RequestBody Map<String, Object> updates) {

        return ResponseEntity.ok(userProfileService.updateUserProfile(userId,updates));
    }
}














