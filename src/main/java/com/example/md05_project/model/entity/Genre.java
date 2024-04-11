package com.example.md05_project.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String genreName;
    private boolean status;
    @OneToMany(mappedBy = "genre",fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Book> books;
}
