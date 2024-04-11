package com.example.md05_project.service.role;

import com.example.md05_project.exception.CustomException;
import com.example.md05_project.model.entity.Role;

import java.util.List;

public interface RoleService {
    Role findRoleByName(String name);
    List<String> findAll() throws CustomException;
}
