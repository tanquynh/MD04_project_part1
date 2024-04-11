package com.example.md05_project.repository;

import com.example.md05_project.model.entity.WaitingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface WaitingListRepository extends JpaRepository<WaitingList,Long> {

    @Modifying
    @Query("select w from WaitingList w WHERE w.book.id=?1 and w.user.id=?2")
    WaitingList findBookById(Long bookId,Long userId);

    @Modifying
    @Query("select w from WaitingList w where w.user.id=?1")
    List<WaitingList> findAllByUserId(Long userId);

    @Modifying
    @Query("DELETE from WaitingList w where w.user.id=?1")
    void deleteByUserId(Long userId);

}
