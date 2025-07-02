package com.example.demo.controller;

import com.example.demo.dto.UserViewDto;
import com.example.demo.dto.ProfileUpdateDto;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "https://staj-projem-frontend-b7gufmg4bvcgcxbu.polandcentral-01.azurewebsites.net")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserViewDto>> getAllPublicUsers() {
        List<UserViewDto> users = userService.getAllPublicUserViews();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserViewDto> getPublicUserProfile(@PathVariable String username) {
        UserViewDto userViewDto = userService.getPublicUserProfileByUsername(username);
        return ResponseEntity.ok(userViewDto);
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileUpdateDto> getProfileForEditing(Authentication authentication) {
        ProfileUpdateDto profileData = userService.getProfileForEditing(authentication.getName());
        return ResponseEntity.ok(profileData);
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserViewDto> updateUserProfile(
            @PathVariable String username,
            @RequestBody ProfileUpdateDto profileUpdateDto,
            Authentication authentication) {

        UserViewDto updatedUser = userService.updateUserProfile(username, profileUpdateDto, authentication);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/{username}/photo")
    public ResponseEntity<Map<String, String>> uploadProfilePhoto(
            @PathVariable String username,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        String fileUrl = userService.storeProfilePhoto(username, file, authentication);
        return ResponseEntity.ok(Map.of("profileImageUrl", fileUrl));
    }
}
