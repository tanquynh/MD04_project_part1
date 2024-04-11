package com.example.md05_project.model.dto.response;

import com.example.md05_project.model.entity.BorrowedCart;
import com.example.md05_project.model.entity.BorrowedCartStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowedCartResponseDTO {
    private Long id;

    private Long userId;

    private LocalDate startDate = LocalDate.now();

    private LocalDate endDate = LocalDate.now().plusDays(2);

    private String note;

    private double fine=0;
    private int count;

    private BorrowedCartStatus borrowedCartStatus;

    public BorrowedCartResponseDTO(BorrowedCart borrowedCart) {
        this.id = borrowedCart.getId();
        this.userId = borrowedCart.getUser().getId();
        this.startDate = borrowedCart.getStartDate();
        this.endDate = borrowedCart.getEndDate();
        this.note = borrowedCart.getNote();
        this.fine = borrowedCart.getFine();
        this.borrowedCartStatus = borrowedCart.getBorrowedCartStatus();
        this.count= borrowedCart.getCount();
    }
}
