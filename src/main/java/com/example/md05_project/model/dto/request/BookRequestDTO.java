package com.example.md05_project.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequestDTO {
    private Long id;
    @NotEmpty(message = "Title is mandatory!")
    private String title;

    @NotEmpty(message = "Description is mandatory!")
    private String description;

    @NotNull(message = "Description is mandatory!")
    @PositiveOrZero(message = "Stock must be greater than or equal to 0")
    private int stock;
    private boolean status = true;
    private MultipartFile file;
    private LocalDate createdAt=LocalDate.now();

    @PositiveOrZero(message = "unitPrice must be greater than 0")
    private double unitPrice;

    @NotNull(message = "Genre is mandatory!")
    private Long genreId;
    private Set<String> authors;
}
