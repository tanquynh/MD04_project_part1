package com.example.md05_project.service.author;

import com.example.md05_project.exception.CustomException;
import com.example.md05_project.model.entity.Author;

public interface AuthorService {
    Author findAuthorByName(String name);
}
