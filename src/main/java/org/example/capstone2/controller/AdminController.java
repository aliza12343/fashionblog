package org.example.capstone2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.capstone2.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin", description = "Admin-only content moderation operations")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final PostService postService;

    public AdminController(PostService postService) {
        this.postService = postService;
    }

    @Operation(summary = "Delete a post (Admin only)", description = "Permanently removes a post by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Post deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Post not found"),
        @ApiResponse(responseCode = "403", description = "Access denied — ADMIN role required")
    })
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<String> adminDeletePost(
            @Parameter(description = "ID of the post to delete") @PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok("Post successfully deleted by Admin.");
    }
}
