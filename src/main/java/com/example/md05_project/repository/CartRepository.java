package com.example.md05_project.repository;

import com.example.md05_project.model.entity.Cart;
import com.example.md05_project.model.entity.WaitingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface CartRepository extends JpaRepository<Cart,Long> {
    @Modifying
    @Query("select c from Cart c WHERE c.book.id=?1 and c.user.id=?2")
    Cart findBookById(Long bookId, Long userId);

    @Modifying
    @Query("select c from Cart c where c.user.id=?1")
    List<Cart> findAllByUserId(Long userId);

    @Modifying
    @Query("DELETE from Cart c where c.user.id=?1")
    void deleteByUserId(Long userId);
}
