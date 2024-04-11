package com.example.md05_project.repository;

import com.example.md05_project.model.entity.BorrowedCartDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowedCartDetailRepository extends JpaRepository<BorrowedCartDetail,Long> {

   List<BorrowedCartDetail>findAllByBorrowedCart_Id(Long cartId);
}
