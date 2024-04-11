package com.example.md05_project.controller.admin;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.UserNotFoundException;
import com.example.md05_project.model.dto.response.GenreResponseDTO;
import com.example.md05_project.model.dto.response.userResponse.UserResponseDTO;
import com.example.md05_project.model.entity.User;
import com.example.md05_project.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api.myservice.com/v1/admin/users")

public class UserAdminController {
    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<?> getUsers(@RequestParam(name = "keyword", required = false) String keyword,
                                      @RequestParam(defaultValue = "0", name = "page") int page,
                                      @RequestParam(defaultValue = "5", name = "limit") int limit,
                                      @RequestParam(defaultValue = "id", name = "sort") String sort,
                                      @RequestParam(defaultValue = "asc", name = "order") String order) throws BookException, CustomException {
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(direction, sort));
        Page<UserResponseDTO> userResponseDTOPage;

        if (keyword != null && !keyword.isEmpty()) {
            userResponseDTOPage = userService.searchByUsernameWithPaginationAndSort(pageable, keyword);
        } else {
            userResponseDTOPage = userService.findAll(pageable);
        }

        if (userResponseDTOPage == null || userResponseDTOPage.isEmpty()) {
            return new ResponseEntity<>("Don't have any users.", HttpStatus.OK);
        }

        return new ResponseEntity<>(userResponseDTOPage, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) throws UserNotFoundException, BookException, CustomException {
        UserResponseDTO userResponseDTO = userService.findById(id);
        return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) throws UserNotFoundException, BookException, CustomException {

        UserResponseDTO userResponseDTO = userService.changeStatus(id);

        Map<String, UserResponseDTO> response=new HashMap<>();
        response.put("Change user status with id "+id+" successfully",userResponseDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{id}/add-role")
    public ResponseEntity<?> addRole(@PathVariable Long id, @RequestParam("role") String role) throws UserNotFoundException, BookException, CustomException {
        UserResponseDTO userResponseDTO = userService.addRole(role, id);
        Map<String, UserResponseDTO> response=new HashMap<>();
        response.put("Add user's role with id "+id+" successfully",userResponseDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{id}/delete-role")
    public ResponseEntity<?> deleteRole(@PathVariable Long id, @RequestParam("role") String role) throws UserNotFoundException, BookException, CustomException {
        UserResponseDTO userResponseDTO = userService.deleteRole(role, id);
        Map<String, UserResponseDTO> response=new HashMap<>();
        response.put("Delete user's role with id "+id+" successfully",userResponseDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
