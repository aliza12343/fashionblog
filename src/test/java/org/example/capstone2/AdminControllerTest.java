package org.example.capstone2;

import exception.ResourceNotFoundException;
import org.example.capstone2.Config.SecurityConfig;
import org.example.capstone2.controller.AdminController;
import org.example.capstone2.jwt.JwtAuthFilter;
import org.example.capstone2.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = AdminController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthFilter.class})
)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Test
    void adminDeletePost_ShouldReturn200_WhenPostExists() throws Exception {
        doNothing().when(postService).deletePost(1L);

        mockMvc.perform(delete("/api/admin/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Post successfully deleted by Admin."));
    }

    @Test
    void adminDeletePost_ShouldReturn404_WhenPostNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Post not found: 99"))
                .when(postService).deletePost(99L);

        mockMvc.perform(delete("/api/admin/posts/99"))
                .andExpect(status().isNotFound());
    }
}
