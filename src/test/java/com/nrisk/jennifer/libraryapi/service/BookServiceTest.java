package com.nrisk.jennifer.libraryapi.service;

import com.nrisk.jennifer.libraryapi.exception.BusinessException;
import com.nrisk.jennifer.libraryapi.model.entity.Book;
import com.nrisk.jennifer.libraryapi.model.repository.BookRepository;
import com.nrisk.jennifer.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test") //apenas testes unitarios
public class BookServiceTest {

    BookService service;
    @MockBean
    BookRepository repository;

    @BeforeEach //faz com que o metodo a seguir seja executado antes de cada teste da classe
    public void setUp(){
        this.service = new BookServiceImpl(repository);
    }


    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        //cenario
        Book book = createValidBook(); //criando uma instancia de Book
        Mockito.when(repository.existsByIsbn(Mockito.anyString()) ).thenReturn(false); //quando executar o metodo existsByIsbn do repository, para qualquer string, retorna false

        Mockito.when( repository.save(book)).thenReturn( //simula o comportamento do repository. Vai passar pra save o objeto book e quando ele salvar especificamente a instancia book, ele vai retornar o objeto abaixo
                Book.builder().id(1l)
                        .isbn("123")
                        .author("Fulano")
                        .title("As aventuras").build()
        );


        //execucao
        Book savedBook = service.save(book); //salva o objeto book

        //verificacao
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
        assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
    }

    private static Book createValidBook() {
        return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveABookWithDuplicatedISBN(){
    //cenario
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString()) ).thenReturn(true); //quando executar o metodo existsByIsbn do repository, para qualquer string, retorna true
        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));//quando executar o save, ele vai lancar uma exception e salva-la na variavel exception do tipo Throwable

        //verificacoes
        assertThat(exception) //vamos verificar na exception
                .isInstanceOf(BusinessException.class) //se ela é uma instancia da classe BusinessException, que é a exception que ela deve ser
                .hasMessage("Isbn ja cadastrado");     //e se a mensagem dessa exception é "Isbn ja cadastrado"

        //temos que proibir de chamar o save do repository, pois mesmo lancando erro,ele chama o save
        Mockito.verify(repository, Mockito.never()).save(book); //vai verificar que o repository nunca vai executar o metodo save com o parametro book
    }
}
