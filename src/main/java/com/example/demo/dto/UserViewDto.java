package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserViewDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String bio;
    private String location;
    private String email;
    private String phoneNumber;
    private String gender;
    private String dateOfBirth;
    private String profileImageUrl;

    public UserViewDto(Long id, String username, String firstName, String lastName, String bio, String location) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.location = location;
    }
}