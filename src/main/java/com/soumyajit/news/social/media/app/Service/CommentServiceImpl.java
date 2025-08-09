package com.soumyajit.news.social.media.app.Service;

import com.soumyajit.news.social.media.app.Dtos.CommentDtos;
import com.soumyajit.news.social.media.app.Dtos.CommentRequestDtos;
import com.soumyajit.news.social.media.app.Entities.Comments;
import com.soumyajit.news.social.media.app.Entities.Post;
import com.soumyajit.news.social.media.app.Entities.User;
import com.soumyajit.news.social.media.app.Exception.ResourceNotFound;
import com.soumyajit.news.social.media.app.Repository.CommentRepository;
import com.soumyajit.news.social.media.app.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final ModelMapper modelMapper;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "posts", key = "#postId"),
            @CacheEvict(value = "postList", allEntries = true)
    })
    public CommentDtos addComment(CommentRequestDtos commentRequestDtos, Long postId) {
        log.info("Adding comments in a Post with Id :{}", postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with Id : " + postId));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Comments comments = modelMapper.map(commentRequestDtos, Comments.class);
        comments.setPost_id(post);
        comments.setUser_id(user);

        // Remove storing profileImage in DB to avoid outdated copies
        comments.setProfileImage(null);

        post.getComments().add(comments);
        comments = commentRepository.save(comments);
        postRepository.save(post);

        // Build DTO manually to always get the latest user profile image
        CommentDtos commentDtos = modelMapper.map(comments, CommentDtos.class);
        commentDtos.setUserName(user.getName());
        commentDtos.setUserId(user.getId());
        commentDtos.setProfileImage(user.getProfileImage());
        commentDtos.setPostId(postId);

        return commentDtos;
    }




    @Override
    @Caching(evict = {
            @CacheEvict(value = "posts", key = "#postId"),
            @CacheEvict(value = "postList", allEntries = true)
    })
    public void deleteComment(Long postId, Long commentId) {
        log.info("Deleting comments in a Post with Id :{}",postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new ResourceNotFound("Post not found with Id : "+postId));
        Comments comments = commentRepository.findById(commentId)
                .orElseThrow(()->new ResourceNotFound("Comment not found with Id : "+commentId));

        User user = getCurrentUser();

        if(!comments.getUser_id().getId().equals(user.getId())){
            if( !comments.getPost_id().getUser_id().getId().equals(user.getId())) {
                throw new RuntimeException("Comment does not belong to the user with name : " + user.getName() +
                        " id : " + user.getId());
            }
        }

        if(!comments.getPost_id().getId().equals(postId)){
            throw new RuntimeException("Comment does not belong to the specified post");
        }

        post.getComments().remove(comments); //delete from post's comment
        commentRepository.deleteById(commentId);
        postRepository.save(post);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "posts", key = "#postId"),
            @CacheEvict(value = "postList", allEntries = true)
    })
    public CommentDtos updateComment(CommentRequestDtos commentRequestDtos, Long postId, Long commentId) {
        log.info("Updating comments in a Post with Id :{}",postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(()->new ResourceNotFound("Post not found with Id : "+postId));
        Comments comments = commentRepository.findById(commentId)
                .orElseThrow(()->new ResourceNotFound("Comment not found with Id : "+commentId));

        User user = getCurrentUser();
        if(!comments.getUser_id().getId().equals(user.getId())){
            throw new RuntimeException("Comment does not belong to the user with name : "+user.getName()+
                    " id : "+user.getId());
        }

        if(!comments.getPost_id().getId().equals(postId)){
            throw new RuntimeException("Comment does not belong to the specified post");
        }


        comments.setContent(commentRequestDtos.getContent()); //set new comment
        post.getComments().add(comments); //add that comment into post
        comments = commentRepository.save(comments); //save comment in repo
        postRepository.save(post); //save the post in repo
        return modelMapper.map(comments,CommentDtos.class);
    }

    private User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}