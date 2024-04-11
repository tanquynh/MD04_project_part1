package com.example.md05_project.service.WaitingRequestDetail;

import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.UserNotFoundException;
import com.example.md05_project.model.dto.response.WaitingRequestDetailDTO;
import com.example.md05_project.model.dto.response.userResponse.UserResponseDTO;
import com.example.md05_project.model.entity.WaitingRequest;
import com.example.md05_project.model.entity.WaitingRequestDetail;
import com.example.md05_project.repository.BookRepository;
import com.example.md05_project.repository.WaitingRequestDetailRepository;
import com.example.md05_project.repository.WaitingRequestRepository;
import com.example.md05_project.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WaitingRequestDetailServiceImpl implements WaitingRequestDetailService {
    @Autowired
    private WaitingRequestDetailRepository waitingRequestDetailRepository;
    @Autowired
    private WaitingRequestRepository waitingRequestRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserService userService;

    @Override
    public List<WaitingRequestDetailDTO> findByRequestId(Long id) throws CustomException {
        List<WaitingRequestDetail>list = waitingRequestDetailRepository.findAllByRequestId(id);
        if(list.isEmpty()){
            throw new CustomException("Don't find waiting request with this id "+id);
        }
        return list.stream().map(WaitingRequestDetailDTO::new).toList();
    }

    @Override
    public List<WaitingRequestDetailDTO> findByRequestIdOfUser(HttpServletRequest request,Long id) throws UserNotFoundException, CustomException {
        UserResponseDTO userResponseDTO=userService.getAccount(request);

        WaitingRequest waitingRequest=waitingRequestRepository.findById(id).orElseThrow(()->
                new CustomException("Don't find waiting request with id "+id));
        if(!waitingRequest.getUser().getId().equals(userResponseDTO.getId())){
           throw  new CustomException("Don't find waiting request with id "+id);
        }
        List<WaitingRequestDetail>list = waitingRequestDetailRepository.findAllByRequestId(id);
        if(list.isEmpty()){
            throw new CustomException("Don't find waiting request with this id "+id);
        }
        return list.stream().map(WaitingRequestDetailDTO::new).toList();
    }

    @Override
    public WaitingRequestDetailDTO save(WaitingRequestDetailDTO waitingRequestDetailDTO) {
        WaitingRequestDetail waitingRequestDetail=waitingRequestDetailRepository.save(WaitingRequestDetail.builder()
//                        .id(waitingRequestDetailDTO.getId())
                        .waitingRequest(waitingRequestRepository.findById(waitingRequestDetailDTO.getWaitingRequestId()).orElse(null))
                        .book(bookRepository.findBookByTitle(waitingRequestDetailDTO.getBook()))
                .build());
        return new WaitingRequestDetailDTO(waitingRequestDetail);
    }

    @Override
    public WaitingRequestDetail findById(Long id) throws CustomException {
        return waitingRequestDetailRepository.findById(id).orElseThrow(()->
                new CustomException("Don't find waiting request with this id "+id));
    }
}
