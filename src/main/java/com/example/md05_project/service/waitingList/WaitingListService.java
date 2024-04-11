package com.example.md05_project.service.waitingList;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.UserNotFoundException;
import com.example.md05_project.model.dto.request.WaitingListRequestDTO;
import com.example.md05_project.model.dto.response.WaitingListResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WaitingListService {
    List<WaitingListResponseDTO> getAll(Long userId) throws CustomException;
    WaitingListResponseDTO save(WaitingListRequestDTO waitingListRequestDTO) throws BookException, CustomException, UserNotFoundException;
    void delete(Long id) throws CustomException;
    WaitingListResponseDTO findById(Long id) throws CustomException;
    WaitingListResponseDTO findByBookId (Long bookId,Long userId);
    Page<WaitingListResponseDTO>getAllWithPagination(Pageable pageable,Long userId);
    void deleteByUserId(Long userId);
}
