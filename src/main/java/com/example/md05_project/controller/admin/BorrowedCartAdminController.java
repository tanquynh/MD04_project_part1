package com.example.md05_project.controller.admin;

import com.example.md05_project.exception.CustomException;
import com.example.md05_project.model.dto.response.BorrowedCartDetailDTO;
import com.example.md05_project.model.dto.response.BorrowedCartResponseDTO;
import com.example.md05_project.model.entity.BorrowedCartStatus;
import com.example.md05_project.service.borrowedCart.BorrowedCartService;
import com.example.md05_project.service.borrowedCartDetail.BorrowedCartDetailService;
import com.example.md05_project.validation.ValidateParamType;
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
@RequestMapping("/api.myservice.com/v1/admin/borrowed-cart")

public class BorrowedCartAdminController {
    @Autowired
    private BorrowedCartService borrowedCartService;
    @Autowired
    private BorrowedCartDetailService borrowedCartDetailService;
    @Autowired
    private ValidateParamType validateParamType;

    @GetMapping("")
    public ResponseEntity<?> getAll(@RequestParam(name = "keyword", required = false) String status,
                                    @RequestParam(defaultValue = "5", name = "limit") int limit,
                                    @RequestParam(defaultValue = "0", name = "page") int page,
                                    @RequestParam(defaultValue = "id", name = "sort") String sort,
                                    @RequestParam(defaultValue = "acs", name = "order") String order) throws CustomException {

        Page<BorrowedCartResponseDTO> borrowedCartResponseDTOPage;
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(direction, sort));

        if (status != null && !status.isEmpty()) {
            BorrowedCartStatus parsedStatus = validateParamType.validateBorrowedCartStatus(status);
            borrowedCartResponseDTOPage = borrowedCartService.findAllByBorrowedCartStatus(pageable, parsedStatus);
        } else {
            borrowedCartResponseDTOPage = borrowedCartService.findAll(pageable);
        }
        if (borrowedCartResponseDTOPage == null || borrowedCartResponseDTOPage.isEmpty()) {
            return new ResponseEntity<>("Don't have any borrowed cart", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(borrowedCartResponseDTOPage, HttpStatus.OK);
        }
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<?> getCartDetail(@PathVariable Long cartId) throws CustomException {
        BorrowedCartResponseDTO borrowedCartResponseDTO = borrowedCartService.findById(cartId);

        List<BorrowedCartDetailDTO> list = borrowedCartDetailService.findByCartId(borrowedCartResponseDTO.getId());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
