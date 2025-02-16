package com.soumyajit.news.social.media.app.Service;

import com.soumyajit.news.social.media.app.Dtos.CommentDtos;
import com.soumyajit.news.social.media.app.Dtos.CommentRequestDtos;
import com.soumyajit.news.social.media.app.Dtos.PostDto;
import com.soumyajit.news.social.media.app.Dtos.PostRequestDtos;
import com.soumyajit.news.social.media.app.Entities.Post;
import com.soumyajit.news.social.media.app.Entities.User;
import com.soumyajit.news.social.media.app.Exception.ResourceNotFound;
import com.soumyajit.news.social.media.app.Exception.UnAuthorizedException;
import com.soumyajit.news.social.media.app.Repository.PostRepository;
import com.soumyajit.news.social.media.app.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PostDto createPost(PostRequestDtos postRequestDtos) {
        log.info("Creating new Post with title: {}", postRequestDtos.getTitle());

        // Map PostRequestDtos to Post entity
        Post post = modelMapper.map(postRequestDtos, Post.class);
        post.setLikes(0L);
        post.setComments(new ArrayList<>()); // Initialize as an empty list

        // Get the currently authenticated user
        User user = getCurrentUserWithPosts(); // Fetch user with posts collection
        post.setUser_id(user);

        // Save the post
        Post savedPost = postRepository.save(post);

        return modelMapper.map(savedPost, PostDto.class);
    }

    @Override
    public PostDto getPostById(Long postId) {
        log.info("Getting Post with id:{}" ,postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new ResourceNotFound("Post not found with Id : "+postId));
        return modelMapper.map(post,PostDto.class);
    }

    @Override
    public List<PostDto> getAllPosts() {
        log.info("Getting All Posts ");
        List<Post> post = postRepository.findAll();
        return post.stream().
                map(post1 -> modelMapper.map(post1, PostDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public PostDto postLikeById(Long postId) {
        log.info("Creating Like of a Post with id:{}" ,postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new ResourceNotFound("Post not found with Id : "+postId));
        post.setLikes(post.getLikes()+1);
        Post savedPost = postRepository.save(post);
        return modelMapper.map(savedPost,PostDto.class);
    }

    @Override
    @Transactional
    public PostDto updatePostById(Long postId, PostRequestDtos postRequestDtos) {
        log.info("Updating Post with id: {}", postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with Id: " + postId));

        // Get the currently authenticated user
        User user = getCurrentUser();

        if (!user.getId().equals(post.getUser_id().getId())) {
            throw new UnAuthorizedException("Post does not belong to this user with id: " + user.getId());
        }

        post.setTitle(postRequestDtos.getTitle());
        post.setDescription(postRequestDtos.getDescription());
        post.setImages(postRequestDtos.getImages());
        post.setLikes(post.getLikes());
        Post updatedPost = postRepository.save(post);

        return modelMapper.map(updatedPost, PostDto.class);
    }

    @Override
    @Transactional
    public void deletePostById(Long postId) {
        log.info("Deleting Post with id: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with Id: " + postId));

        User user = getCurrentUser();

        if (!user.getId().equals(post.getUser_id().getId())) {
            throw new UnAuthorizedException("Post does not belong to this user with id: " + user.getId());
        }

        postRepository.deleteById(postId);
    }

    @Override
    public List<PostDto> searchPosts(String keyword) {
        List<Post> posts = postRepository.
                findByTitleContainingOrDescriptionContaining(keyword,keyword);
        return posts.stream()
                .map(post -> modelMapper.map(post,PostDto.class))
                .collect(Collectors.toList());
    }


    //get Authenticated User
    private User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //this way we can get the authenticated user everyTime
    }

    @Transactional(readOnly = true)
    private User getCurrentUserWithPosts() {
        User currentUser = getCurrentUser();
        // Fetch user with posts collection initialized
        return userRepository.findByIdWithPosts(currentUser.getId()).orElseThrow(() -> new ResourceNotFound("User not found"));
    }

}
