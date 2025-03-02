package com.soumyajit.news.social.media.app.Service;

import com.soumyajit.news.social.media.app.Dtos.PostDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    PostDto createPost(String title ,  String description ,List<MultipartFile> imageFiles) throws IOException;

    PostDto getPostById(Long postId);

    List<PostDto> getAllPosts();

    PostDto postLikeById(Long postId);

    PostDto updatePostById(Long postId,String title ,  String description,List<MultipartFile> imageFiles) throws IOException;

    void deletePostById(Long postId);


    List<PostDto> searchPosts(String keyword);

    PostDto removeLikeById(Long postId);
}
