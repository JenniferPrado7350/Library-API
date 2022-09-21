package com.nrisk.jennifer.libraryapi.model.repository;

import com.nrisk.jennifer.libraryapi.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {//JpaRepository é uma interface que recebe 2 parametros: a entidade, no caso Book, e tipo do id(ou chave primaria da entidade Book, no caso Long
    boolean existsByIsbn(String isbn); //esse metodo ja vai verificar se existe um isbn igual ao do parametro isbn na base de dados(repository)
}