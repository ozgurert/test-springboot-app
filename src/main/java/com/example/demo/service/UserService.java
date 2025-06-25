package com.example.demo.service;

import com.example.demo.dto.LoginDto;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserViewDto;
import com.example.demo.dto.ProfileUpdateDto;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.ForgotPasswordRequest;
import com.example.demo.request.ResetPasswordRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    // --- DEPENDENCIES ---
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path rootLocation = Paths.get("uploads");

    @Autowired
    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder, JwtService jwtService, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.mailSender = mailSender;
    }

    // --- INITIALIZATION ---
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    // --- PUBLIC-FACING METHODS (Called by Controller) ---

    /**
     * DÜZENLENDİ: Yeni kullanıcı kaydederken varsayılan profil fotoğrafı atanır.
     */
    public User registerUser(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent() ||
                userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu kullanıcı adı veya e-posta adresi zaten kullanılıyor!");
        }
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setGender(userDto.getGender());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        String defaultVisibilitySettings = "{\"showEmail\": false, \"showPhoneNumber\": false, \"showGender\": false, \"showDateOfBirth\": false}";
        user.setVisibilitySettings(defaultVisibilitySettings);
        user.setBio("Merhaba! Bu benim yeni profilim.");
        user.setLocation("");

        // *** ANA DEĞİŞİKLİK BURADA ***
        // 'null' yerine varsayılan fotoğrafın yolu atanıyor.
        user.setProfileImageUrl("/images/default-avatar.png");

        return userRepository.save(user);
    }

    public String loginUser(LoginDto loginDto) {
        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Geçersiz kullanıcı adı veya parola"));

        if (passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return jwtService.generateToken(user.getUsername());
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Geçersiz kullanıcı adı veya parola");
        }
    }

    public List<UserViewDto> getAllPublicUserViews() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToPublicViewDto)
                .collect(Collectors.toList());
    }

    public UserViewDto getPublicUserProfileByUsername(String username) {
        User user = findUserByUsername(username);
        return convertToPublicViewDto(user);
    }

    public ProfileUpdateDto getProfileForEditing(String authenticatedUsername) {
        User user = findUserByUsername(authenticatedUsername);
        ProfileUpdateDto dto = new ProfileUpdateDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setBio(user.getBio());
        dto.setLocation(user.getLocation());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setGender(user.getGender());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setEmail(user.getEmail());

        try {
            Map<String, Boolean> settings = parseVisibilitySettings(user.getVisibilitySettings());
            dto.setVisibilitySettings(settings);
        } catch (IOException e) {
            System.err.println("Visibility settings parse edilirken hata: " + e.getMessage());
            dto.setVisibilitySettings(Collections.emptyMap());
        }

        return dto;
    }

    @Transactional
    public UserViewDto updateUserProfile(String username, ProfileUpdateDto profileUpdateDto, Authentication authentication) {
        if (!authentication.getName().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu profili düzenleme yetkiniz yok.");
        }
        User userToUpdate = findUserByUsername(username);

        userToUpdate.setFirstName(profileUpdateDto.getFirstName());
        userToUpdate.setLastName(profileUpdateDto.getLastName());
        userToUpdate.setBio(profileUpdateDto.getBio());
        userToUpdate.setLocation(profileUpdateDto.getLocation());
        userToUpdate.setPhoneNumber(profileUpdateDto.getPhoneNumber());
        userToUpdate.setGender(profileUpdateDto.getGender());
        userToUpdate.setDateOfBirth(profileUpdateDto.getDateOfBirth());

        if (profileUpdateDto.getVisibilitySettings() != null) {
            try {
                String settingsJson = objectMapper.writeValueAsString(profileUpdateDto.getVisibilitySettings());
                userToUpdate.setVisibilitySettings(settingsJson);
            } catch (JsonProcessingException e) {
                System.err.println("Visibility settings JSON'a çevrilirken hata: " + e.getMessage());
            }
        }

        User updatedUser = userRepository.save(userToUpdate);
        return convertToPublicViewDto(updatedUser);
    }

    @Transactional
    public String storeProfilePhoto(String username, MultipartFile file, Authentication authentication) {
        if (!authentication.getName().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to change this profile photo.");
        }
        User user = findUserByUsername(username);
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Boş dosya yüklenemez.");
            }
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String destinationFilename = username + "-" + System.currentTimeMillis() + fileExtension;
            Path destinationFile = this.rootLocation.resolve(destinationFilename).normalize().toAbsolutePath();
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            String fileUrl = "/uploads/" + destinationFilename;
            user.setProfileImageUrl(fileUrl);
            userRepository.save(user);
            return fileUrl;
        } catch (IOException e) {
            throw new RuntimeException("Dosya kaydedilemedi.", e);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findUserByUsername(username);
    }

    public void handleForgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            return;
        }
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        String resetLink = "http://localhost:4200/reset-password?token=" + token;
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("BookFace Şifre Sıfırlama Talebi");
        mailMessage.setText("Merhaba,\n\nŞifrenizi sıfırlamak için lütfen aşağıdaki linke tıklayın:\n" + resetLink + "\n\nEğer bu isteği siz yapmadıysanız, bu e-postayı görmezden gelebilirsiniz.");
        try {
            mailSender.send(mailMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleResetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetPasswordToken(request.getToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Şifre sıfırlama linki geçersiz veya süresi dolmuş."));

        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Şifre sıfırlama linkinin süresi dolmuş.");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        userRepository.save(user);
    }

    @Transactional
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupExpiredResetTokens() {
        System.out.println("Zamanlanmış görev çalışıyor: Süresi dolmuş şifre sıfırlama token'ları temizleniyor...");
        List<User> usersWithExpiredTokens = userRepository.findAllByResetPasswordTokenIsNotNullAndResetPasswordTokenExpiryBefore(LocalDateTime.now());
        if (!usersWithExpiredTokens.isEmpty()) {
            for (User user : usersWithExpiredTokens) {
                user.setResetPasswordToken(null);
                user.setResetPasswordTokenExpiry(null);
            }
            userRepository.saveAll(usersWithExpiredTokens);
            System.out.println(usersWithExpiredTokens.size() + " adet süresi dolmuş token temizlendi.");
        } else {
            System.out.println("Temizlenecek süresi dolmuş token bulunamadı.");
        }
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));
    }

    private UserViewDto convertToPublicViewDto(User user) {
        UserViewDto dto = new UserViewDto();
        Map<String, Boolean> settings;
        try {
            settings = parseVisibilitySettings(user.getVisibilitySettings());
        } catch (IOException e) {
            System.err.println("convertToPublicViewDto içinde visibility settings parse edilirken hata: " + e.getMessage());
            settings = Collections.emptyMap();
        }

        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setBio(user.getBio());
        dto.setLocation(user.getLocation());
        dto.setProfileImageUrl(user.getProfileImageUrl());

        if (settings.getOrDefault("showEmail", false)) {
            dto.setEmail(user.getEmail());
        }
        if (settings.getOrDefault("showPhoneNumber", false)) {
            dto.setPhoneNumber(user.getPhoneNumber());
        }
        if (settings.getOrDefault("showGender", false)) {
            dto.setGender(user.getGender());
        }
        if (settings.getOrDefault("showDateOfBirth", false)) {
            dto.setDateOfBirth(user.getDateOfBirth());
        }
        return dto;
    }

    private Map<String, Boolean> parseVisibilitySettings(String json) throws IOException {
        if (json == null || json.isEmpty()) {
            return Collections.emptyMap();
        }
        return objectMapper.readValue(json, new TypeReference<Map<String, Boolean>>() {});
    }
}