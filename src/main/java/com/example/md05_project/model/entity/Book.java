package com.example.md05_project.model.entity;

import com.example.md05_project.model.dto.request.BookRequestDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private int stock;
    private String image;
    private double unitPrice;
    @Column(columnDefinition = "boolean default true")
    private boolean status = true; // Tình trạng sách

    @Column(columnDefinition = "date default (DATE(NOW()))")
    private LocalDate createdAt;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "genre_id", referencedColumnName = "id")
    private Genre genre;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors;

    @OneToMany(mappedBy = "book",fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Cart>carts;

    @OneToMany(mappedBy = "book",fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<WaitingList>lists;

    @OneToMany(mappedBy = "book",fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<WaitingRequestDetail>waitingRequestDetails;

    @OneToMany(mappedBy = "book",fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<BorrowedCartDetail>borrowedCartDetails;
}