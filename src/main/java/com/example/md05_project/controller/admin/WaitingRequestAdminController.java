package com.example.md05_project.controller.admin;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.model.dto.response.BorrowedCartResponseDTO;
import com.example.md05_project.model.dto.response.GenreResponseDTO;
import com.example.md05_project.model.dto.response.WaitingRequestDetailDTO;
import com.example.md05_project.model.dto.response.WaitingRequestResponseDTO;
import com.example.md05_project.model.entity.WaitingRequestStatus;
import com.example.md05_project.service.WaitingRequestDetail.WaitingRequestDetailService;
import com.example.md05_project.service.waitingRequest.WaitingRequestService;
import com.example.md05_project.validation.ValidateParamType;
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
@RequestMapping("/api.myservice.com/v1/admin/waiting-request")
public class WaitingRequestAdminController {
    @Autowired
    private WaitingRequestService waitingRequestService;
    @Autowired
    private WaitingRequestDetailService waitingRequestDetailService;
    @Autowired
    private ValidateParamType validateParamType;

    @GetMapping("")
    public ResponseEntity<?> getAll(@RequestParam(name = "keyword", required = false) String status,
                                    @RequestParam(defaultValue = "5", name = "limit") int limit,
                                    @RequestParam(defaultValue = "0", name = "page") int page,
                                    @RequestParam(defaultValue = "id", name = "sort") String sort,
                                    @RequestParam(defaultValue = "acs", name = "order") String order) throws CustomException {
        Page<WaitingRequestResponseDTO> waitingRequestResponseDTOPage;
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(direction, sort));

        if (status != null && !status.isEmpty()) {
            WaitingRequestStatus parsedStatus = validateParamType.validateWaitingRequestStatus(status);
            waitingRequestResponseDTOPage = waitingRequestService.findAllByWaitingRequestStatus(parsedStatus, pageable);
        } else {
            waitingRequestResponseDTOPage = waitingRequestService.getAll(pageable);
        }
        if (waitingRequestResponseDTOPage == null || waitingRequestResponseDTOPage.isEmpty()) {
            return new ResponseEntity<>("Don't have any waiting list", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(waitingRequestResponseDTOPage, HttpStatus.OK);
        }
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<?> getByRequestId(@PathVariable Long requestId) throws CustomException {
        List<WaitingRequestDetailDTO> list = waitingRequestDetailService.findByRequestId(requestId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PatchMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptRequest(@PathVariable Long requestId) throws CustomException, BookException {
        BorrowedCartResponseDTO borrowedCartResponseDTO  = waitingRequestService.acceptWaitingRequest(requestId);

        Map<String, BorrowedCartResponseDTO> response=new HashMap<>();
        response.put("Accept waiting request successfully",borrowedCartResponseDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/cancel/{requestId}")
    public ResponseEntity<?> cancelRequest(@PathVariable Long requestId) throws CustomException {
        WaitingRequestResponseDTO waitingRequestResponseDTO = waitingRequestService.cancelWaitingRequestByAdmin(requestId);
        Map<String, WaitingRequestResponseDTO> response=new HashMap<>();
        response.put("Cancel waiting request successfully",waitingRequestResponseDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
