package com.example.md05_project.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitingRequestDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "waiting_request_id",referencedColumnName = "id")
    private WaitingRequest waitingRequest;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id",referencedColumnName = "id")
    private Book book;
}
