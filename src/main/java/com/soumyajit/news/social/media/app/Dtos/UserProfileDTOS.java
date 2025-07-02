package com.soumyajit.news.social.media.app.Dtos;

import lombok.Data;

import java.util.List;

@Data
public class UserProfileDTOS {
    private Long id;
    private String userName;
    private String email;
    private List<ProfilePostDTOS> postsList;
    private String bio;
    private String profileImage;

}
