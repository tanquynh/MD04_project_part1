package com.example.md05_project.controller.user;

import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.UserNotFoundException;
import com.example.md05_project.model.dto.request.WaitingRequestDTO;
import com.example.md05_project.model.dto.response.GenreResponseDTO;
import com.example.md05_project.model.dto.response.WaitingRequestDetailDTO;
import com.example.md05_project.model.dto.response.WaitingRequestResponseDTO;

import com.example.md05_project.model.dto.response.userResponse.UserResponseDTO;
import com.example.md05_project.model.entity.WaitingRequestStatus;
import com.example.md05_project.service.WaitingRequestDetail.WaitingRequestDetailService;
import com.example.md05_project.service.user.UserService;
import com.example.md05_project.service.waitingList.WaitingListService;
import com.example.md05_project.service.waitingRequest.WaitingRequestService;
import com.example.md05_project.validation.ValidateParamType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api.myservice.com/v1/user/waiting-request")
public class WaitingRequestController {
    @Autowired
    private WaitingRequestService waitingRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private WaitingListService waitingListService;
    @Autowired
    private WaitingRequestDetailService waitingRequestDetailService;
    @Autowired
    private ValidateParamType validateParamType;

    @GetMapping("")
    public ResponseEntity<?> getBooksWithPaginationAndSortAndSearch(HttpServletRequest request,
                                      @RequestParam(name = "keyword", required = false)WaitingRequestStatus status,
                                      @RequestParam(defaultValue = "5", name = "limit") int limit,
                                      @RequestParam(defaultValue = "0", name = "page") int page,
                                      @RequestParam(defaultValue = "id", name = "sort") String sort,
                                      @RequestParam(defaultValue = "acs", name = "order") String order) throws UserNotFoundException {
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(direction, sort));
        Page<WaitingRequestResponseDTO> waitingRequestResponseDTOPage = null;

        UserResponseDTO userResponseDTO=userService.getAccount(request);
        if (status != null) {
            waitingRequestResponseDTOPage=waitingRequestService.findAllByWaitingRequestStatus(status,pageable);
        } else {
            waitingRequestResponseDTOPage = waitingRequestService.getByUserId(userResponseDTO.getId(), pageable);
        }

        if (waitingRequestResponseDTOPage == null || waitingRequestResponseDTOPage.isEmpty()) {
            return new ResponseEntity<>("Don't have any book-request.", HttpStatus.OK);
        }
        return new ResponseEntity<>(waitingRequestResponseDTOPage, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> addRequest(HttpServletRequest request, @RequestBody WaitingRequestDTO waitingRequestDTO) throws CustomException, UserNotFoundException {
        WaitingRequestResponseDTO waitingRequestResponseDTO = waitingRequestService.save(waitingRequestDTO);
        UserResponseDTO userResponseDTO=userService.getAccount(request);

        waitingListService.deleteByUserId(userResponseDTO.getId());

        Map<String, WaitingRequestResponseDTO> response=new HashMap<>();
        response.put("Add new request successfully",waitingRequestResponseDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<?>getRequestById(@PathVariable ("requestId") String requestId, HttpServletRequest request) throws CustomException, UserNotFoundException {
        Long parsedId= validateParamType.validateId(requestId);
        List<WaitingRequestDetailDTO>list=waitingRequestDetailService.findByRequestIdOfUser(request,parsedId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PatchMapping("/cancel/{requestId}")
    public ResponseEntity<?>cancelRequestById(@PathVariable ("requestId")  String requestId) throws CustomException {
        Long parsedId= validateParamType.validateId(requestId);
        WaitingRequestResponseDTO waitingRequestResponseDTO=waitingRequestService.cancelWaitingRequestByUser(parsedId);

        Map<String, WaitingRequestResponseDTO> response=new HashMap<>();
        response.put("Cancel request successfully",waitingRequestResponseDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
