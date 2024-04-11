package com.example.md05_project.model.dto.response;

import com.example.md05_project.model.entity.Book;
import com.example.md05_project.model.entity.User;
import com.example.md05_project.model.entity.WaitingList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitingListResponseDTO {
    private Long id;
    private Long userId;
    private String book;

    public WaitingListResponseDTO(WaitingList waitingList) {
        this.id = waitingList.getId();
        this.userId = waitingList.getUser().getId();
        this.book = waitingList.getBook().getTitle();
    }
}
