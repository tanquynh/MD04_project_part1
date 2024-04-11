package com.example.md05_project.repository;

import com.example.md05_project.model.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface GenreRepository extends JpaRepository<Genre,Long> {
    Page<Genre>findAllByGenreNameContainingIgnoreCase(Pageable pageable,String name);
    boolean existsByGenreName(String name);
    @Modifying
    @Query("update Genre g set g.status=case when g.status=true then false else true end where g.id=?1")
    void changeStatus(Long id);

    Genre findGenreByGenreName(String name);

    List<Genre> findAllByStatus(boolean status);
}
