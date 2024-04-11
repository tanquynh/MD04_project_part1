package com.example.md05_project.service.borrowedCart;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.UserNotFoundException;
import com.example.md05_project.model.dto.request.BorrowedCartRequestDTO;
import com.example.md05_project.model.dto.response.BorrowedCartResponseDTO;
import com.example.md05_project.model.dto.response.CartResponseDTO;
import com.example.md05_project.model.dto.response.WaitingRequestDetailDTO;
import com.example.md05_project.model.dto.response.userResponse.UserResponseDTO;
import com.example.md05_project.model.entity.*;
import com.example.md05_project.repository.*;
import com.example.md05_project.service.WaitingRequestDetail.WaitingRequestDetailService;
import com.example.md05_project.service.book.BookService;
import com.example.md05_project.service.cart.CartService;
import com.example.md05_project.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static com.example.md05_project.model.entity.BorrowedCartStatus.BORROWED;
import static com.example.md05_project.model.entity.BorrowedCartStatus.RETURNED;


@Service
public class BorrowedCartServiceImpl implements BorrowedCartService {

    @Value("${books.fine.per_day}")
    private double fine_per_day;
    @Autowired
    private BorrowedCartRepository borrowedCartRepository;

    @Autowired
    private CartService cartService;
    @Autowired
    private BorrowedCartDetailRepository borrowedCartDetailRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WaitingRequestRepository waitingRequestRepository;

    @Autowired
    private WaitingRequestDetailService waitingRequestDetailService;
    @Autowired
    private UserService userService;

    @Override
    public Page<BorrowedCartResponseDTO> findAllByBorrowedCartStatus(Pageable pageable, BorrowedCartStatus borrowedCartStatus) {
        Page<BorrowedCart> cartPage = borrowedCartRepository.findAllByBorrowedCartStatus(pageable, borrowedCartStatus);
        return cartPage.map(BorrowedCartResponseDTO::new);
    }

    @Override
    public Page<BorrowedCartResponseDTO> findAllByUserId(Long userId, Pageable pageable) {
        Page<BorrowedCart> cartPage = borrowedCartRepository.findAllByUser_Id(userId, pageable);

        return cartPage.map(cart -> {
            BorrowedCartResponseDTO responseDTO = BorrowedCartResponseDTO.builder()
                    .borrowedCartStatus(cart.getBorrowedCartStatus())
                    .userId(cart.getUser().getId())
                    .id(cart.getId())
                    .count(cart.getCount())
                    .note(cart.getNote())
                    .startDate(cart.getStartDate())
                    .endDate(cart.getEndDate())
                    .build();
            try {
                responseDTO.setFine(calculateFine(cart.getId())); // Tính toán lại giá trị fine
                borrowedCartRepository.save(BorrowedCart.builder()
                        .borrowedCartStatus(responseDTO.getBorrowedCartStatus())
                        .fine(responseDTO.getFine())
                        .id(responseDTO.getId())
                        .startDate(responseDTO.getStartDate())
                        .endDate(responseDTO.getEndDate())
                        .note(responseDTO.getNote())
                        .user(userRepository.findById(responseDTO.getUserId()).orElse(null))
                        .count(responseDTO.getCount())
                        .build());

                //truong hop phieu muon co tien phat duong va status la BORROWED thi khoa luon user
                if (responseDTO.getFine() > 0 && responseDTO.getBorrowedCartStatus().equals(BorrowedCartStatus.BORROWED)) {
                    userRepository.blockUser(responseDTO.getUserId());
                }
            } catch (CustomException e) {
                throw new RuntimeException(e);
            }
            return responseDTO;
        });
    }

    @Override
    public Page<BorrowedCartResponseDTO> findAll(Pageable pageable) {
        Page<BorrowedCart> cartPage = borrowedCartRepository.findAll(pageable);
        return cartPage.map(cart -> {
            BorrowedCartResponseDTO responseDTO = BorrowedCartResponseDTO.builder()
                    .borrowedCartStatus(cart.getBorrowedCartStatus())
                    .userId(cart.getUser().getId())
                    .id(cart.getId())
                    .count(cart.getCount())
                    .note(cart.getNote())
                    .startDate(cart.getStartDate())
                    .endDate(cart.getEndDate())
                    .build();
            try {
                responseDTO.setFine(calculateFine(cart.getId())); // Tính toán lại giá trị fine
                borrowedCartRepository.save(BorrowedCart.builder()
                        .borrowedCartStatus(responseDTO.getBorrowedCartStatus())
                        .fine(responseDTO.getFine())
                        .id(responseDTO.getId())
                        .startDate(responseDTO.getStartDate())
                        .endDate(responseDTO.getEndDate())
                        .note(responseDTO.getNote())
                        .user(userRepository.findById(responseDTO.getUserId()).orElse(null))
                        .count(responseDTO.getCount())
                        .build());

                //truong hop phieu muon co tien phat duong va status la BORROWED thi khoa luon user
                if (responseDTO.getFine() > 0 && responseDTO.getBorrowedCartStatus().equals(BorrowedCartStatus.BORROWED)) {
                    userRepository.blockUser(responseDTO.getUserId());
                }
            } catch (CustomException e) {
                throw new RuntimeException(e);
            }

            return responseDTO;
        });
    }

