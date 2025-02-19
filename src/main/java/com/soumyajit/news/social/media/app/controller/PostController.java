package com.soumyajit.news.social.media.app.controller;

import com.soumyajit.news.social.media.app.Dtos.PostDto;
import com.soumyajit.news.social.media.app.Dtos.PostRequestDtos;
import com.soumyajit.news.social.media.app.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/createPost")
    public ResponseEntity<PostDto> createPost(@RequestBody PostRequestDtos postRequestDtos){
        return ResponseEntity.ok(postService.createPost(postRequestDtos));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long postId){
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @GetMapping()
   // @CrossOrigin(origins = "http://localhost:63342")
    public ResponseEntity<List<PostDto>> getAllPost(){
        return ResponseEntity.ok(postService.getAllPosts());
    }
    @PostMapping("/{postId}/like")
    public ResponseEntity<PostDto> postLikeById(@PathVariable Long postId){
        return ResponseEntity.ok(postService.postLikeById(postId));
    }

    @PostMapping("/{postId}/removelike")
    public ResponseEntity<PostDto> removeLike(@PathVariable Long postId) {
        PostDto postDto = postService.removeLikeById(postId);
        return ResponseEntity.ok(postDto);
    }


    @PutMapping("/{postId}/update")
    public ResponseEntity<PostDto> updatePostById(@PathVariable Long postId,@RequestBody PostRequestDtos postRequestDtos){
        return ResponseEntity.ok(postService.updatePostById(postId,postRequestDtos));
    }
    @DeleteMapping("/{postId}/delete")
    public ResponseEntity<Void> deletePostById(@PathVariable Long postId){
        postService.deletePostById(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostDto>>searchPosts(@RequestParam String keyword){
        return ResponseEntity.ok(postService.searchPosts(keyword));
    }

}
