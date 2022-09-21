package com.nrisk.jennifer.libraryapi.api.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nrisk.jennifer.libraryapi.api.dto.BookDTO;
import com.nrisk.jennifer.libraryapi.exception.BusinessException;
import com.nrisk.jennifer.libraryapi.model.entity.Book;
import com.nrisk.jennifer.libraryapi.service.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class) //estamos dizendo que o spring deve criar um mini contexto para rodar o test
@ActiveProfiles("test") //para rodar com perfil de teste e ter configurações que vao rodar apenas no ambiente de teste
@WebMvcTest //vamos fzr apenas testes unitários e não de integração, então vai testar apenas o comportamento da api
@AutoConfigureMockMvc //springboot vai fzr uma configuração no teste,onde vai configurar um objeto para que possamos fazer as requisições
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired //PARA TERMOS UMA DEPENDENCIA DO SPRING
    MockMvc mvc; //vai estar simulando nossas requisições

    @MockBean //VAI CRIAR UMAS INSTANCIA MOCKADA DA CLASSE
    BookService service;

    @Test
    @DisplayName("Deve criar um livro com sucesso") //vamos definir o que o metodo abaixo vai testar
    public void createBookTest() throws  Exception { //retorna Exception para nenhuma chamada reclamar das Exceptions, caso der erro

        BookDTO dto = createNewBook(); // o builder evita instancias, ele cria um objeto de BookDTO
        Book savedBook = Book.builder().id(10l).author("Artur").title("As aventuras").isbn("001").build(); //fizemos um builder para Book para simular o retorno no service

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook); //dizemos ao spring q quando ele chamar a instancia service de BookService com o metodo save ele vai salvar qualquer livro no metodo save e depois vai retornar salvando savedBook, pois estamos forçando o retorno de savedBook. Independente do livro que for salvo como parametro, ele vai retornar o livro savedBook.
        String json = new ObjectMapper().writeValueAsString(dto); //writeValueAsString vai receber um objeto de qualquer tipo  e transforma ele em JSON

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)                             //MockMvcRequestBuilders.post serve para definir a requisição
                .contentType(MediaType.APPLICATION_JSON)   // indica que vamos passar um conteudo, em que a midia sera do tipo JSON
                .accept(MediaType.APPLICATION_JSON)         //servidor tambem aceita requisicoes de conteudo do tipo JSON
                .content(json);//vamos usar content para parra o nosso JSON nele, ou seja, temos que passar o corpo da requisicao, o JSON com os dados do livro


        mvc
                .perform(request)//vai receber o objeto de request criado acima
                .andExpect(MockMvcResultMatchers.status().isCreated())              //andExpect para fzr nossas assertivas e verificar o que aconteceu, MockMvcResultMatchers são os verificadores, então esperamos que ele envie o CREATED quando for requisitado
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())   // esperamos que ele envie como retorno o json com o id copulado. jsonPath vai verificar se o json de resposta esta igual ao do parametro
                //.andExpect(status().isCreated())        //    Siga o PASSO2 do word  API library para      //
                //.andExpect(jsonPath("id").isNotEmpty()) //obter essa forma minimizada das duas linhas acima//
                .andExpect( jsonPath("title").value(dto.getTitle()) )  //vai verificar se o json de resposta esta igual ao do parametro
                .andExpect( jsonPath("author").value(dto.getAuthor()) )     //"
                .andExpect( jsonPath("isbn").value(dto.getIsbn()) )     //"
                ;
    }

    private static BookDTO createNewBook() {
        return BookDTO.builder().author("Artur").title("As aventuras").isbn("001").build(); //vai retornar a instancia de um livro
    }

    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para a criação do livro") //vamos definir o que o metodo abaixo vai testar
    public void createInvalidBookTest() throws Exception{
        String json = new ObjectMapper().writeValueAsString(new BookDTO()); //criando um json passando um DTO vazio, entao cria um json vazio

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath("errors", hasSize(3))); //vai enviar erro avisando que os 3 elementos, title, author e isbn sao obrigatorios, tera um aviso para cada
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn ja utilizado por outro")
    public void createBookWithDuplicateIsbn() throws Exception {

        BookDTO dto = createNewBook();
        String json = new ObjectMapper().writeValueAsString(dto); //criando um json passando um DTO
        String mensagemErro = "Isbn ja cadastrado";
        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(mensagemErro)); //quando o service for salvar qualquer livro, vai lançar BusinessException(significa erro da regra de negocio) que o isbn ja foi cadastrado

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest()) //espera-se que encontre erro
                .andExpect(jsonPath("errors", hasSize(1))) //espera-se que a lista errors do json só tenha uma mensagem de erro(apenas um erro)
                .andExpect(jsonPath("errors[0]").value(mensagemErro)); //espera-se que a posição 0 do array de errors contenha o valor igual a string em mensagemErro, ja que na classe BusinessException passamos essa string para ser a mensagem de erro do RuntimeException
    }
}
