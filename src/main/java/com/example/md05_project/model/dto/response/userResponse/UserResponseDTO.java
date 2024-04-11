package com.example.md05_project.model.dto.response.userResponse;

import com.example.md05_project.model.entity.Role;
import com.example.md05_project.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private boolean status;
    private String address;
    private String phone;
    private Set<Role> roles;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.status = user.isStatus();
        this.address = user.getAddress();
        this.phone = user.getPhone();
        this.roles = user.getRoles();
    }
}
