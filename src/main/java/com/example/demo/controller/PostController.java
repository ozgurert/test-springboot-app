// src/main/java/com/example/demo/controller/PostController.java
package com.example.demo.controller;

import com.example.demo.dto.PostCreateDto;
import com.example.demo.dto.PostViewDto;
import com.example.demo.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "https://staj-projem-frontend.polandcentral.azurewebsites.net")
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<Void> createPost(@RequestBody PostCreateDto postCreateDto, Authentication authentication) {
        postService.createPost(postCreateDto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<PostViewDto>> getAllPosts(Authentication authentication) {
        List<PostViewDto> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }
}