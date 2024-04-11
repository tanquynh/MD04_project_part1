package com.example.md05_project.model.dto.request.userRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestSignUpDTO {
    @NotEmpty(message = "Please fill fullName!")
    private String fullName;

    @NotEmpty(message = "Please fill username!")
    private String username;

    @Email(message = "Email should be valid")
    private String email;

    @NotEmpty(message = "Please fill password!")
    @Size(min = 4,max = 8,message = "Password's length must be between 4 and 8")
    private String password;
    private boolean status=true;
    private Set<String> roles;
}
