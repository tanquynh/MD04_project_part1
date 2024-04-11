package com.example.md05_project.controller.user;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.UserNotFoundException;
import com.example.md05_project.model.dto.request.WaitingListRequestDTO;
import com.example.md05_project.model.dto.response.GenreResponseDTO;
import com.example.md05_project.model.dto.response.WaitingListResponseDTO;
import com.example.md05_project.model.dto.response.WaitingRequestResponseDTO;
import com.example.md05_project.model.dto.response.userResponse.UserResponseDTO;
import com.example.md05_project.service.user.UserService;
import com.example.md05_project.service.waitingList.WaitingListService;
import com.example.md05_project.validation.ValidateParamType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api.myservice.com/v1/user/waiting")

public class WaitingListController {
    @Autowired
    private WaitingListService waitingListService;
    @Autowired
    private UserService userService;
    @Autowired
    private ValidateParamType validateParamType;

    @GetMapping("")
    public ResponseEntity<?> getWaitingList(HttpServletRequest request) throws CustomException, UserNotFoundException {
        UserResponseDTO userResponseDTO=userService.getAccount(request);
        List<WaitingListResponseDTO> list = waitingListService.getAll(userResponseDTO.getId());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> addWaitingList(HttpServletRequest request, @RequestParam String bookId) throws UserNotFoundException, BookException, CustomException {
        Long parsedId= validateParamType.validateId(bookId);
        UserResponseDTO userResponseDTO=userService.getAccount(request);

        WaitingListRequestDTO waitingListRequestDTO = WaitingListRequestDTO.builder()
                .userId(userResponseDTO.getId())
                .bookId(parsedId)
                .build();

        WaitingListResponseDTO waitingListResponseDTO = waitingListService.save(waitingListRequestDTO);

        Map<String, WaitingListResponseDTO> response=new HashMap<>();
        response.put("Add new waiting list successfully",waitingListResponseDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{waitingId}")
    public ResponseEntity<?>deleteWaitingList(HttpServletRequest request,@PathVariable String waitingId) throws CustomException {
        Long parsedId= validateParamType.validateId(waitingId);

        WaitingListResponseDTO waitingListResponseDTO=waitingListService.findById(parsedId);
        waitingListService.delete(waitingListResponseDTO.getId());
        return new ResponseEntity<>("Delete successful", HttpStatus.OK);
    }

    @DeleteMapping("")
    public ResponseEntity<?>deleteList(HttpServletRequest request) throws UserNotFoundException, CustomException, BookException {
        UserResponseDTO userResponseDTO=userService.getAccount(request);
        List<WaitingListResponseDTO>list=waitingListService.getAll(userResponseDTO.getId());
        waitingListService.deleteByUserId(userResponseDTO.getId());
        return new ResponseEntity<>("Delete cart successful", HttpStatus.OK);
    }
}
