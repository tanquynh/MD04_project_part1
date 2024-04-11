package com.example.md05_project.service.waitingRequest;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.model.dto.request.WaitingRequestDTO;
import com.example.md05_project.model.dto.response.BorrowedCartResponseDTO;
import com.example.md05_project.model.dto.response.WaitingRequestResponseDTO;
import com.example.md05_project.model.entity.User;
import com.example.md05_project.model.entity.WaitingRequest;
import com.example.md05_project.model.entity.WaitingRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WaitingRequestService {
    Page<WaitingRequestResponseDTO> getAll(Pageable pageable);
    Page<WaitingRequestResponseDTO>findAllByWaitingRequestStatus(WaitingRequestStatus status, Pageable pageable);
    Page<WaitingRequestResponseDTO>getByUserId(Long userId,Pageable pageable);
    WaitingRequestResponseDTO save(WaitingRequestDTO waitingRequestDTO) throws CustomException;
    WaitingRequestResponseDTO cancelWaitingRequestByUser(Long id) throws CustomException;
    WaitingRequestResponseDTO cancelWaitingRequestByAdmin(Long id) throws CustomException;
    BorrowedCartResponseDTO acceptWaitingRequest(Long id) throws CustomException, BookException;
    WaitingRequestResponseDTO findById(Long id) throws CustomException;
}
