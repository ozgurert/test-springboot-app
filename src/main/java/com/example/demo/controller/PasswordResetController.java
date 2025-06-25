package com.example.demo.controller;

import com.example.demo.request.ForgotPasswordRequest;
import com.example.demo.request.ResetPasswordRequest;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map; // Gerekli import

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    @Autowired
    private UserService userService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        userService.handleForgotPassword(request);

        // DEĞİŞİKLİK: Düz metin yerine, "message" anahtarı olan bir JSON nesnesi döndür.
        // Map.of(), kolayca bir Map oluşturur ve Spring bunu JSON'a çevirir.
        return ResponseEntity.ok(Map.of("message", "Eğer e-posta adresi sistemimizde kayıtlıysa, şifre sıfırlama linki gönderilmiştir."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.handleResetPassword(request);

        // DEĞİŞİKLİK: Düz metin yerine, "message" anahtarı olan bir JSON nesnesi döndür.
        return ResponseEntity.ok(Map.of("message", "Şifreniz başarıyla güncellenmiştir. Şimdi giriş yapabilirsiniz."));
    }
}
