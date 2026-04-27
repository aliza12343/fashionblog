package org.example.capstone2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.capstone2.Config.SecurityConfig;
import org.example.capstone2.controller.PostController;
import org.example.capstone2.entity.Post;
import org.example.capstone2.entity.User;
import org.example.capstone2.entity.UserRole;
import org.example.capstone2.jwt.JwtAuthFilter;
import org.example.capstone2.service.PostService;
import org.example.capstone2.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = PostController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthFilter.class})
)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private UserService userService;

    @Test
    void getAllPosts_ShouldReturn200_WithListOfPosts() throws Exception {
        Post p1 = new Post();
        p1.setTitle("Summer Dress");
        p1.setContent("Light and breezy.");
        p1.setCategory("Dresses");

        Post p2 = new Post();
        p2.setTitle("Gold Earrings");
        p2.setContent("Minimalist style.");
        p2.setCategory("Accessories");

        when(postService.getAllPosts()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Summer Dress"))
                .andExpect(jsonPath("$[1].title").value("Gold Earrings"));
    }

    @Test
    void getAllPosts_ShouldReturn200_WithEmptyList_WhenNoPosts() throws Exception {
        when(postService.getAllPosts()).thenReturn(List.of());

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void addPost_ShouldReturn201_WhenValid() throws Exception {
        User author = new User();
        author.setUsername("testuser");
        author.setRole(UserRole.USER);

        Post created = new Post();
        created.setTitle("New Post");
        created.setContent("Great content.");
        created.setCategory("Dresses");
        created.setUser(author);

        when(userService.findByUsername("testuser")).thenReturn(author);
        when(postService.createPost(any(Post.class))).thenReturn(created);

        String body = """
                {
                  "title": "New Post",
                  "content": "Great content.",
                  "category": "Dresses"
                }
                """;

        var auth = new UsernamePasswordAuthenticationToken("testuser", null, List.of());

        mockMvc.perform(post("/api/posts/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .principal(auth))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Post"));
    }

    @Test
    void addPost_ShouldReturn400_WhenTitleMissing() throws Exception {
        String body = """
                {
                  "content": "Some content.",
                  "category": "Dresses"
                }
                """;

        var auth = new UsernamePasswordAuthenticationToken("testuser", null, List.of());

        mockMvc.perform(post("/api/posts/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .principal(auth))
                .andExpect(status().isBadRequest());
    }
}
