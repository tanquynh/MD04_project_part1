package com.example.md05_project.controller;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.GenreException;
import com.example.md05_project.model.dto.response.BookResponseDTO;
import com.example.md05_project.model.dto.response.GenreResponseDTO;
import com.example.md05_project.service.book.BookService;
import com.example.md05_project.service.genre.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api.myservice.com/v1")
public class HomeController {
    @Autowired
    private GenreService genreService;
    @Autowired
    private BookService bookService;

    @GetMapping("/genres")
    public ResponseEntity<?> getGenres(@RequestParam(name = "keyword", required = false) String keyword,
                                       @RequestParam(defaultValue = "5", name = "limit") int limit,
                                       @RequestParam(defaultValue = "0", name = "page") int page,
                                       @RequestParam(defaultValue = "id", name = "sort") String sort,
                                       @RequestParam(defaultValue = "acs", name = "order") String order) {
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, limit, Sort.by(direction, sort));
        Page<GenreResponseDTO> categoryPage;

        if (keyword != null && !keyword.isEmpty()) {
            categoryPage = genreService.searchByNameWithPaginationAndSort(pageable, keyword);
        } else {
            categoryPage = genreService.findAllWithPaginationAndSort(pageable);
        }

        if (categoryPage == null || categoryPage.isEmpty()) {
            return new ResponseEntity<>("Don't have any genres.", HttpStatus.OK);
        }
        return new ResponseEntity<>(categoryPage, HttpStatus.OK);
    }

    @GetMapping("/genres/{id}")
    public ResponseEntity<?> getGenreById(@PathVariable Long id) throws BookException {
        List<BookResponseDTO> list = bookService.getBooksByGenreId(id);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/books")
    public ResponseEntity<?> getBooks(@RequestParam(name = "keyword", required = false) String keyword,
                                      @RequestParam(defaultValue = "5", name = "limit") int limit,
                                      @RequestParam(defaultValue = "0", name = "page") int page,
                                      @RequestParam(defaultValue = "id", name = "sort") String sort,
                                      @RequestParam(defaultValue = "acs", name = "order") String order) {
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(direction, sort));
        Page<BookResponseDTO> bookPage;

        if (keyword != null) {
            bookPage = bookService.searchByNameWithPaginationAndSort(pageable, keyword);
        } else {
            bookPage = bookService.findAllWithPaginationAndSort(pageable);
        }

        if (bookPage == null || bookPage.isEmpty()) {
            return new ResponseEntity<>("Don't have any books.", HttpStatus.OK);
        }
        return new ResponseEntity<>(bookPage, HttpStatus.OK);
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) throws BookException {
        BookResponseDTO bookResponseDTO = bookService.findById(id);
        return new ResponseEntity<>(bookResponseDTO, HttpStatus.OK);
    }

}
