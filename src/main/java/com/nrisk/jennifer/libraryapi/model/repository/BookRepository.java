package com.nrisk.jennifer.libraryapi.model.repository;

import com.nrisk.jennifer.libraryapi.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {//JpaRepository é uma interface que recebe 2 parametros: a entidade, no caso Book, e tipo do id(ou chave primaria da entidade Book, no caso Long
    boolean existsByIsbn(String isbn); //esse metodo ja vai verificar se existe um isbn igual ao do parametro isbn na base de dados(repository), ele é automatico, verifica devido a palavra exists

    Optional<Book> findByIsbn(String isbn); // vai procurar um livro pelo isbn. Ele ja faz isso automaticamente, devido a palavra find
    // o Optional significa que ele pode retornar o objeto ou n, pois o objeto pode nao existir, ao inves de verificar se o objeto ta nulo, só verificamos se ele esta copulado

}