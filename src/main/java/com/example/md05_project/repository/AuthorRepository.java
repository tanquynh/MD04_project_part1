package com.example.md05_project.repository;

import com.example.md05_project.model.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author,Long> {
    boolean existsByName(String name);
    Author findAuthorByName(String name);
}
