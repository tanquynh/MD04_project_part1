package com.example.md05_project.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
//@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(columnDefinition = "date default (DATE(NOW()))")
    private LocalDate startDate = LocalDate.now();

    private String note;

    @Enumerated(EnumType.STRING)
    private WaitingRequestStatus waitingRequestStatus = WaitingRequestStatus.PENDING;

    @OneToMany(mappedBy = "waitingRequest", fetch = FetchType.EAGER)
    @JsonIgnore
    Set<WaitingList> waitingList;

    @OneToMany(mappedBy = "waitingRequest", fetch = FetchType.EAGER)
    @JsonIgnore
    Set<WaitingRequestDetail> waitingRequestDetails;
}
