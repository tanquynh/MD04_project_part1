package com.example.md05_project.controller.admin;

import com.example.md05_project.exception.GenreException;
import com.example.md05_project.model.dto.request.GenreRequestDTO;
import com.example.md05_project.model.dto.response.BookResponseDTO;
import com.example.md05_project.model.dto.response.GenreResponseDTO;
import com.example.md05_project.service.genre.GenreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api.myservice.com/v1/admin/genres")
public class GenreController {
    @Autowired
    private GenreService genreService;
    @GetMapping("")
    public ResponseEntity<Page<GenreResponseDTO>> getGenres(@RequestParam(name = "keyword",required = false)String keyword,
                                                            @RequestParam(defaultValue = "5",name = "limit")int limit,
                                                            @RequestParam(defaultValue = "0",name = "page")int page,
                                                            @RequestParam(defaultValue = "id",name = "sort")String sort,
                                                            @RequestParam(defaultValue = "acs",name = "order")String order){
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, limit, Sort.by(direction, sort));
        Page<GenreResponseDTO> categoryPage;

        if(keyword!=null && !keyword.isEmpty()){
            categoryPage=genreService.searchByNameWithPaginationAndSort( pageable,keyword);
        }else{
            categoryPage=genreService.findAllWithPaginationAndSort( pageable);
        }

        return new ResponseEntity<>(categoryPage, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?>getGenreById(@PathVariable Long id) throws GenreException {
        GenreResponseDTO genreResponseDTO=genreService.findById(id);
        return new ResponseEntity<>(genreResponseDTO,HttpStatus.OK);
    }
    @PostMapping("")
    public ResponseEntity<?>addGenre(@RequestBody @Valid GenreRequestDTO genreRequestDTO) throws GenreException {
        GenreResponseDTO genreResponseDTO=genreService.saveOrUpdate(genreRequestDTO);

        Map<String, GenreResponseDTO> response=new HashMap<>();
        response.put("Add new genre successfully",genreResponseDTO);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?>editGenre(@PathVariable Long id,@RequestBody @Valid GenreRequestDTO genreRequestDTO) throws GenreException {
        GenreResponseDTO editGenre=genreService.findById(id);
        genreRequestDTO.setId(editGenre.getId());
        GenreResponseDTO genreResponseDTO=genreService.saveOrUpdate(genreRequestDTO);

        Map<String, GenreResponseDTO> response=new HashMap<>();
        response.put("Edit genre successfully",genreResponseDTO);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?>changeStatus(@PathVariable Long id) throws GenreException {
        GenreResponseDTO changedGenre=genreService.findById(id);
        genreService.changeStatus(id);

        Map<String, GenreResponseDTO> response=new HashMap<>();
        response.put("Edit genre successfully",changedGenre);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
