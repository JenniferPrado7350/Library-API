package com.nrisk.jennifer.libraryapi.service;

import com.nrisk.jennifer.libraryapi.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {

    Book save(Book any);

    Optional<Book> getById(Long id); //optional pois pode ser que exista um livro com esse id ou nao

    void delete(Book book);

    Book update(Book book);

    Page<Book> find(Book filter, Pageable pageRequest);
}
