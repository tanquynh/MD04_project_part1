package com.example.md05_project.service.user;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.UserNotFoundException;
import com.example.md05_project.model.dto.request.userRequest.UserRequestSignInDTO;
import com.example.md05_project.model.dto.request.userRequest.UserRequestSignUpDTO;
import com.example.md05_project.model.dto.request.userRequest.UserRequestUpdateDTO;
import com.example.md05_project.model.dto.response.userResponse.UserResponseDTO;
import com.example.md05_project.model.dto.response.userResponse.UserResponseSignInDTO;
import com.example.md05_project.model.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDTO findByUsername(String username) throws UserNotFoundException;

    UserResponseDTO findById(Long id) throws UserNotFoundException, BookException, CustomException;

    UserResponseDTO update(Long id) throws UserNotFoundException;

    UserResponseDTO updateAccount(UserRequestUpdateDTO userRequestUpdateDTO, HttpServletRequest request, String password) throws UserNotFoundException;

    Page<UserResponseDTO> findAll(Pageable pageable) throws BookException, CustomException;

    Page<UserResponseDTO> searchByUsernameWithPaginationAndSort(Pageable pageable, String name) throws BookException, CustomException;

    UserResponseDTO register(UserRequestSignUpDTO user) throws UserNotFoundException;

    UserResponseSignInDTO login(UserRequestSignInDTO user) throws UserNotFoundException;

    UserResponseDTO changeStatus(Long id) throws UserNotFoundException, BookException, CustomException;

    UserResponseDTO addRole(String role, Long id) throws UserNotFoundException, BookException, CustomException;

    UserResponseDTO deleteRole(String role, Long id) throws UserNotFoundException, BookException, CustomException;

    void blockUserWhenReturnBooksExpire(User user) throws BookException, CustomException;

    UserResponseDTO getAccount(HttpServletRequest request) throws UserNotFoundException;

    void changePassword(String oldPassword, String newPassword, String confirmPassword, HttpServletRequest request) throws UserNotFoundException, BookException, CustomException, ServletException;

    void logout(HttpServletRequest request) throws ServletException, UserNotFoundException;
}
