package com.example.md05_project.service.waitingRequest;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.model.dto.request.WaitingRequestDTO;
import com.example.md05_project.model.dto.response.BorrowedCartResponseDTO;
import com.example.md05_project.model.dto.response.WaitingListResponseDTO;
import com.example.md05_project.model.dto.response.WaitingRequestDetailDTO;
import com.example.md05_project.model.dto.response.WaitingRequestResponseDTO;
import com.example.md05_project.model.entity.*;
import com.example.md05_project.repository.BookRepository;
import com.example.md05_project.repository.UserRepository;
import com.example.md05_project.repository.WaitingRequestDetailRepository;
import com.example.md05_project.repository.WaitingRequestRepository;
import com.example.md05_project.service.WaitingRequestDetail.WaitingRequestDetailService;
import com.example.md05_project.service.borrowedCart.BorrowedCartService;
import com.example.md05_project.service.email.EmailService;
import com.example.md05_project.service.waitingList.WaitingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WaitingRequestServiceImpl implements WaitingRequestService {
    @Autowired
    private WaitingRequestRepository waitingRequestRepository;

    @Autowired
    private WaitingListService waitingListService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WaitingRequestDetailService waitingRequestDetailService;

    @Autowired
    private BorrowedCartService borrowedCartService;
    @Autowired
    private EmailService emailService;

    @Autowired
    private BookRepository bookRepository;
    @Override
    public Page<WaitingRequestResponseDTO> getAll(Pageable pageable) {
        Page<WaitingRequest> list = waitingRequestRepository.findAll(pageable);
        return list.map(WaitingRequestResponseDTO::new);
    }

    @Override
    public Page<WaitingRequestResponseDTO> findAllByWaitingRequestStatus(WaitingRequestStatus status, Pageable pageable) {
        Page<WaitingRequest> list = waitingRequestRepository.findAllByWaitingRequestStatus(status, pageable);
        return list.map(WaitingRequestResponseDTO::new);
    }

    @Override
    public Page<WaitingRequestResponseDTO> getByUserId(Long userId, Pageable pageable) {
        Page<WaitingRequest> list = waitingRequestRepository.findAllByUserId(userId, pageable);
        return list.map(WaitingRequestResponseDTO::new);
    }

    @Override
    public WaitingRequestResponseDTO save(WaitingRequestDTO waitingRequestDTO) throws CustomException {
        List<WaitingListResponseDTO> list = waitingListService.getAll(waitingRequestDTO.getUserId());
        Set<WaitingList> waitingList = list.stream().map(li -> WaitingList.builder()
                .id(li.getId())
                .book(bookRepository.findBookByTitle(li.getBook()))
                .user(userRepository.findById(li.getUserId()).orElse(null))
                .build()).collect(Collectors.toSet());

        WaitingRequest waitingRequest = waitingRequestRepository.save(WaitingRequest.builder()
                .waitingRequestStatus(waitingRequestDTO.getWaitingRequestStatus())
                .note(waitingRequestDTO.getNote())
                .startDate(waitingRequestDTO.getStartDate())
                .id(waitingRequestDTO.getId())
                .user(userRepository.findById(waitingRequestDTO.getUserId()).orElse(null))
                .waitingList(waitingList)
                .build());

        WaitingRequestResponseDTO waitingRequestResponseDTO = new WaitingRequestResponseDTO(waitingRequest);
        Set<WaitingRequestDetailDTO> waitingRequestDetails = waitingList.stream().map(detail ->
                WaitingRequestDetailDTO.builder()
                        .waitingRequestId(waitingRequestResponseDTO.getId())
                        .book(detail.getBook().getTitle()).build()).collect(Collectors.toSet());
        waitingRequestDetails.forEach(waitingRequestDetailDTO -> waitingRequestDetailService.save(waitingRequestDetailDTO));

        return waitingRequestResponseDTO;
    }


    @Override
    public WaitingRequestResponseDTO cancelWaitingRequestByUser(Long id) throws CustomException {
        WaitingRequestResponseDTO waitingRequestResponseDTO = findById(id);
        if (waitingRequestResponseDTO.getWaitingRequestStatus().equals(WaitingRequestStatus.PENDING)) {
            waitingRequestRepository.changeStatus(WaitingRequestStatus.FAILED, waitingRequestResponseDTO.getId());
            return findById(waitingRequestResponseDTO.getId());
        } else {
            throw new CustomException("Don't cancel waiting request having status " + waitingRequestResponseDTO.getWaitingRequestStatus());
        }
    }

    @Override
    public WaitingRequestResponseDTO cancelWaitingRequestByAdmin(Long id) throws CustomException {
        WaitingRequestResponseDTO waitingRequestResponseDTO = findById(id);
        List<WaitingRequestDetailDTO> list = waitingRequestDetailService.findByRequestId(id);
        boolean checkBookStockAndStatus = list.stream().anyMatch(detail -> bookRepository.findBookByTitle(detail.getBook()).getStock() == 0 && !bookRepository.findBookByTitle(detail.getBook()).isStatus());
        Period period = Period.between(waitingRequestResponseDTO.getStartDate(), LocalDate.now());
        int days = period.getDays();

        if (waitingRequestResponseDTO.getWaitingRequestStatus().equals(WaitingRequestStatus.PENDING)
                && days > 5 && checkBookStockAndStatus) {
            waitingRequestRepository.changeStatus(WaitingRequestStatus.FAILED, waitingRequestResponseDTO.getId());

            //gui mai bao thanh cong
            User user = userRepository.findById(waitingRequestResponseDTO.getUserId()).orElse(null);
            assert user != null;
            if (user.getEmail() != null) {
                String subject = "Notification from Library";
                String text = "Your waiting request with id " + waitingRequestResponseDTO.getId() + " has been cancelled";
                emailService.sendMail(user.getEmail(), subject, text);
            }

            return findById(waitingRequestResponseDTO.getId());
        } else {
            if (!waitingRequestResponseDTO.getWaitingRequestStatus().equals(WaitingRequestStatus.PENDING)) {
                throw new CustomException("Don't cancel waiting request having status " + waitingRequestResponseDTO.getWaitingRequestStatus());
            }
            throw new CustomException("Don't cancel waiting request");
        }
    }

    @Override
    public BorrowedCartResponseDTO acceptWaitingRequest(Long id) throws CustomException, BookException {
        WaitingRequestResponseDTO waitingRequestResponseDTO = findById(id);
        List<WaitingRequestDetailDTO> list = waitingRequestDetailService.findByRequestId(id);
        boolean checkBookStockAndStatus = list.stream().allMatch(detail -> bookRepository.findBookByTitle(detail.getBook()).getStock() > 0 && bookRepository.findBookByTitle(detail.getBook()).isStatus());
        if (checkBookStockAndStatus && waitingRequestResponseDTO.getWaitingRequestStatus().equals(WaitingRequestStatus.PENDING)) {
            waitingRequestRepository.changeStatus(WaitingRequestStatus.SUCCESSFUL, waitingRequestResponseDTO.getId());
            borrowedCartService.saveFromWaitingList(id);

            //gui mai bao thanh cong
            User user = userRepository.findById(waitingRequestResponseDTO.getUserId()).orElse(null);
            assert user != null;
            if (user.getEmail() != null) {
                String subject = "Notification from Library";
                String text = "Your waiting request with id " + waitingRequestResponseDTO.getId() + " has been accept";
                emailService.sendMail(user.getEmail(), subject, text);
            }
            WaitingRequestResponseDTO waitingRequestResponseDTO1=findById(waitingRequestResponseDTO.getId());
            return BorrowedCartResponseDTO.builder()
                    .borrowedCartStatus(BorrowedCartStatus.BORROWED)
                    .count(list.size())
                    .fine(0)
                    .userId(user.getId())
                    .build();
        } else {
            if (!waitingRequestResponseDTO.getWaitingRequestStatus().equals(WaitingRequestStatus.PENDING)) {
                throw new CustomException("Don't accept waiting request having status " + waitingRequestResponseDTO.getWaitingRequestStatus());
            }
            throw new CustomException("Don't accept waiting request because list of books has been issued");
        }
    }

    @Override
    public WaitingRequestResponseDTO findById(Long id) throws CustomException {
        WaitingRequest waitingRequest = waitingRequestRepository.findById(id).orElseThrow(() ->
                new CustomException("Don't find waiting request with this id " + id));
        return new WaitingRequestResponseDTO(waitingRequest);
    }

}
