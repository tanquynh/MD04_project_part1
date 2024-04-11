package com.example.md05_project.service.borrowedCart;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.UserNotFoundException;
import com.example.md05_project.model.dto.request.BorrowedCartRequestDTO;
import com.example.md05_project.model.dto.response.BorrowedCartResponseDTO;
import com.example.md05_project.model.entity.BorrowedCart;
import com.example.md05_project.model.entity.BorrowedCartStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface BorrowedCartService {
    Page<BorrowedCartResponseDTO> findAllByBorrowedCartStatus(Pageable pageable, BorrowedCartStatus borrowedCartStatus);
    Page<BorrowedCartResponseDTO> findAllByUserId(Long userId, Pageable pageable);
    Page<BorrowedCartResponseDTO> findAll(Pageable pageable);
    BorrowedCartResponseDTO changeStatus(BorrowedCartStatus status, Long id) throws CustomException;
    BorrowedCartResponseDTO save(BorrowedCartRequestDTO borrowedCartRequestDTO, HttpServletRequest request) throws UserNotFoundException, CustomException, BookException;
    BorrowedCartResponseDTO saveFromWaitingList(Long requestId) throws CustomException, BookException;
    BorrowedCartResponseDTO findById(Long id) throws CustomException;

    List<BorrowedCartResponseDTO>getAllByUserId(Long userId) throws CustomException, BookException;
    BorrowedCartResponseDTO returnCart(BorrowedCartRequestDTO borrowedCartRequestDTO);
    BorrowedCartResponseDTO returnBook(Long cartId) throws CustomException, BookException;
    Double calculateFine(Long cartId) throws CustomException;
    List<BorrowedCartResponseDTO>getCartByDate(LocalDate fromDate, LocalDate toDate);
    List<BorrowedCartResponseDTO>getCartByDateAndStatus(LocalDate fromDate,LocalDate toDate,BorrowedCartStatus status);

    Integer countAllByBorrowedCartStatus(BorrowedCartStatus status);
    Page<BorrowedCartResponseDTO> findAllByUserIdAndStatus(Long userId, BorrowedCartStatus status, Pageable pageable);
}
