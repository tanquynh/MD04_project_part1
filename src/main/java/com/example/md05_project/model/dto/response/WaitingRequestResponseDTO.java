package com.example.md05_project.model.dto.response;


import com.example.md05_project.model.entity.WaitingRequest;
import com.example.md05_project.model.entity.WaitingRequestStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitingRequestResponseDTO {
    private Long id;

    private Long  userId;

    private LocalDate startDate = LocalDate.now();

    private String note;
    private WaitingRequestStatus waitingRequestStatus=WaitingRequestStatus.PENDING;
    @JsonIgnore
    Set<WaitingListResponseDTO> waitingList;

    public WaitingRequestResponseDTO(WaitingRequest waitingRequest) {
        this.id = waitingRequest.getId();
        this.userId = waitingRequest.getUser().getId();
        this.startDate = waitingRequest.getStartDate();
        this.note = waitingRequest.getNote();
        this.waitingRequestStatus = waitingRequest.getWaitingRequestStatus();
        this.waitingList = waitingRequest.getWaitingList().stream().map(WaitingListResponseDTO::new).collect(Collectors.toSet());
    }
}
