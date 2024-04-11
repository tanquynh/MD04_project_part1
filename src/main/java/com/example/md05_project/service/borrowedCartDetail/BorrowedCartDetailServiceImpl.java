package com.example.md05_project.service.borrowedCartDetail;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.UserNotFoundException;
import com.example.md05_project.model.dto.response.BookResponseDTO;
import com.example.md05_project.model.dto.response.BorrowedCartDetailDTO;
import com.example.md05_project.model.dto.response.BorrowedCartResponseDTO;
import com.example.md05_project.model.dto.response.userResponse.UserResponseDTO;
import com.example.md05_project.model.entity.BorrowedCart;
import com.example.md05_project.model.entity.BorrowedCartDetail;
import com.example.md05_project.model.entity.User;
import com.example.md05_project.repository.BookRepository;
import com.example.md05_project.repository.BorrowedCartDetailRepository;
import com.example.md05_project.service.borrowedCart.BorrowedCartService;
import com.example.md05_project.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BorrowedCartDetailServiceImpl implements BorrowedCartDetailService {
    @Autowired
    private BorrowedCartDetailRepository borrowedCartDetailRepository;

    @Autowired
    private BorrowedCartService borrowedCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<BorrowedCartDetailDTO> findByCartId(Long cartId) throws CustomException {
        BorrowedCartResponseDTO borrowedCartResponseDTO = borrowedCartService.findById(cartId);
        List<BorrowedCartDetail> list = borrowedCartDetailRepository.findAllByBorrowedCart_Id(borrowedCartResponseDTO.getId());
        return list.stream().map(cart -> BorrowedCartDetailDTO.builder()
                .id(cart.getId())
                .borrowedCartId(cart.getBorrowedCart().getId())
                .book(cart.getBook().getTitle())
                .build()).toList();
    }

    @Override
    public BorrowedCartDetailDTO save(BorrowedCartDetailDTO borrowedCartDetailDTO) throws CustomException, UserNotFoundException, BookException {
        BorrowedCartResponseDTO borrowedCartResponseDTO = borrowedCartService.findById(borrowedCartDetailDTO.getBorrowedCartId());

        UserResponseDTO userResponseDTO = userService.findById(borrowedCartResponseDTO.getUserId());
        User user = User.builder()
                .id(userResponseDTO.getId())
                .roles(userResponseDTO.getRoles())
                .email(userResponseDTO.getEmail())
                .phone(userResponseDTO.getPhone())
                .address(userResponseDTO.getAddress())
                .fullName(userResponseDTO.getFullName())
                .username(userResponseDTO.getUsername())
                .status(userResponseDTO.isStatus())
                .build();

        BorrowedCart borrowedCart = BorrowedCart.builder()
                .count(borrowedCartResponseDTO.getCount())
                .borrowedCartStatus(borrowedCartResponseDTO.getBorrowedCartStatus())
                .fine(borrowedCartResponseDTO.getFine())
                .user(user)
                .note(borrowedCartResponseDTO.getNote())
                .startDate(borrowedCartResponseDTO.getStartDate())
                .endDate(borrowedCartResponseDTO.getEndDate())
                .id(borrowedCartDetailDTO.getId())
                .build();

        BorrowedCartDetail borrowedCartDetail = borrowedCartDetailRepository.save(BorrowedCartDetail.builder()
                .borrowedCart(borrowedCart)
                .book(bookRepository.findBookByTitle(borrowedCartDetailDTO.getBook()))
                .build());
        return new BorrowedCartDetailDTO(borrowedCartDetail);
    }

    @Override
    public BorrowedCartDetailDTO findById(Long id) throws CustomException {
        BorrowedCartDetail borrowedCartDetail = borrowedCartDetailRepository.findById(id).orElseThrow(() ->
                new CustomException("Don't find waiting request with this id " + id));
        return new BorrowedCartDetailDTO(borrowedCartDetail);
    }

    @Override
    public List<BorrowedCartDetailDTO> findByCartIdOfUser(HttpServletRequest request, Long cartId) throws UserNotFoundException, CustomException {
        UserResponseDTO userResponseDTO = userService.getAccount(request);
        BorrowedCartResponseDTO borrowedCartResponseDTO = borrowedCartService.findById(cartId);
        if (!borrowedCartResponseDTO.getUserId().equals(userResponseDTO.getId())) {
            throw new CustomException("Don't find list of borrowed cart detail with id " + cartId);
        }
        List<BorrowedCartDetail> list = borrowedCartDetailRepository.findAllByBorrowedCart_Id(borrowedCartResponseDTO.getId());
        return list.stream().map(cart -> BorrowedCartDetailDTO.builder()
                .id(cart.getId())
                .borrowedCartId(cart.getBorrowedCart().getId())
                .book(cart.getBook().getTitle())
                .build()).toList();
    }
}
