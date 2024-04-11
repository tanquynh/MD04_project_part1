package com.example.md05_project.model.dto.response;

import com.example.md05_project.model.entity.Book;

import com.example.md05_project.model.entity.WaitingRequestDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitingRequestDetailDTO {
    private Long id;
    private Long waitingRequestId;
    private String book;

    public WaitingRequestDetailDTO(WaitingRequestDetail waitingRequestDetail) {
        this.id = waitingRequestDetail.getId();
        this.waitingRequestId = waitingRequestDetail.getWaitingRequest().getId();
        this.book = waitingRequestDetail.getBook().getTitle();
    }
}
