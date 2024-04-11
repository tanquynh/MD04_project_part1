package com.example.md05_project.controller;

import com.example.md05_project.exception.UserNotFoundException;
import com.example.md05_project.model.dto.request.userRequest.UserRequestSignInDTO;
import com.example.md05_project.model.dto.request.userRequest.UserRequestSignUpDTO;
import com.example.md05_project.model.dto.response.userResponse.UserResponseDTO;
import com.example.md05_project.model.dto.response.userResponse.UserResponseSignInDTO;
import com.example.md05_project.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api.myservice.com/v1/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @PostMapping("/login")
    public ResponseEntity<?>login(@RequestBody @Valid UserRequestSignInDTO userRequestSignInDTO) throws UserNotFoundException {
        UserResponseDTO user=userService.findByUsername(userRequestSignInDTO.getUsername());

        UserResponseSignInDTO userResponseSignInDTO=userService.login(userRequestSignInDTO);
        return new ResponseEntity<>(userResponseSignInDTO,HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?>register(@RequestBody @Valid UserRequestSignUpDTO userRequestSignUpDTO) throws UserNotFoundException {
       UserResponseDTO userResponseDTO= userService.register(userRequestSignUpDTO);
        Map<String,UserResponseDTO> response=new HashMap<>();
        response.put("Register successfully, please login",userResponseDTO);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

}
