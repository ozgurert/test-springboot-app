package com.example.demo.dto;

import lombok.Data;
import java.util.Map;

@Data
public class ProfileUpdateDto {
    private String firstName;
    private String lastName;
    private String gender;
    private String dateOfBirth;
    private String bio;
    private String location;
    private String phoneNumber;
    private String email;

    private Map<String, Boolean> visibilitySettings;
}
