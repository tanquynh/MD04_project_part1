package com.example.md05_project.repository;

import com.example.md05_project.model.dto.response.WaitingRequestDetailDTO;
import com.example.md05_project.model.entity.WaitingRequestDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
public interface WaitingRequestDetailRepository extends JpaRepository<WaitingRequestDetail,Long> {

    @Query("select w from WaitingRequestDetail  w where w.waitingRequest.id=?1")
    List<WaitingRequestDetail>findAllByRequestId(Long requestId);
}
