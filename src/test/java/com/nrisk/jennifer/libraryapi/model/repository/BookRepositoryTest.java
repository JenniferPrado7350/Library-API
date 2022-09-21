package com.nrisk.jennifer.libraryapi.model.repository;

import com.nrisk.jennifer.libraryapi.model.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest //indica que vamos fzr testes com JPA, cria uma instancia do banco de dados em memoria apenas para rodar os testes, no final dos testes ele apaga tudo
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager; //criando um objeto TestEntityManager para criar um cenario, vai simular um entityManager do JPA configurado apenas para fazer testes

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado")
    public void returnTrueWhenIsbnExists(){
        //cenario

        String isbn = "123";

                //ja que o @DataJpaTest vai sempre limpar a base de dados quando o teste acabar de rodar
                //vamos criar um objeto book que vai persistir, nao sera apagado
        Book book = Book.builder().title("Aventuras").author("Fulano").isbn(isbn).build();
        entityManager.persist(book); //metodo persist serve para persistir uma entidade

        //execucao

        boolean exists = repository.existsByIsbn(isbn); //verifica se o isbn informado existe na base de dados e retorna o resultado boolean para a variavel exists

        //verificacao

        assertThat(exists).isTrue(); //verifica se o valor de exists é verdadeiro, se for falso vai acusar erro
    }

    @Test
    @DisplayName("Deve retornar falso se nao existir um livro na base com o isbn informado")
    public void returnFalseWhenIsbnDoesntExists(){
        //cenario

        String isbn = "123";

        //execucao

        boolean exists = repository.existsByIsbn(isbn); //verifica se o isbn informado existe na base de dados e retorna o resultado boolean para a variavel exists

        //verificacao

        assertThat(exists).isFalse(); //verifica se o valor de exists é verdadeiro, se for falso vai acusar erro
    }
}
