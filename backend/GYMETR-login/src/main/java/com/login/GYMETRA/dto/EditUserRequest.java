package com.login.GYMETRA.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String photoUrl;
    private String password;  // Optional - only if user wants to change password
    private Long roleId;     // Optional - to change user's role
}