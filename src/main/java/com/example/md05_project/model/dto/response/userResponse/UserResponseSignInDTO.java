package com.example.md05_project.model.dto.response.userResponse;

import com.example.md05_project.model.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseSignInDTO {
    private String token;
//    private Long id;
//    private String fullName;
    private String username;
//    private String email;
//    private boolean status;
//    private String address;
//    private String phone;
    private Set<String> roles;
}

