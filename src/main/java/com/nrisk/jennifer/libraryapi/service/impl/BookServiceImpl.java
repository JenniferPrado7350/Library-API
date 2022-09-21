package com.nrisk.jennifer.libraryapi.service.impl;

import com.nrisk.jennifer.libraryapi.exception.BusinessException;
import com.nrisk.jennifer.libraryapi.model.entity.Book;
import com.nrisk.jennifer.libraryapi.model.repository.BookRepository;
import com.nrisk.jennifer.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

@Service //esteriotipamos a classe como um serviço
public class BookServiceImpl implements BookService {
    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn()) ){  //vai verificar se existe um livro na base de dados com o isbn book.getIsbn()
           throw  new BusinessException("Isbn ja cadastrado");
        }
        return repository.save(book);
    }
}
