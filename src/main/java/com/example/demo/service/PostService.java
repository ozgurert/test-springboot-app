// src/main/java/com/example/demo/service/PostService.java
package com.example.demo.service;

import com.example.demo.dto.PostCreateDto;
import com.example.demo.dto.PostViewDto;
import com.example.demo.dto.UserViewDto;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Post createPost(PostCreateDto postCreateDto, String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + username));

        Post post = new Post();
        post.setContent(postCreateDto.getContent());
        post.setAuthor(author);

        return postRepository.save(post);
    }

    public List<PostViewDto> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToPostViewDto)
                .collect(Collectors.toList());
    }

    private PostViewDto convertToPostViewDto(Post post) {
        PostViewDto dto = new PostViewDto();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setAuthor(convertToUserViewDto(post.getAuthor()));
        // İleride beğeni ve yorum sayıları burada hesaplanacak.
        return dto;
    }

    private UserViewDto convertToUserViewDto(User user) {
        UserViewDto dto = new UserViewDto();
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        return dto;
    }
}