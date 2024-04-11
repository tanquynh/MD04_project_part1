package com.example.md05_project.controller.admin;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.GenreException;
import com.example.md05_project.model.dto.request.BookRequestDTO;
import com.example.md05_project.model.dto.response.BookResponseDTO;
import com.example.md05_project.model.dto.response.GenreResponseDTO;
import com.example.md05_project.service.book.BookService;
import com.example.md05_project.validation.ValidateParamType;
import jakarta.validation.Valid;
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
@RequestMapping("/api.myservice.com/v1/admin/books")
public class BookController {
    @Autowired
    private BookService bookService;
    @Autowired
    private ValidateParamType validateParamType;

    @GetMapping("")
    public ResponseEntity<?> getBooksWithPaginationAndSortAndSearch(@RequestParam(name = "keyword", required = false) String keyword,
                                      @RequestParam(defaultValue = "5", name = "limit") int limit,
                                      @RequestParam(defaultValue = "0", name = "page") int page,
                                      @RequestParam(defaultValue = "id", name = "sort") String sort,
                                      @RequestParam(defaultValue = "acs", name = "order") String order) {
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(direction, sort));
        Page<BookResponseDTO> bookPage;

        if (keyword != null && !keyword.isEmpty()) {
            bookPage = bookService.searchByNameWithPaginationAndSort(pageable, keyword);
        } else {
            bookPage = bookService.findAllWithPaginationAndSort(pageable);
        }

        if (bookPage == null || bookPage.isEmpty()) {
            return new ResponseEntity<>("Don't have any books.", HttpStatus.OK);
        }
        return new ResponseEntity<>(bookPage, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable String id) throws BookException, CustomException {
        Long parsedId = validateParamType.validateId(id);
        BookResponseDTO bookResponseDTO = bookService.findById(parsedId);
        return new ResponseEntity<>(bookResponseDTO, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> addBook(@Valid @ModelAttribute("book") BookRequestDTO bookRequestDTO) throws GenreException, BookException, CustomException {
        BookResponseDTO bookResponseDTO = bookService.saveOrUpdate(bookRequestDTO);
        Map<String, BookResponseDTO> response = new HashMap<>();
        response.put("Add new book successfully", bookResponseDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editBook(@PathVariable String id, @Valid @ModelAttribute("book") BookRequestDTO bookRequestDTO) throws GenreException, BookException, CustomException {
        Long parsedId = validateParamType.validateId(id);
        BookResponseDTO editBook = bookService.findById(parsedId);

        bookRequestDTO.setId(editBook.getId());
        BookResponseDTO bookResponseDTO = bookService.saveOrUpdate(bookRequestDTO);

        Map<String, BookResponseDTO> response = new HashMap<>();
        response.put("Edit book with id " + id + " successfully", bookResponseDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable String id) throws BookException, CustomException {
        Long parsedId = validateParamType.validateId(id);
        BookResponseDTO editBook = bookService.findById(parsedId);

        bookService.changeStatus(editBook.getId());

        Map<String, BookResponseDTO> response = new HashMap<>();
        response.put("Change book status with id " + id + " successfully", editBook);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
