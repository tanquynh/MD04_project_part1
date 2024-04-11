package com.example.md05_project.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenreRequestDTO {
    private Long id;
    @NotEmpty(message = "Tên danh mục sách không được để trống")
    private String genreName;
    private boolean status=true;
}
