package com.soumyajit.news.social.media.app.Service;

import com.soumyajit.news.social.media.app.Dtos.CommentDtos;
import com.soumyajit.news.social.media.app.Dtos.CommentRequestDtos;
import com.soumyajit.news.social.media.app.Dtos.PostDto;
import com.soumyajit.news.social.media.app.Dtos.PostRequestDtos;
import com.soumyajit.news.social.media.app.Entities.Post;
import com.soumyajit.news.social.media.app.Exception.ResourceNotFound;
import com.soumyajit.news.social.media.app.Repository.CommentRepository;
import com.soumyajit.news.social.media.app.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final CommentRepository commentRepository;

    @Override
    public PostDto createPost(PostRequestDtos postRequestDtos) {
        log.info("Creating new Post with title:{}" ,postRequestDtos.getTitle());
        Post post = modelMapper.map(postRequestDtos,Post.class);
        post.setLikes(0L);
        post.setComments(List.of());
        Post savedPost = postRepository.save(post);
        return modelMapper.map(savedPost,PostDto.class);
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
    public PostDto updatePostById(Long postId, PostRequestDtos postRequestDtos) {
        log.info("Updating Post with id:{}" ,postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new ResourceNotFound("Post not found with Id : "+postId));
        post.setTitle(postRequestDtos.getTitle());
        post.setDescription(postRequestDtos.getDescription());
        post.setImages(postRequestDtos.getImages());
        Post updatedPost = postRepository.save(post);
        return modelMapper.map(updatedPost, PostDto.class);
    }

    @Override
    public void deletePostById(Long postId) {
        log.info("Deleting Post with id:{}" ,postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new ResourceNotFound("Post not found with Id : "+postId));
        postRepository.deleteById(postId);
    }




}
