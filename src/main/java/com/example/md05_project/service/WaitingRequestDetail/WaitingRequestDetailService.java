package com.example.md05_project.service.WaitingRequestDetail;

import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.UserNotFoundException;
import com.example.md05_project.model.dto.response.WaitingRequestDetailDTO;
import com.example.md05_project.model.dto.response.WaitingRequestResponseDTO;
import com.example.md05_project.model.entity.WaitingRequestDetail;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface WaitingRequestDetailService {
    List<WaitingRequestDetailDTO> findByRequestId(Long id) throws CustomException;
    List<WaitingRequestDetailDTO> findByRequestIdOfUser(HttpServletRequest request,Long id) throws UserNotFoundException, CustomException;
    WaitingRequestDetailDTO save(WaitingRequestDetailDTO waitingRequestDetailDTO);
    WaitingRequestDetail findById(Long id) throws CustomException;
}
