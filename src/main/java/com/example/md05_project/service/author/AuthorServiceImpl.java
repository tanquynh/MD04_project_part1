package com.example.md05_project.service.author;

import com.example.md05_project.exception.CustomException;
import com.example.md05_project.model.entity.Author;
import com.example.md05_project.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorServiceImpl implements AuthorService{
    @Autowired
    private AuthorRepository authorRepository;
    @Override
    public Author findAuthorByName(String name){
        return authorRepository.findAuthorByName(name);
    }
}
