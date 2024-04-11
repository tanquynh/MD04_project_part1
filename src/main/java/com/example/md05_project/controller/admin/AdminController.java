package com.example.md05_project.controller.admin;

import com.example.md05_project.exception.UserNotFoundException;
import com.example.md05_project.validation.ValidateParamType;
import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.model.dto.response.BookResponseDTO;
import com.example.md05_project.model.dto.response.BorrowedCartResponseDTO;
import com.example.md05_project.model.dto.response.GenreResponseDTO;
import com.example.md05_project.model.entity.BorrowedCartStatus;
import com.example.md05_project.service.book.BookService;
import com.example.md05_project.service.borrowedCart.BorrowedCartService;
import com.example.md05_project.service.genre.GenreService;
import com.example.md05_project.service.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api.myservice.com/v1/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private GenreService genreService;
    @Autowired
    private BookService bookService;
    @Autowired
    private BorrowedCartService borrowedCartService;
    @Autowired
    private ValidateParamType validateParamType;

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) throws ServletException, UserNotFoundException {
        userService.logout(request);
        return new ResponseEntity<>("Log out successfully!", HttpStatus.OK);
    }

    @GetMapping("/dash-board/genres")
    public ResponseEntity<?> dashboardGenres(@RequestParam(value = "genreId", required = false) String genreId) throws BookException, CustomException {
        List<GenreResponseDTO> trueGenre = genreService.findAllByStatus(true);
        List<GenreResponseDTO> falseGenre = genreService.findAllByStatus(false);

        Map<String, Integer> response = new LinkedHashMap<>();

        response.put("Total of genres", trueGenre.size() + falseGenre.size());
        response.put("True genres", trueGenre.size());
        response.put("False genres", falseGenre.size());

        if (genreId != null) {
            Long id= validateParamType.validateId(genreId);
            List<BookResponseDTO> listBookByGenreId = bookService.getBooksByGenreId(id);
            response.put("List of Books By GenreId", listBookByGenreId.size());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/dash-board/books")
    public ResponseEntity<?> dashboardBooks(@RequestParam(name = "fromDate", required = false) String fromDate,
                                            @RequestParam(name = "toDate", defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate toDate) throws CustomException {
        //Trong Spring Framework, có thể sử dụng SpEL (Spring Expression Language) để đánh giá các biểu thức phức tạp trong các annotation như @RequestParam
        //#{...}: Đây là cú pháp để đánh dấu một biểu thức SpEL.
        //T(java.time.LocalDate): Đây là cách để truy cập vào một lớp trong biểu thức SpEL

        List<BookResponseDTO> trueBooks = bookService.findAllByStatus(true);
        List<BookResponseDTO> falseBooks = bookService.findAllByStatus(false);
        Map<String, String> response = new LinkedHashMap<>();

        response.put("Total of books: ", String.valueOf(trueBooks.size() + falseBooks.size()));
        response.put("True books: ", String.valueOf(trueBooks.size()));
        response.put("False books: ", String.valueOf(falseBooks.size()));

        if (fromDate != null) {
            LocalDate parsedFromDate = validateParamType.validateDate(fromDate);
            String totalNoti = "Total of books borrowed from " + parsedFromDate + " to " + toDate + ": ";
            List<BorrowedCartResponseDTO> totalBorrowedBooks = borrowedCartService.getCartByDate(parsedFromDate, toDate);

            int totalBooks = bookService.totalBorrowedBooks(totalBorrowedBooks);
            response.put(totalNoti, String.valueOf(totalBooks));

            List<BookResponseDTO> bookResponseDTOList = bookService.getBestBorrowedBooks(totalBorrowedBooks);
            response.put("Number of best borrowed books: ", String.valueOf(bookResponseDTOList.size()));

            for (int i = 0; i < bookResponseDTOList.size(); i++) {
                response.put("Book No." + (i + 1), bookResponseDTOList.get(i).getTitle());
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/dash-board/borrowed-cart")
    public ResponseEntity<?> dashboardBorrowedCart(@RequestParam(name = "fromDate", required = false) String fromDate,
                                                   @RequestParam(name = "toDate", defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate toDate) throws CustomException {
        Map<String, String> response = new LinkedHashMap<>();
        int borrowedCart = borrowedCartService.countAllByBorrowedCartStatus(BorrowedCartStatus.BORROWED);
        int returnedCart = borrowedCartService.countAllByBorrowedCartStatus(BorrowedCartStatus.RETURNED);
        response.put("Number of borrowed cart " , String.valueOf(borrowedCart));
        response.put("Number of borrowed cart with status " + BorrowedCartStatus.BORROWED, String.valueOf(borrowedCart));
        response.put("Number of borrowed cart with status " + BorrowedCartStatus.RETURNED, String.valueOf(returnedCart));

        if (fromDate != null) {
            LocalDate parsedFromDate = validateParamType.validateDate(fromDate);
            String totalNoti = "Total of borrowed carts from " + parsedFromDate + " to " + toDate + ": ";
            List<BorrowedCartResponseDTO> totalBorrowedCart = borrowedCartService.getCartByDateAndStatus(parsedFromDate, toDate, BorrowedCartStatus.BORROWED);
            List<BorrowedCartResponseDTO> totalReturnedCart = borrowedCartService.getCartByDateAndStatus(parsedFromDate, toDate, BorrowedCartStatus.BORROWED);
            response.put(totalNoti, String.valueOf(totalBorrowedCart.size() + totalReturnedCart.size()));
            response.put("Number of borrowed cart with status " + BorrowedCartStatus.BORROWED + " from " + parsedFromDate + " to " + toDate + ": ", String.valueOf(totalBorrowedCart.size()));
            response.put("Number of borrowed cart with status " + BorrowedCartStatus.RETURNED + " from " + parsedFromDate + " to " + toDate + ": ", String.valueOf(totalReturnedCart.size()));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
