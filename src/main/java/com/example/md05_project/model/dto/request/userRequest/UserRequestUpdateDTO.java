package com.example.md05_project.model.dto.request.userRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestUpdateDTO {
    private Long id;

    @NotEmpty(message = "Please fill fullName!")
    private String fullName;

    @NotEmpty(message = "Please fill email!")
    @Email(message = "Email should be valid")
    private String email;

    @NotEmpty(message = "Please fill address!")
    @Pattern(regexp = "^(\\+?84|0)(3[2-9]|5[2689]|7[06-9]|8[1-689]|9[0-9])[0-9]{7}$", message = "Invalid phone number!")
    @Size(min = 10, max = 11, message = "Invalid phone number length!")
    private String phone;

    @NotEmpty(message = "Please fill address!")
    private String address;



}
