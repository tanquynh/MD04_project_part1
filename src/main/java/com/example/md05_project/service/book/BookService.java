package com.example.md05_project.service.book;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.GenreException;
import com.example.md05_project.model.dto.request.BookRequestDTO;
import com.example.md05_project.model.dto.response.BookResponseDTO;
import com.example.md05_project.model.dto.response.BorrowedCartResponseDTO;
import com.example.md05_project.model.dto.response.GenreResponseDTO;
import com.example.md05_project.model.entity.Book;
import com.example.md05_project.model.entity.BorrowedCart;
import com.example.md05_project.model.entity.BorrowedCartStatus;
import com.example.md05_project.model.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface BookService {
    Page<BookResponseDTO> findAllWithPaginationAndSort(Pageable pageable);

    Page<BookResponseDTO> searchByNameWithPaginationAndSort(Pageable pageable, String name);

    BookResponseDTO findById(Long id) throws BookException;

    BookResponseDTO saveOrUpdate(BookRequestDTO bookRequestDTO) throws BookException, GenreException, CustomException;
    void changeStatus(Long id) throws BookException;
    BookResponseDTO save(BookRequestDTO bookRequestDTO) throws BookException, GenreException, CustomException;
    void increaseStock(Long bookId);
    void decreaseStock(Long bookId) throws BookException;
    List<BookResponseDTO> getBooksByGenreId(Long genreId) throws BookException;
    List<BookResponseDTO>findAllByStatus(boolean status);
    Integer totalBorrowedBooks(List<BorrowedCartResponseDTO>list);

    List<BookResponseDTO>getBestBorrowedBooks(List<BorrowedCartResponseDTO>list) throws CustomException;

}
