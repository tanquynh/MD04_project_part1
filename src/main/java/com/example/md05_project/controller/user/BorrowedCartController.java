package com.example.md05_project.controller.user;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.UserNotFoundException;
import com.example.md05_project.model.dto.request.BorrowedCartRequestDTO;
import com.example.md05_project.model.dto.response.BorrowedCartDetailDTO;
import com.example.md05_project.model.dto.response.BorrowedCartResponseDTO;
import com.example.md05_project.model.dto.response.GenreResponseDTO;
import com.example.md05_project.model.dto.response.userResponse.UserResponseDTO;
import com.example.md05_project.model.entity.BorrowedCartStatus;
import com.example.md05_project.service.borrowedCart.BorrowedCartService;
import com.example.md05_project.service.borrowedCartDetail.BorrowedCartDetailService;
import com.example.md05_project.service.cart.CartService;
import com.example.md05_project.service.user.UserService;
import com.example.md05_project.validation.ValidateParamType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api.myservice.com/v1/user/borrowed-cart")

public class BorrowedCartController {
    @Autowired
    private BorrowedCartService borrowedCartService;
    @Autowired
    private BorrowedCartDetailService borrowedCartDetailService;
    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ValidateParamType validateParamType;

    @GetMapping("")
    public ResponseEntity<?> getAllByUserIdWithPaginationAndSearchAndSort(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "5", name = "limit") int limit,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "id", name = "sort") String sort,
            @RequestParam(defaultValue = "acs", name = "order") String order,
            HttpServletRequest request) throws UserNotFoundException, BookException, CustomException {
        UserResponseDTO userResponseDTO = userService.getAccount(request);
        Page<BorrowedCartResponseDTO> borrowedCartResponseDTOPage;
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, limit, Sort.by(direction, sort));
        if (keyword != null && !keyword.isEmpty()) {
            BorrowedCartStatus status=validateParamType.validateBorrowedCartStatus(keyword);
            borrowedCartResponseDTOPage = borrowedCartService.findAllByUserIdAndStatus(userResponseDTO.getId(), status, pageable);
        } else {
            borrowedCartResponseDTOPage = borrowedCartService.findAllByUserId(userResponseDTO.getId(), pageable);
        }

        if (borrowedCartResponseDTOPage == null || borrowedCartResponseDTOPage.isEmpty()) {
            return new ResponseEntity<>("Dont have any borrowed carts", HttpStatus.OK);
        }

        return new ResponseEntity<>(borrowedCartResponseDTOPage, HttpStatus.OK);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<?> getById(HttpServletRequest request,@PathVariable("cartId") String cartId) throws CustomException, UserNotFoundException {
        Long parsedId = validateParamType.validateId(cartId);

        List<BorrowedCartDetailDTO> list = borrowedCartDetailService.findByCartIdOfUser(request,parsedId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> addBorrowedCart(@RequestBody BorrowedCartRequestDTO borrowedCartRequestDTO, HttpServletRequest request)
            throws UserNotFoundException, CustomException, BookException {
        UserResponseDTO userResponseDTO = userService.getAccount(request);
        BorrowedCartResponseDTO borrowedCartResponseDTO = borrowedCartService.save(borrowedCartRequestDTO,request);
        cartService.deleteByUserId(userResponseDTO.getId());

        Map<String, BorrowedCartResponseDTO> response=new HashMap<>();
        response.put("Add new borrowed cart genre successfully",borrowedCartResponseDTO);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/return/{cartId}")
    public ResponseEntity<?> returnCart(@PathVariable("cartId") String cartId) throws BookException, CustomException {
        Long parsedId = validateParamType.validateId(cartId);
        BorrowedCartResponseDTO borrowedCartResponseDTO = borrowedCartService.returnBook(parsedId);

        Map<String, BorrowedCartResponseDTO> response=new HashMap<>();
        response.put("Return borrowed cart genre successfully",borrowedCartResponseDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
