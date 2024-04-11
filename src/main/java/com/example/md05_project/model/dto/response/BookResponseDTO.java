package com.example.md05_project.model.dto.response;

import com.example.md05_project.model.entity.Author;
import com.example.md05_project.model.entity.Book;
import com.example.md05_project.model.entity.Genre;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDTO {
    private Long id;
    private String title;
    private String description;
    private int stock;
    private String image;
    private double unitPrice;
    private boolean status = true; // Tình trạng sách
    private LocalDate createdAt;
    private String genre;
    private Set<String> authors;

    public BookResponseDTO(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.description = book.getDescription();
        this.stock = book.getStock();
        this.image = book.getImage();
        this.unitPrice = book.getUnitPrice();
        this.status = book.isStatus();
        this.createdAt = book.getCreatedAt();
        this.genre = book.getGenre().getGenreName();
        this.authors = book.getAuthors().stream().map(Author::getName).collect(Collectors.toSet());
    }
}
