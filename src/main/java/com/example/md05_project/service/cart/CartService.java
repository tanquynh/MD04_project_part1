package com.example.md05_project.service.cart;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.UserNotFoundException;
import com.example.md05_project.model.dto.request.CartRequestDTO;
import com.example.md05_project.model.dto.response.CartResponseDTO;

import java.util.List;

public interface CartService {
    CartResponseDTO findBookById(Long bookId, Long userId);
    List<CartResponseDTO> findAllByUserId(Long userId) throws CustomException;
    void deleteByUserId(Long userId);
    CartResponseDTO findById(Long id) throws CustomException;
    void delete(Long id);
    CartResponseDTO save(CartRequestDTO cartRequestDTO) throws UserNotFoundException, BookException, CustomException;
}
