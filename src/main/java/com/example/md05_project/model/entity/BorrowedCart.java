package com.example.md05_project.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BorrowedCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(columnDefinition = "date default (DATE(NOW()))")
    private LocalDate startDate = LocalDate.now();

    @Column(columnDefinition = "date")
    private LocalDate endDate = LocalDate.now().plusDays(2);

    private String note;

    private double fine=0;
    private int count;

    @Enumerated(EnumType.STRING)
    private BorrowedCartStatus borrowedCartStatus;

//    @OneToMany(mappedBy = "borrowedCart", fetch = FetchType.EAGER)
//    @JsonIgnore
//    Set<Cart> carts;

    @OneToMany(mappedBy = "borrowedCart", fetch = FetchType.EAGER)
    @JsonIgnore
    Set<BorrowedCartDetail> borrowedCartDetails;
}
