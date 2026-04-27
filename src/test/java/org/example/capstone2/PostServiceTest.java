package org.example.capstone2;

import exception.ResourceNotFoundException;
import org.example.capstone2.entity.Post;
import org.example.capstone2.repository.PostRepository;
import org.example.capstone2.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void createPost_ShouldSaveAndReturnPost() {
        Post post = new Post();
        post.setTitle("Bridal Lehenga");
        post.setContent("Handcrafted silk lehenga.");
        post.setCategory("Bridal");

        when(postRepository.save(post)).thenReturn(post);

        Post result = postService.createPost(post);

        assertNotNull(result);
        assertEquals("Bridal Lehenga", result.getTitle());
        verify(postRepository).save(post);
    }

    @Test
    void getAllPosts_ShouldReturnList() {
        Post p1 = new Post();
        p1.setTitle("Post 1");
        Post p2 = new Post();
        p2.setTitle("Post 2");

        when(postRepository.findTop20WithUser()).thenReturn(List.of(p1, p2));

        List<Post> result = postService.getAllPosts();

        assertEquals(2, result.size());
        assertEquals("Post 1", result.get(0).getTitle());
    }

    @Test
    void getAllPosts_ShouldReturnEmptyList_WhenNoPosts() {
        when(postRepository.findTop20WithUser()).thenReturn(List.of());

        List<Post> result = postService.getAllPosts();

        assertTrue(result.isEmpty());
    }

    @Test
    void deletePost_ShouldCallDeleteById_WhenPostExists() {
        when(postRepository.existsById(1L)).thenReturn(true);

        postService.deletePost(1L);

        verify(postRepository).deleteById(1L);
    }

    @Test
    void deletePost_ShouldThrow_WhenPostNotFound() {
        when(postRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(99L));
        verify(postRepository, never()).deleteById(any());
    }
}