    @Override
    public BorrowedCartResponseDTO changeStatus(BorrowedCartStatus status, Long id) throws CustomException {
        BorrowedCartResponseDTO borrowedCartResponseDTO = findById(id);
        borrowedCartRepository.changeStatus(status, id);
        return borrowedCartResponseDTO;
    }

    @Override
    public BorrowedCartResponseDTO save(BorrowedCartRequestDTO borrowedCartRequestDTO, HttpServletRequest request) throws UserNotFoundException, CustomException, BookException {
        UserResponseDTO user = userService.getAccount(request);

        List<CartResponseDTO> list = cartService.findAllByUserId(user.getId());

        BorrowedCart borrowedCart = borrowedCartRepository.save(BorrowedCart.builder()
                .borrowedCartStatus(borrowedCartRequestDTO.getBorrowedCartStatus())
                .fine(borrowedCartRequestDTO.getFine())
                .id(borrowedCartRequestDTO.getId())
                .startDate(borrowedCartRequestDTO.getStartDate())
                .endDate(borrowedCartRequestDTO.getEndDate())
                .note(borrowedCartRequestDTO.getNote())
                .user(userRepository.findByUsername(user.getUsername()))
                .count(list.size())
                .build());

        BorrowedCartResponseDTO borrowedCartResponseDTO = new BorrowedCartResponseDTO(borrowedCart);

        List<BorrowedCartDetail> detailDTOList = list.stream().map(detail -> BorrowedCartDetail.builder()
                .id(detail.getId())
                .borrowedCart(borrowedCartRepository.findById(borrowedCartResponseDTO.getId()).orElse(null))
                .book(bookRepository.findBookByTitle(detail.getBook()))
                .build()).toList();

        //luu vao detail
        borrowedCartDetailRepository.saveAll(detailDTOList);

        //tru luong stock cua book
        for (BorrowedCartDetail borrowedCartDetail : detailDTOList) {
            for (Book book : bookRepository.findAll()) {
                if (borrowedCartDetail.getBook().getId().equals(book.getId())) {
                    bookService.decreaseStock(book.getId());
                }
            }
        }

        return borrowedCartResponseDTO;
    }

    @Override
    public BorrowedCartResponseDTO saveFromWaitingList(Long requestId) throws CustomException, BookException {
        WaitingRequest waitingRequest = waitingRequestRepository.findById(requestId).orElse(null);
        List<WaitingRequestDetailDTO> list = waitingRequestDetailService.findByRequestId(requestId);
        assert waitingRequest != null;
        BorrowedCart borrowedCart = borrowedCartRepository.save(BorrowedCart.builder()
                .borrowedCartStatus(BORROWED)
                .fine(0)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .user(waitingRequest.getUser())
                .count(list.size())
                .build());

        BorrowedCartResponseDTO borrowedCartResponseDTO = new BorrowedCartResponseDTO(borrowedCart);
        List<BorrowedCartDetail> detailDTOList = list.stream().map(detail -> BorrowedCartDetail.builder()
//                .id(detail.getId())
                .borrowedCart(borrowedCartRepository.findById(borrowedCartResponseDTO.getId()).orElse(null))
                .book(bookRepository.findBookByTitle(detail.getBook()))
                .build()).toList();

        //luu vao detail
        borrowedCartDetailRepository.saveAll(detailDTOList);

        //tru luong stock cua book
        for (BorrowedCartDetail borrowedCartDetail : detailDTOList) {
            for (Book book : bookRepository.findAll()) {
                if (borrowedCartDetail.getBook().getId().equals(book.getId())) {
                    bookService.decreaseStock(book.getId());
                }
            }
        }
        return borrowedCartResponseDTO;
    }

    @Override
    public BorrowedCartResponseDTO findById(Long id) throws CustomException {
        BorrowedCart borrowedCart = borrowedCartRepository.findById(id).orElseThrow(() ->
                new CustomException("Borrowed cart is not found with this id" + id));
        return new BorrowedCartResponseDTO(borrowedCart);
    }

    @Override
    public List<BorrowedCartResponseDTO> getAllByUserId(Long userId) throws BookException {
        List<BorrowedCart> list = borrowedCartRepository.findAllByUser_Id(userId);
        if (list.isEmpty()) {
            throw new BookException("Don't have any borrowed cart");
        }

        return list.stream().map(cart -> {
            BorrowedCartResponseDTO responseDTO = BorrowedCartResponseDTO.builder()
                    .borrowedCartStatus(cart.getBorrowedCartStatus())
                    .userId(cart.getUser().getId())
                    .id(cart.getId())
                    .count(cart.getCount())
                    .note(cart.getNote())
                    .startDate(cart.getStartDate())
                    .endDate(cart.getEndDate())
                    .build();
            try {
                responseDTO.setFine(calculateFine(cart.getId())); // Tính toán lại giá trị fine
            } catch (CustomException e) {
                throw new RuntimeException(e);
            }

            return responseDTO;
        }).toList();
    }

