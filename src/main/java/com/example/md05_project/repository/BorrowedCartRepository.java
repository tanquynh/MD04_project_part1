package com.example.md05_project.repository;

import com.example.md05_project.model.entity.BorrowedCart;
import com.example.md05_project.model.entity.BorrowedCartStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Transactional
public interface BorrowedCartRepository extends JpaRepository<BorrowedCart, Long> {
    Page<BorrowedCart> findAllByBorrowedCartStatus(Pageable pageable, BorrowedCartStatus borrowedCartStatus);

    @Modifying
    @Query("update BorrowedCart b set b.borrowedCartStatus=?1 where b.id=?2 ")
    void changeStatus(BorrowedCartStatus status, Long id);

    Page<BorrowedCart> findAllByUser_Id(Long userId, Pageable pageable);

    Page<BorrowedCart> findAllByUser_IdAndBorrowedCartStatus(Long userId, BorrowedCartStatus status, Pageable pageable);

//    @Query("select b from BorrowedCart b where b.user.id=?1")
//    List<BorrowedCart> findByUserId(Long userId);

    List<BorrowedCart> findAllByUser_Id(Long userId);

    @Modifying
    @Query("update BorrowedCart  b set b.fine=?1 where b.id=?2")
    void updateFine(double fine, Long cartId);

//    @Query("select b from BorrowedCart b where b.startDate between ?1 and ?2")
//    List<BorrowedCart> getCartByDate(LocalDate fromDate, LocalDate toDate);

    List<BorrowedCart> findAllByStartDateIsBetween(LocalDate fromDate, LocalDate toDate);

//    @Query("select b from BorrowedCart b where b.startDate between ?1 and ?2 and b.borrowedCartStatus=?3")
//    List<BorrowedCart> getCartByDateAndStatus(LocalDate fromDate, LocalDate toDate, BorrowedCartStatus status);

    List<BorrowedCart> findAllByStartDateIsBetweenAndBorrowedCartStatus(LocalDate fromDate, LocalDate toDate, BorrowedCartStatus status);

    Integer countAllByBorrowedCartStatus(BorrowedCartStatus status);
}
