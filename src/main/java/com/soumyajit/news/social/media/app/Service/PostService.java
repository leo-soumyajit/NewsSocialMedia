package com.soumyajit.news.social.media.app.Service;

import com.soumyajit.news.social.media.app.Dtos.CommentDtos;
import com.soumyajit.news.social.media.app.Dtos.CommentRequestDtos;
import com.soumyajit.news.social.media.app.Dtos.PostDto;
import com.soumyajit.news.social.media.app.Dtos.PostRequestDtos;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PostService {
    PostDto createPost(PostRequestDtos postDto);

    PostDto getPostById(Long postId);

    List<PostDto> getAllPosts();

    PostDto postLikeById(Long postId);

    PostDto updatePostById(Long postId,PostRequestDtos postRequestDtos);

    void deletePostById(Long postId);


    List<PostDto> searchPosts(String keyword);
}
