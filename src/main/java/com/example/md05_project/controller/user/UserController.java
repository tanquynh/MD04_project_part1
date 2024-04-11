package com.example.md05_project.controller.user;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.UserNotFoundException;
import com.example.md05_project.model.dto.request.userRequest.UserRequestUpdateDTO;
import com.example.md05_project.model.dto.response.userResponse.UserResponseDTO;
import com.example.md05_project.security.jwt.JWTProvider;
import com.example.md05_project.security.jwt.JWTTokenFilter;
import com.example.md05_project.service.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api.myservice.com/v1/user/account")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<?> getAccount(HttpServletRequest request) throws UserNotFoundException {
        UserResponseDTO userResponseDTO=userService.getAccount(request);

        return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<?>updateAccount(@RequestBody @Valid UserRequestUpdateDTO userRequestUpdateDTO,
                                            HttpServletRequest request,
                                            @RequestParam("password")String password) throws UserNotFoundException {
        UserResponseDTO userResponseDTO=userService.updateAccount(userRequestUpdateDTO, request,password);

        Map<String, Object> response = new HashMap<>();
        response.put("Update account successfully",userResponseDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?>changePassword(@RequestParam("oldPass")String oldPass,
                                           @RequestParam("newPass")String newPass,
                                           @RequestParam("confirmPass")String confirmPass,
                                           HttpServletRequest request) throws UserNotFoundException, BookException, CustomException, ServletException {
        userService.changePassword(oldPass, newPass, confirmPass, request);
        return new ResponseEntity<>("Change password successfully! Please log in", HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ResponseEntity<?>logout(HttpServletRequest request) throws ServletException, UserNotFoundException {
        userService.logout(request);
        return new ResponseEntity<>("Log out successfully!", HttpStatus.OK);
    }
}
