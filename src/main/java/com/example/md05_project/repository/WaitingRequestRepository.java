package com.example.md05_project.repository;

import com.example.md05_project.model.entity.WaitingRequest;
import com.example.md05_project.model.entity.WaitingRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public interface WaitingRequestRepository extends JpaRepository<WaitingRequest, Long> {

    @Query("select w from WaitingRequest w where w.user.id=?1")
    Page<WaitingRequest> findAllByUserId(Long userId,Pageable pageable);
    Page<WaitingRequest>findAllByWaitingRequestStatus(WaitingRequestStatus status,Pageable pageable);


    @Modifying
    @Query("update WaitingRequest w set w.waitingRequestStatus =?1 where w.id=?2")
    void changeStatus(WaitingRequestStatus status, Long id);
}
