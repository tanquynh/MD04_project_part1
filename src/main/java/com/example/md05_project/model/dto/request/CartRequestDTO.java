package com.example.md05_project.model.dto.request;

import com.example.md05_project.model.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartRequestDTO {
    private Long id;
    private Long userId;
    private Long bookId;
}
