package com.soumyajit.news.social.media.app.Service;

import com.soumyajit.news.social.media.app.Dtos.PostDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    PostDto createPost(String title ,  String description ,List<MultipartFile> imageFiles, String category) throws IOException;

    PostDto getPostById(Long postId);

    List<PostDto> getAllPosts();

    PostDto postLikeById(Long postId);

    PostDto updatePostById(Long postId,String title ,  String description) throws IOException;

    void deletePostById(Long postId);


    List<PostDto> searchPosts(String keyword);

    PostDto removeLikeById(Long postId);

    List<PostDto> getPostsByCategory(String category);
}
