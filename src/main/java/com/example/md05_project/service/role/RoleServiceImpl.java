package com.example.md05_project.service.role;

import com.example.md05_project.exception.CustomException;
import com.example.md05_project.model.entity.Role;
import com.example.md05_project.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role findRoleByName(String name) {
        return roleRepository.findRoleByName(name);
    }

    @Override
    public List<String> findAll() throws CustomException {
        List<Role> list = roleRepository.findAll();
        if (list.isEmpty()) {
            throw new CustomException("Don't have any role");
        }
        return list.stream().map(Role::getName).toList();
    }
}
