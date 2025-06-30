package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "https://staj-projem-frontend-b7gufmg4bvcgcxbu.polandcentral-01.azurewebsites.net")
@RestController
@RequestMapping("/api")
public class TestController {

    // Herkesin erişebileceği bir endpoint (güvenlik ayarlarında belirtilmediği için 'authenticated' olacak)
    @GetMapping("/public")
    public String getPublicData() {
        return "Bu bilgiye tüm giriş yapmış kullanıcılar erişebilir.";
    }

    // Hem USER hem ADMIN'in erişebileceği bir endpoint
    @GetMapping("/users/me/test") // <-- DEĞİŞİKLİK BURADA
    public String getUserData(Principal principal) {
        return "Hoş geldin USER (test endpoint'i): " + principal.getName();
    }

    // Sadece ADMIN'in erişebileceği bir endpoint
    @GetMapping("/admin/panel")
    public String getAdminData(Principal principal) {
        return "Hoş geldin ADMIN: " + principal.getName() + ". Bu gizli admin paneli!";
    }
}