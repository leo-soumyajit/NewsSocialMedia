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
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;
    private final CacheManager cacheManager;


    @Override
    @Transactional
    public PostDto createPost(String title, String description, List<MultipartFile> images, String category) throws IOException {
        List<String> imageUrls = new ArrayList<>();

        if (images != null) {
            for (MultipartFile file : images) {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                String imageUrl = uploadResult.get("url").toString();
                log.info("Uploaded image URL: {}", imageUrl);
                imageUrls.add(imageUrl);
            }
        }

        Post post = new Post();
        post.setTitle(title);
        post.setDescription(description);
        post.setLikes(0L);
        post.setComments(new ArrayList<>());
        post.setImages(imageUrls);
        post.setCategory(category);

        User user = getCurrentUserWithPosts();
        post.setUser_id(user); // Only store reference to user

        Post savedPost = postRepository.save(post);

        // No caching — always map fresh from DB
        return mapPostToDto(savedPost);
    }





    @Override
    public PostDto getPostById(Long postId) {
        log.info("Getting Post with id: {}", postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with Id : " + postId));

        return mapPostToDto(post); // custom mapper
    }


    @Override
    public List<PostDto> getAllPosts() {
        log.info("Getting All Posts");

        List<Post> posts = postRepository.findAllPostsOrderedByCreationDateDesc();

        return posts.stream()
                .map(this::mapPostToDto) // custom mapper
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "posts", key = "#postId"),
            @CacheEvict(value = "postList", allEntries = true)
    })
    public PostDto postLikeById(Long postId) {
        log.info("Liking post with id: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with Id: " + postId));

        User user = getCurrentUser();

        if (user.getLikedPostIds().contains(postId)) {
            throw new RuntimeException("User has already liked this post");
        }

        post.setLikes(post.getLikes() + 1);
        user.getLikedPostIds().add(postId);

        Post savedPost = postRepository.save(post);
        userRepository.save(user);

        return modelMapper.map(savedPost, PostDto.class);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "posts", key = "#postId"),
            @CacheEvict(value = "postList", allEntries = true)
    })
    public PostDto removeLikeById(Long postId) {
        log.info("Removing like from post with id: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with Id: " + postId));

        User user = getCurrentUser();

        if (!user.getLikedPostIds().contains(postId)) {
            throw new RuntimeException("User has not liked this post");
        }

        post.setLikes(post.getLikes() - 1);
        user.getLikedPostIds().remove(postId);

        Post savedPost = postRepository.save(post);
        userRepository.save(user);

        return modelMapper.map(savedPost, PostDto.class);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "posts", key = "#postId"),
            @CacheEvict(value = "postList", allEntries = true)
    })
    public PostDto updatePostById(Long postId, String title, String description) throws IOException {
        log.info("Updating Post with id: {}", postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with Id: " + postId));

        User user = getCurrentUser();

        if (!user.getId().equals(post.getUser_id().getId())) {
            throw new UnAuthorizedException("Post does not belong to this user with id: " + user.getId());
        }

        List<String> imageUrls = new ArrayList<>();

        post.setTitle(title);
        post.setDescription(description);

        Post updatedPost = postRepository.save(post);
        PostDto postDto = modelMapper.map(updatedPost, PostDto.class);

        //Force re-cache
        if (cacheManager.getCache("posts") != null) {
            cacheManager.getCache("posts").put(updatedPost.getId(), postDto);
            log.info("Manually cached updated post with ID: {}", updatedPost.getId());
        } else {
            log.warn("Cache 'posts' not found");
        }

        return postDto;
    }


    public List<PostDto> getPostsByCategory(String category) {
        log.info("Getting Posts for category: {}", category);
        List<Post> posts = postRepository.findByCategoryIgnoreCaseOrderByCreatedAtDesc(category);
        return posts.stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();
    }





    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "posts", key = "#postId"),
            @CacheEvict(value = "postList", allEntries = true)
    })
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
        log.info("Searching post with keyword : {}", keyword);
        List<Post> posts = postRepository
                .findByTitleContainingOrDescriptionContainingOrderByCreationDateDesc(keyword, keyword);
        return posts.stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional(readOnly = true)
    private User getCurrentUserWithPosts() {
        User currentUser = getCurrentUser();
        return userRepository.findByIdWithPosts(currentUser.getId()).orElseThrow(() -> new ResourceNotFound("User not found"));
    }

    private PostDto mapPostToDto(Post post) {
        PostDto dto = modelMapper.map(post, PostDto.class);

        dto.setUserId(post.getUser_id().getId());
        dto.setUserName(post.getUser_id().getName());
        dto.setProfileImage(post.getUser_id().getProfileImage()); // Always latest

        return dto;
    }



}



