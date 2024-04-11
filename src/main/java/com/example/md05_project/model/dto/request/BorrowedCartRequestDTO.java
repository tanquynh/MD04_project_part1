package com.example.md05_project.model.dto.request;

import com.example.md05_project.model.entity.BorrowedCartStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowedCartRequestDTO {
    private Long id;

    private Long userId;

    private LocalDate startDate = LocalDate.now();

    private LocalDate endDate = LocalDate.now().plusDays(2);

    private String note;

    private double fine=0;

    private BorrowedCartStatus borrowedCartStatus=BorrowedCartStatus.BORROWED;
}
