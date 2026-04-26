package org.example.capstone2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.capstone2.dto.PostDTO;
import org.example.capstone2.entity.Post;
import org.example.capstone2.entity.User;
import org.example.capstone2.service.PostService;
import org.example.capstone2.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Posts", description = "Fashion post management")
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @Operation(summary = "Get all posts", description = "Returns the 20 most recent posts ordered by date")
    @ApiResponse(responseCode = "200", description = "List of posts")
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @Operation(summary = "Create a post", description = "Creates a new fashion post for the authenticated user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Post created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping("/add")
    public ResponseEntity<Post> addPost(@Valid @RequestBody PostDTO dto, Authentication authentication) {
        User author = userService.findByUsername(authentication.getName());

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setCategory(dto.getCategory());
        post.setUser(author);

        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(post));
    }
}