    @Override
    public BorrowedCartResponseDTO returnCart(BorrowedCartRequestDTO borrowedCartRequestDTO) {

        return null;
    }

    @Override
    public BorrowedCartResponseDTO returnBook(Long cartId) throws CustomException, BookException {
        BorrowedCartResponseDTO borrowedCartResponseDTO = findById(cartId);

        List<BorrowedCartDetail> detailDTOList = borrowedCartDetailRepository.findAllByBorrowedCart_Id(borrowedCartResponseDTO.getId());
        //tra lai luong stock cua book da muon
        for (BorrowedCartDetail borrowedCartDetail : detailDTOList) {
            for (Book book : bookRepository.findAll()) {
                if (borrowedCartDetail.getBook().getId().equals(book.getId())) {
                    bookService.increaseStock(book.getId());
                }
            }
        }
        borrowedCartRepository.changeStatus(RETURNED, borrowedCartResponseDTO.getId());
        return borrowedCartResponseDTO;
    }

    @Override
    public Double calculateFine(Long cartId) throws CustomException {
        double fine = (double) 0;
        BorrowedCartResponseDTO borrowedCartResponseDTO = findById(cartId);

//        //cach 1:
//        if (borrowedCartResponseDTO.getEndDate().isBefore(LocalDate.now())) {
//            int overDays = (int) ChronoUnit.DAYS.between(borrowedCartResponseDTO.getEndDate(), LocalDate.now());
//            fine = overDays * fine_per_day;
//        }

        //cach 2:
        if (borrowedCartResponseDTO.getBorrowedCartStatus().equals(BORROWED)) {
            Period period = Period.between(borrowedCartResponseDTO.getEndDate(), LocalDate.now());
            int overDays = period.getDays();
            if (overDays > 0) {
                fine = overDays * fine_per_day;
            }
        }
        return fine;
    }

    @Override
    public List<BorrowedCartResponseDTO> getCartByDate(LocalDate fromDate, LocalDate toDate) {
        List<BorrowedCart> list = borrowedCartRepository.findAllByStartDateIsBetween(fromDate, toDate);
        return list.stream().map(BorrowedCartResponseDTO::new).toList();
    }

    @Override
    public List<BorrowedCartResponseDTO> getCartByDateAndStatus(LocalDate fromDate, LocalDate toDate, BorrowedCartStatus status) {
        List<BorrowedCart> borrowedCarts = borrowedCartRepository.findAllByStartDateIsBetweenAndBorrowedCartStatus(fromDate, toDate, status);
        return borrowedCarts.stream().map(BorrowedCartResponseDTO::new).toList();
    }

    @Override
    public Integer countAllByBorrowedCartStatus(BorrowedCartStatus status) {
        return borrowedCartRepository.countAllByBorrowedCartStatus(status);
    }

    @Override
    public Page<BorrowedCartResponseDTO> findAllByUserIdAndStatus(Long userId, BorrowedCartStatus status, Pageable pageable) {
        Page<BorrowedCart> cartPage = borrowedCartRepository.findAllByUser_IdAndBorrowedCartStatus(userId,status, pageable);

        return cartPage.map(cart -> {
            BorrowedCartResponseDTO responseDTO = BorrowedCartResponseDTO.builder()
                    .borrowedCartStatus(cart.getBorrowedCartStatus())
                    .userId(cart.getUser().getId())
                    .id(cart.getId())
                    .count(cart.getCount())
                    .note(cart.getNote())
                    .startDate(cart.getStartDate())
                    .endDate(cart.getEndDate())
                    .build();
            try {
                responseDTO.setFine(calculateFine(cart.getId())); // Tính toán lại giá trị fine
                borrowedCartRepository.save(BorrowedCart.builder()
                        .borrowedCartStatus(responseDTO.getBorrowedCartStatus())
                        .fine(responseDTO.getFine())
                        .id(responseDTO.getId())
                        .startDate(responseDTO.getStartDate())
                        .endDate(responseDTO.getEndDate())
                        .note(responseDTO.getNote())
                        .user(userRepository.findById(responseDTO.getUserId()).orElse(null))
                        .count(responseDTO.getCount())
                        .build());

                //truong hop phieu muon co tien phat duong va status la BORROWED thi khoa luon user
                if (responseDTO.getFine() > 0 && responseDTO.getBorrowedCartStatus().equals(BorrowedCartStatus.BORROWED)) {
                    userRepository.blockUser(responseDTO.getUserId());
                }
            } catch (CustomException e) {
                throw new RuntimeException(e);
            }
            return responseDTO;
        });
    }
}
