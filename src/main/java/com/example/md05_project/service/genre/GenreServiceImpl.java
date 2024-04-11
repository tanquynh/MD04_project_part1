package com.example.md05_project.service.genre;

import com.example.md05_project.exception.GenreException;
import com.example.md05_project.model.dto.request.GenreRequestDTO;
import com.example.md05_project.model.dto.response.GenreResponseDTO;
import com.example.md05_project.model.entity.Genre;
import com.example.md05_project.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {
    @Autowired
    private GenreRepository genreRepository;

    @Override
    public Page<GenreResponseDTO> findAllWithPaginationAndSort(Pageable pageable) {
        Page<Genre> list = genreRepository.findAll(pageable);
        return list.map(GenreResponseDTO::new);
    }

    @Override
    public Page<GenreResponseDTO> searchByNameWithPaginationAndSort(Pageable pageable, String name) {
        Page<Genre> list = genreRepository.findAllByGenreNameContainingIgnoreCase(pageable, name);
        return list.map(GenreResponseDTO::new);
    }

    @Override
    public GenreResponseDTO findById(Long id) throws GenreException {
        Genre genre = genreRepository.findById(id).orElseThrow(() -> new GenreException("Genre is not found with this id " + id));
        return GenreResponseDTO.builder().id(genre.getId())
                .genreName(genre.getGenreName()).status(genre.isStatus())
                .books(genre.getBooks()).build();
    }

    @Override
    public GenreResponseDTO saveOrUpdate(GenreRequestDTO genreRequestDTO) throws GenreException {
        if (genreRequestDTO.getId() == null) {
            if (genreRepository.existsByGenreName(genreRequestDTO.getGenreName())) {
                throw new GenreException("Genre name has been already existed!");
            }

        } else {
            GenreResponseDTO editGenreResponseDTO = findById(genreRequestDTO.getId());
            boolean genreNameExist = genreRepository.findAll().stream().anyMatch(genre ->
                    !genreRequestDTO.getGenreName().equals(editGenreResponseDTO.getGenreName()) && genreRequestDTO.getGenreName().equals(genre.getGenreName()));
            if (genreNameExist) {
                throw new GenreException("Genre name has been already existed!");
            }
        }
        Genre genre = genreRepository.save(Genre.builder().id(genreRequestDTO.getId())
                .genreName(genreRequestDTO.getGenreName()).status(genreRequestDTO.isStatus()).build());
        return new GenreResponseDTO(genre);
    }

    @Override
    public void changeStatus(Long id) throws GenreException {
        GenreResponseDTO genreResponseDTO = findById(id);
        if (genreResponseDTO != null) {
            genreRepository.changeStatus(id);
        }
    }

    @Override
    public List<GenreResponseDTO> findAllByStatus(boolean status) {
        List<Genre> list = genreRepository.findAllByStatus(status);
        return list.stream().map(GenreResponseDTO::new).toList();
    }

    @Override
    public List<GenreResponseDTO> findAll() {
        List<Genre>list=genreRepository.findAll();
        return list.stream().map(GenreResponseDTO::new).toList();
    }

}
