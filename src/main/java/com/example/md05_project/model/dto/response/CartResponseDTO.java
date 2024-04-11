package com.example.md05_project.model.dto.response;

import com.example.md05_project.model.entity.Book;
import com.example.md05_project.model.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {
    private Long id;
    private Long userId;
    private String book;

    public CartResponseDTO(Cart cart) {
        this.id = cart.getId();
        this.userId = cart.getUser().getId();
        this.book = cart.getBook().getTitle();
    }
}
