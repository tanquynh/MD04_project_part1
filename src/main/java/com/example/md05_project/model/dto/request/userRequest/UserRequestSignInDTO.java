package com.example.md05_project.model.dto.request.userRequest;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestSignInDTO {
    @NotEmpty(message = "Please fill fullName!")
    private String username;

    @NotEmpty(message = "Please fill password!")
    @Size(min = 4,max = 8,message = "Password's length must be between 4 and 8")
    private String password;
}
