package com.example.md05_project.model.dto.response;

import com.example.md05_project.model.entity.Book;
import com.example.md05_project.model.entity.Genre;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreResponseDTO {
    private Long id;
    private String genreName;
    private boolean status;
    @JsonIgnore
    private Set<Book> books;

    public GenreResponseDTO(Genre genre) {
        this.id = genre.getId();
        this.genreName = genre.getGenreName();
        this.status = genre.isStatus();
        this.books = genre.getBooks();
    }
}
