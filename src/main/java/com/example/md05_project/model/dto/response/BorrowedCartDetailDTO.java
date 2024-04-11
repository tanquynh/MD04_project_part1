package com.example.md05_project.model.dto.response;

import com.example.md05_project.model.entity.Book;
import com.example.md05_project.model.entity.BorrowedCartDetail;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowedCartDetailDTO {
    private Long id;
    private Long borrowedCartId;
    private String book;

    public BorrowedCartDetailDTO(BorrowedCartDetail borrowedCartDetail) {
        this.id = borrowedCartDetail.getId();
        this.borrowedCartId = borrowedCartDetail.getBorrowedCart().getId();
        this.book = borrowedCartDetail.getBook().getTitle();
    }
}
