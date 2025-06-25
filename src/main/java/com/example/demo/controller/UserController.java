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

/**
 * Kullanıcı işlemleri için REST API denetleyicisi.
 * Bu sınıf "Thin Controller" prensibine uygun olarak tasarlanmıştır.
 * Görevi, HTTP isteklerini doğrulamak ve ilgili iş mantığı için UserService'e devretmektir.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Dashboard için tüm kullanıcıların halka açık profil bilgilerini getirir.
     * URL: GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<UserViewDto>> getAllPublicUsers() {
        List<UserViewDto> users = userService.getAllPublicUserViews();
        return ResponseEntity.ok(users);
    }

    /**
     * AÇIKLAMA: Bu endpoint, bir başkasının genel profilini görüntülemek için kullanılır.
     * URL: GET /api/users/{username}
     * Güvenlik ve gizlilik kontrolleri Service katmanında yapılır.
     */
    @GetMapping("/{username}")
    public ResponseEntity<UserViewDto> getPublicUserProfile(@PathVariable String username) {
        UserViewDto userViewDto = userService.getPublicUserProfileByUsername(username);
        return ResponseEntity.ok(userViewDto);
    }

    /**
     * YENİ: Bu endpoint, o an giriş yapmış kullanıcının kendi profilini
     * düzenleme formunda göstermek için gerekli verileri çeker.
     * URL: GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<ProfileUpdateDto> getProfileForEditing(Authentication authentication) {
        ProfileUpdateDto profileData = userService.getProfileForEditing(authentication.getName());
        return ResponseEntity.ok(profileData);
    }

    /**
     * Belirli bir kullanıcının profil bilgilerini günceller.
     * Güvenlik kontrolü (sadece kendi profilini güncelleyebilme) Service katmanında yapılır.
     * URL: PUT /api/users/{username}
     */
    @PutMapping("/{username}")
    public ResponseEntity<UserViewDto> updateUserProfile(
            @PathVariable String username,
            @RequestBody ProfileUpdateDto profileUpdateDto,
            Authentication authentication) {

        UserViewDto updatedUser = userService.updateUserProfile(username, profileUpdateDto, authentication);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Belirtilen kullanıcı için bir profil fotoğrafı yükler.
     * Güvenlik kontrolü Service katmanında yapılır.
     * URL: POST /api/users/{username}/photo
     */
    @PostMapping("/{username}/photo")
    public ResponseEntity<Map<String, String>> uploadProfilePhoto(
            @PathVariable String username,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        String fileUrl = userService.storeProfilePhoto(username, file, authentication);
        return ResponseEntity.ok(Map.of("profileImageUrl", fileUrl));
    }
}
