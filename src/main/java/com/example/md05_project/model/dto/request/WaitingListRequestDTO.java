package com.example.md05_project.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class WaitingListRequestDTO {
    private Long id;
    private Long userId;
    private Long bookId;
}
