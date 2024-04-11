package com.example.md05_project.controller.user;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.UserNotFoundException;
import com.example.md05_project.model.dto.request.CartRequestDTO;
import com.example.md05_project.model.dto.response.CartResponseDTO;
import com.example.md05_project.model.dto.response.GenreResponseDTO;
import com.example.md05_project.model.dto.response.userResponse.UserResponseDTO;
import com.example.md05_project.service.cart.CartService;
import com.example.md05_project.service.user.UserService;
import com.example.md05_project.validation.ValidateParamType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api.myservice.com/v1/user/cart")

public class CartController {
    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;
    @Autowired
    private ValidateParamType validateParamType;

    @GetMapping("")
    public ResponseEntity<?> getCartByUserId(HttpServletRequest request) throws UserNotFoundException, CustomException, BookException {
        UserResponseDTO userResponseDTO = userService.getAccount(request);

        List<CartResponseDTO> list = cartService.findAllByUserId(userResponseDTO.getId());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> addCart(@RequestParam("bookId") String bookId, HttpServletRequest request) throws UserNotFoundException, BookException, CustomException {
        Long parsedId = validateParamType.validateId(bookId);
        UserResponseDTO userResponseDTO = userService.getAccount(request);

        CartRequestDTO cartRequestDTO = CartRequestDTO.builder()
                .userId(userResponseDTO.getId())
                .bookId(parsedId)
                .build();
        CartResponseDTO cartResponseDTO = cartService.save(cartRequestDTO);

        Map<String, CartResponseDTO> response = new HashMap<>();
        response.put("Add new cart successfully", cartResponseDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> deleteCart(@PathVariable("cartId") String cartId) throws CustomException {
        Long parsedId = validateParamType.validateId(cartId);
        CartResponseDTO cartResponseDTO = cartService.findById(parsedId);
        cartService.delete(cartResponseDTO.getId());
        return new ResponseEntity<>("Delete successful", HttpStatus.OK);
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteCartByUserId(HttpServletRequest request) throws UserNotFoundException, CustomException, BookException {
        UserResponseDTO userResponseDTO = userService.getAccount(request);
        List<CartResponseDTO> list = cartService.findAllByUserId(userResponseDTO.getId());
        cartService.deleteByUserId(userResponseDTO.getId());
        return new ResponseEntity<>("Delete cart successful", HttpStatus.OK);
    }
}
