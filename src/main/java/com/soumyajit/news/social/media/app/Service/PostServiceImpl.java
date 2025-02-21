package com.soumyajit.news.social.media.app.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.soumyajit.news.social.media.app.Dtos.PostDto;
import com.soumyajit.news.social.media.app.Entities.Post;
import com.soumyajit.news.social.media.app.Entities.User;
import com.soumyajit.news.social.media.app.Exception.ResourceNotFound;
import com.soumyajit.news.social.media.app.Exception.UnAuthorizedException;
import com.soumyajit.news.social.media.app.Repository.PostRepository;
import com.soumyajit.news.social.media.app.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;


    @Override
    @Transactional
    public PostDto createPost(String title, String description, List<MultipartFile> images) throws IOException {
        List<String> imageUrls = new ArrayList<>();

        // Upload images to Cloudinary and get URLs
        if (images != null) {
            for (MultipartFile file : images) {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                String imageUrl = uploadResult.get("url").toString();
                log.info("Uploaded image URL: {}", imageUrl);
                imageUrls.add(imageUrl);
            }
        }

        // Create and populate the Post entity
        Post post = new Post();
        post.setTitle(title);
        post.setDescription(description);
        post.setLikes(0L);
        post.setComments(new ArrayList<>()); // Initialize as an empty list
        post.setImages(imageUrls); // Set image URLs

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
        log.info("Liking post with id: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with Id: " + postId));

        User user = getCurrentUser();

        // Check if the user has already liked this particular post
        if (user.getLikedPostIds().contains(postId)) {
            throw new RuntimeException("User has already liked this post");
        }

        // Add like and increment like count
        post.setLikes(post.getLikes() + 1);
        user.getLikedPostIds().add(postId);

        Post savedPost = postRepository.save(post);
        userRepository.save(user);

        return modelMapper.map(savedPost, PostDto.class);
    }


    @Override
    public PostDto removeLikeById(Long postId) {
        log.info("Removing like from post with id: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with Id: " + postId));

        User user = getCurrentUser();

        // Check if the user has not liked this particular post
        if (!user.getLikedPostIds().contains(postId)) {
            throw new RuntimeException("User has not liked this post");
        }

        // Remove like and decrement like count
        post.setLikes(post.getLikes() - 1);
        user.getLikedPostIds().remove(postId);

        Post savedPost = postRepository.save(post);
        userRepository.save(user);

        return modelMapper.map(savedPost, PostDto.class);
    }





    @Override
    @Transactional
    public PostDto updatePostById(Long postId, String title, String description, List<MultipartFile> images) throws IOException {
        log.info("Updating Post with id: {}", postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with Id: " + postId));

        // Get the currently authenticated user
        User user = getCurrentUser();

        if (!user.getId().equals(post.getUser_id().getId())) {
            throw new UnAuthorizedException("Post does not belong to this user with id: " + user.getId());
        }

        List<String> imageUrls = new ArrayList<>();
        // Upload images to Cloudinary and get URLs
        if (images != null) {
            for (MultipartFile file : images) {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                String imageUrl = uploadResult.get("url").toString();
                log.info("Updated image URL: {}", imageUrl);
                imageUrls.add(imageUrl);
            }
        }

        post.setTitle(title);
        post.setDescription(description);
        post.setImages(imageUrls); // Set image URLs
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
        log.info("Searching post with keyword : {}" ,keyword);
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
