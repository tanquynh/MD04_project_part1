package com.example.md05_project.controller.admin;

import com.example.md05_project.exception.CustomException;
import com.example.md05_project.model.entity.Role;
import com.example.md05_project.service.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api.myservice.com/v1/admin/roles")

public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("")
    public ResponseEntity<?> getRoles() throws CustomException {
        List<String> list = roleService.findAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
