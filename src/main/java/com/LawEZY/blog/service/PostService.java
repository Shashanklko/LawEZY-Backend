package com.LawEZY.blog.service;

import com.LawEZY.blog.entity.Comment;
import com.LawEZY.blog.entity.Like;
import com.LawEZY.blog.entity.Post;
import com.LawEZY.blog.enums.PostType;
import com.LawEZY.blog.dto.PostRequest;

import com.LawEZY.blog.repository.CommentRepository;
import com.LawEZY.blog.repository.LikeRepository;
import com.LawEZY.blog.repository.PostRepository;
import com.LawEZY.user.entity.User;
import com.LawEZY.user.repository.UserRepository;
import com.LawEZY.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Post createPost(@NonNull PostRequest request) {
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
        
        Post post = new Post();
        post.setAuthor(author);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setType(request.getType());
        return postRepository.save(post);
    }

    @NonNull
    public List<Post> getFeed(@Nullable PostType type) {
        if (type != null) {
            return postRepository.findByTypeOrderByCreatedAtDesc(type);
        }
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public void toggleLike(@NonNull Long postId, @NonNull Long userId) {
        if (likeRepository.existsByPostIdAndUserId(postId, userId)) {
            likeRepository.deleteByPostIdAndUserId(postId, userId);
            updateLikeCount(postId, -1);
        } else {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);
            updateLikeCount(postId, 1);
        }
    }

    @Transactional
    @NonNull
    public Comment addComment(@NonNull Long postId, @NonNull Long authorId, @NonNull String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
        
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setAuthor(author);
        comment.setContent(content);
        
        Comment savedComment = commentRepository.save(comment);
        
        Integer currentCount = post.getCommentCount();
        post.setCommentCount(currentCount != null ? currentCount + 1 : 1);
        postRepository.save(post);
        
        return savedComment;
    }

    public Post getPostById(@NonNull Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    }

    @Transactional
    public Post updatePost(@NonNull Long postId, @NonNull Long userId, @NonNull PostRequest request) {
        Post post = getPostById(postId);
        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: Only the author can update this post.");
        }
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setType(request.getType());
        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(@NonNull Long postId, @NonNull Long userId) {
        Post post = getPostById(postId);
        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: Only the author can delete this post.");
        }
        postRepository.delete(post);
    }

    private void updateLikeCount(@NonNull Long postId, int delta) {
        postRepository.findById(postId).ifPresent(post -> {
            Integer currentLikes = post.getLikeCount();
            post.setLikeCount(currentLikes != null ? Math.max(0, currentLikes + delta) : Math.max(0, delta));
            postRepository.save(post);
        });
    }
}
