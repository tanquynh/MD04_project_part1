package com.example.md05_project.service.genre;

import com.example.md05_project.exception.GenreException;
import com.example.md05_project.model.dto.request.GenreRequestDTO;
import com.example.md05_project.model.dto.response.GenreResponseDTO;
import com.example.md05_project.model.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GenreService {
    Page<GenreResponseDTO> findAllWithPaginationAndSort(Pageable pageable);

    Page<GenreResponseDTO> searchByNameWithPaginationAndSort(Pageable pageable, String name);

    GenreResponseDTO findById(Long id) throws GenreException;

    GenreResponseDTO saveOrUpdate(GenreRequestDTO genreRequestDTO) throws GenreException;

    void changeStatus(Long id) throws GenreException;

    //    Genre findGenreByGenreName(String name);
    List<GenreResponseDTO> findAllByStatus(boolean status);
    List<GenreResponseDTO> findAll();
}
