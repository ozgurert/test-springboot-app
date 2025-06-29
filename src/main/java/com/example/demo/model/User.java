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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    @JsonIgnore
    @Column(name = "password_hash")
    private String password;

    private String bio;

    private String location;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "visibility_settings", columnDefinition = "TEXT")
    private String visibilitySettings;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_token_expiry")
    private LocalDateTime resetPasswordTokenExpiry;

    @ManyToMany(fetch = FetchType.EAGER) // EAGER: Kullanıcı çekildiğinde rolleri de hemen yüklensin.
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // =================================================================
    // UserDetails Arayüzü İçin Gerekli Olan Metotlar
    // =================================================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // <-- DEĞİŞİKLİK: Artık rolleri statik olarak vermek yerine,
        // kullanıcının 'roles' set'inden dinamik olarak alıyoruz.
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}