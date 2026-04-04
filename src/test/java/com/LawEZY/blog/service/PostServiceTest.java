package com.LawEZY.blog.service;

import com.LawEZY.blog.dto.PostRequest;
import com.LawEZY.blog.entity.Post;
import com.LawEZY.blog.enums.PostType;
import com.LawEZY.blog.repository.PostRepository;
import com.LawEZY.user.entity.User;
import com.LawEZY.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPost_Success() {
        PostRequest request = new PostRequest();
        request.setAuthorId(1L);
        request.setTitle("Test Blog");
        request.setContent("Content here");
        request.setType(PostType.BLOG);

        User author = new User();
        author.setId(1L);

        Post post = new Post();
        post.setAuthor(author);
        post.setTitle(request.getTitle());

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post response = postService.createPost(request);

        assertNotNull(response);
        assertEquals("Test Blog", response.getTitle());
        verify(postRepository, times(1)).save(any(Post.class));
    }
}
