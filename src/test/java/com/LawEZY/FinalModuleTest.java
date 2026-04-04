package com.LawEZY;

import com.LawEZY.blog.entity.Post;
import com.LawEZY.blog.repository.PostRepository;
import com.LawEZY.blog.service.PostService;
import com.LawEZY.content.entity.LegalResource;
import com.LawEZY.content.repository.ResourceRepository;
import com.LawEZY.content.service.ResourceService;
import com.LawEZY.user.entity.User;
import com.LawEZY.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinalModuleTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    @InjectMocks
    private ResourceService resourceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deletePost_UnauthorizedUser_ShouldThrowException() {
        Long postId = 1L;
        Long authorId = 100L;
        Long attackerId = 200L;

        User author = new User();
        author.setId(authorId);

        Post post = new Post();
        post.setId(postId);
        post.setAuthor(author);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThrows(RuntimeException.class, () -> postService.deletePost(postId, attackerId));
        verify(postRepository, never()).delete(any());
    }

    @Test
    void deletePost_AuthorizedUser_ShouldSucceed() {
        Long postId = 1L;
        Long authorId = 100L;

        User author = new User();
        author.setId(authorId);

        Post post = new Post();
        post.setId(postId);
        post.setAuthor(author);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.deletePost(postId, authorId);
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    void getResource_ById_ShouldReturnResource() {
        Long resourceId = 50L;
        LegalResource resource = new LegalResource();
        resource.setId(resourceId);
        resource.setTitle("Legal Guide");

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));

        LegalResource found = resourceService.getResourceById(resourceId);
        assertNotNull(found);
        assertEquals("Legal Guide", found.getTitle());
    }
}
