package com.soumyajit.news.social.media.app.Dtos;

import com.soumyajit.news.social.media.app.Entities.Post;
import com.soumyajit.news.social.media.app.Entities.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
public class UserProfileDTOS {
    private Long id;
    private String userName;
    private List<ProfilePostDTOS> postsList;
    private String bio;
    private String profileImage;

}
