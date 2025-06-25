package com.example.demo.dto;

import lombok.Data;

@Data
public class UserDto {
    private String firstName;
    private String lastName;
    private String username;
    private String gender;
    private String dateOfBirth;
    private String phoneNumber;
    private String email;
    private String password;
}