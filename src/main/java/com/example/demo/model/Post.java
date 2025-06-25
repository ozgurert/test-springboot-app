// src/main/java/com/example/demo/model/Post.java
package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data; // Lombok'u import ediyoruz
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data // @Getter, @Setter, @ToString, @EqualsAndHashCode ve @RequiredArgsConstructor'ı tek seferde ekler.
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 280)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    // Veritabanına ilk kez kaydedilirken otomatik olarak tarih ataması yapar.
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}