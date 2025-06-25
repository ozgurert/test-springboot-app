package com.example.demo.model; // Kendi paket adınızı kullanın

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data // Lombok: Getter, Setter, toString, EqualsAndHashCode metodlarını otomatik oluşturur.
@Entity
@Table(name = "users")
public class User implements UserDetails { // Spring Security için UserDetails arayüzünü uygular

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String username;

    private String gender;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String email;

    // Alanın adı 'password' olarak değiştirildi, bu UserDetails standardıdır.
    // Veritabanı kolon adı hala 'password_hash' olarak kalabilir.
    // @JsonIgnore, bu alanın API cevaplarında gönderilmesini engeller.
    @JsonIgnore
    @Column(name = "password_hash")
    private String password;

    private String bio;

    private String location;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "visibility_settings", columnDefinition = "TEXT")
    private String visibilitySettings;

    // Şifre sıfırlama için eklenen alanlar
    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_token_expiry")
    private LocalDateTime resetPasswordTokenExpiry;


    // =================================================================
    // UserDetails Arayüzü İçin Gerekli Olan Metotlar
    // =================================================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Bu projede şimdilik her kullanıcıya standart "ROLE_USER" yetkisi veriyoruz.
        // Daha karmaşık bir rol yönetimi sistemi için burayı özelleştirebilirsin.
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // `password` alanının getter metodu UserDetails tarafından kullanılır.
    // @Data anotasyonu bu metodu bizim için otomatik olarak oluşturur: getPassword()

    // `username` alanının getter metodu UserDetails tarafından kullanılır.
    // @Data anotasyonu bu metodu bizim için otomatik olarak oluşturur: getUsername()

    @Override
    public boolean isAccountNonExpired() {
        // Hesabın süresinin dolup dolmadığını kontrol eder. Şimdilik her zaman true.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Hesabın kilitli olup olmadığını kontrol eder. Şimdilik her zaman true.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Kullanıcı parolasının süresinin dolup dolmadığını kontrol eder. Şimdilik her zaman true.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Kullanıcının aktif olup olmadığını kontrol eder. Şimdilik her zaman true.
        return true;
    }
}