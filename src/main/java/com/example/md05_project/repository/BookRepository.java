package com.example.md05_project.repository;

import com.example.md05_project.model.entity.Book;
import com.example.md05_project.model.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface BookRepository extends JpaRepository<Book,Long> {
    Page<Book> findAllByTitle(Pageable pageable, String name);
    Page<Book>findAllByTitleContainingIgnoreCase(Pageable pageable,String name);
    boolean existsByTitle(String name);
    @Modifying
    @Query("update Book b set b.status=case when b.status=true then false else true end where b.id=?1")
    void changeStatus(Long id);

    @Modifying
    @Query("update Book  b set b.stock=b.stock+1 where b.id=?1")
    void increaseStock(Long bookId);

    @Modifying
    @Query("update Book  b set b.stock=b.stock-1 where b.id=?1")
    void decreaseStock(Long bookId);

    @Modifying
    @Query("update Book b set b.status=false where b.id=?1")
    void setStatusFalse(Long id);

    List<Book>findAllByGenre_Id(Long genreId);

    List<Book>findAllByStatus(boolean status);
    Book findBookByTitle(String title);

}
