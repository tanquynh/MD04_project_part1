package com.example.md05_project.model.dto.request;

import com.example.md05_project.model.entity.User;
import com.example.md05_project.model.entity.WaitingRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitingRequestDTO {
    private Long id;
    private Long userId;
    private LocalDate startDate = LocalDate.now();
    private String note;
    private WaitingRequestStatus waitingRequestStatus=WaitingRequestStatus.PENDING;
}
