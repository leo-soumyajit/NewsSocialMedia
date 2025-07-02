package com.soumyajit.news.social.media.app.controller;

import com.soumyajit.news.social.media.app.Dtos.UserProfileDTOS;
import com.soumyajit.news.social.media.app.Service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user-profile")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;



    @GetMapping()
    public ResponseEntity<UserProfileDTOS> getUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userProfileService.getCurrentUserProfile(username));
    }




    @PatchMapping(value = "/update",consumes = {"multipart/form-data"})
    public ResponseEntity<UserProfileDTOS> updateUserProfile(@RequestParam Map<String, Object> updates,
                                                             @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture) throws IOException {
        return ResponseEntity.ok(userProfileService.updateUserProfile(updates, profilePicture));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserProfileDTOS>>searchUserByName(@RequestParam String name){
        return ResponseEntity.ok(userProfileService.searchUserByName(name));
    }



}














