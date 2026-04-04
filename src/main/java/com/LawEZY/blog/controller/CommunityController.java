package com.LawEZY.blog.controller;

import com.LawEZY.blog.entity.Comment;
import com.LawEZY.blog.entity.Post;
import com.LawEZY.blog.enums.PostType;
import com.LawEZY.blog.service.PostService;
import com.LawEZY.blog.dto.PostRequest;

import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    @Autowired
    private PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(@RequestBody @NonNull PostRequest request) {
        return ResponseEntity.ok(postService.createPost(request));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<Post>> getFeed(@RequestParam(required = false) PostType type) {
        return ResponseEntity.ok(postService.getFeed(type));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable @NonNull Long postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<Post> updatePost(
            @PathVariable @NonNull Long postId,
            @RequestParam @NonNull Long userId,
            @RequestBody @NonNull PostRequest request) {
        return ResponseEntity.ok(postService.updatePost(postId, userId, request));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable @NonNull Long postId,
            @RequestParam @NonNull Long userId) {
        postService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Void> toggleLike(@PathVariable @NonNull Long postId, @RequestParam @NonNull Long userId) {
        postService.toggleLike(postId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/posts/{postId}/comment")
    public ResponseEntity<Comment> addComment(
            @PathVariable @NonNull Long postId,
            @RequestParam @NonNull Long authorId,
            @RequestParam @NonNull String content) {
        return ResponseEntity.ok(postService.addComment(postId, authorId, content));
    }
}
