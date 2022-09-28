package com.nrisk.jennifer.libraryapi.api.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nrisk.jennifer.libraryapi.api.dto.BookDTO;
import com.nrisk.jennifer.libraryapi.exception.BusinessException;
import com.nrisk.jennifer.libraryapi.model.entity.Book;
import com.nrisk.jennifer.libraryapi.service.BookService;
import com.nrisk.jennifer.libraryapi.service.LoanService;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class) //estamos dizendo que o spring deve criar um mini contexto para rodar o test
@ActiveProfiles("test") //para rodar com perfil de teste e ter configurações que vao rodar apenas no ambiente de teste
@WebMvcTest(controllers = BookController.class) //vamos fzr apenas testes unitários e não de integração, então vai testar apenas o comportamento da api
@AutoConfigureMockMvc //springboot vai fzr uma configuração no teste,onde vai configurar um objeto para que possamos fazer as requisições
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired //PARA TERMOS UMA DEPENDENCIA DO SPRING
    MockMvc mvc; //vai estar simulando nossas requisições

    @MockBean //VAI CRIAR UMAS INSTANCIA MOCKADA DA CLASSE
    BookService service;

    @MockBean
    LoanService loanService;

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
                .andExpect(status().isCreated())              //andExpect para fzr nossas assertivas e verificar o que aconteceu, MockMvcResultMatchers são os verificadores, então esperamos que ele envie o CREATED quando for requisitado
                .andExpect(jsonPath("id").isNotEmpty())   // esperamos que ele envie como retorno o json com o id copulado. jsonPath vai verificar se o json de resposta esta igual ao do parametro
                //.andExpect(status().isCreated())        //    Siga o PASSO2 do word  API library para      //
                //.andExpect(jsonPath("id").isNotEmpty()) //obter essa forma minimizada das duas linhas acima//
                .andExpect( jsonPath("title").value(dto.getTitle()) )  //vai verificar se o json de resposta esta igual ao do parametro
                .andExpect( jsonPath("author").value(dto.getAuthor()) )     //"
                .andExpect( jsonPath("isbn").value(dto.getIsbn()) )     //"
                ;
    }
    @Test
    @DisplayName("Deve obter informações de um livro")
    public void getBookDetailsTest()throws Exception{
        //cenario (given)
        Long id = 1l;
        Book book = Book.builder()     //criando uma instancia de livro
                .id(id)                 //passando o id criado acima
                .title(createNewBook().getTitle())  //chamando o metodo createNewBook abaixo que retorna uma instancia, e chamando getTitle para pegar apenas o title dessa instancia DTO
                .author(createNewBook().getAuthor())//chamando o metodo createNewBook abaixo que retorna uma instancia, e chamando getAuthor para pegar apenas o author dessa instancia DTO
                .isbn(createNewBook().getIsbn())    //chamando o metodo createNewBook abaixo que retorna uma instancia, e chamando getIsbn para pegar apenas o isbn dessa instancia DTO
                .build();
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        //execucao (when)

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/"+id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk()) //espero que o status seja retornado isOk
                .andExpect( jsonPath("id").value(id))   // esperamos que ele envie como retorno o json com o id copulado. jsonPath vai verificar se o json de resposta esta igual ao do parametro
                .andExpect( jsonPath("title").value(createNewBook().getTitle()) )  //vai verificar se o json de resposta esta igual ao do parametro
                .andExpect( jsonPath("author").value(createNewBook().getAuthor()) )//"
                .andExpect( jsonPath("isbn").value(createNewBook().getIsbn()) );   //"
    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado nao existir")
    public void bookNotFoundTest() throws Exception{
        //cenario
        BDDMockito.given(service.getById(anyLong())).willReturn(Optional.empty()); //quando ele for no service buscar o livro de id qualquer, retorna o optional vazio, ja que nao encontrou o livro

        //execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/"+1))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound()); //espero que o status seja retornado NotFound(nao encontrado)

    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() throws Exception{
        //cenario
        BDDMockito.given(service.getById(anyLong())).willReturn(Optional.of(Book.builder().id(1l).build())); //estamos simulando que para qualquer Long id passado no parametro de getById o retorno seja um Optional de uma instancia de livro

        //execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/"+1)); //vai deletar o book /api/books/1, para deletar ele ele precisa existir no servidor, entao vamos fazer o mock acima

        //verificacao
        mvc.perform( request )
                .andExpect(status().isNoContent()); //apos a delecao, esperamos que status de retorno seja NoContent
    }

    @Test
    @DisplayName("Deve retornar resource not found quando não encontrar um livro para deletar")
    public void deleteInexistentBookTest() throws Exception{
        //cenario
        BDDMockito.given(service.getById(anyLong())).willReturn(Optional.empty()); //estamos simulando que para qualquer Long id passado no parametro de getById o retorno seja um Optional vazio, ja que nao vai encontrar

        //execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/"+1)); //vai deletar o book /api/books/1, para deletar ele ele precisa existir no servidor, entao vamos fazer o mock acima

        //verificacao
        mvc.perform( request )
                .andExpect(status().isNotFound()); //apos a delecao, esperamos que status de retorno seja NotFound
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() throws Exception{
        //cenario
        Long id = 1l;
        String json = new ObjectMapper().writeValueAsString(createNewBook());
        Book updatingBook = Book.builder().id(1l).title("some title").author("some author").isbn("321").build();
        BDDMockito.given(service.getById(anyLong()))                    //quando o servico buscar por id qualquer id de entrada
                .willReturn(Optional.of(updatingBook)); //vai simular que retornou uma Optional de uma instancia de livro
        Book updatedBook = Book.builder().id(id).author("Artur").title("As aventuras").isbn("321").build();
        BDDMockito.given(service.update(updatingBook)).willReturn(updatedBook);

        //execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/"+1)) //vai atualizar o book /api/books/1, para atualizar ele precisa existir no servidor, entao vamos fazer o mock acima
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        //verificacao
        mvc.perform( request )
                .andExpect(status().isOk())
                .andExpect( jsonPath("id").value(id))   // esperamos que ele envie como retorno o json com o id copulado. jsonPath vai verificar se o json de resposta esta igual ao do parametro
                .andExpect( jsonPath("title").value(createNewBook().getTitle()) )  //vai verificar se o json de resposta esta igual ao do parametro
                .andExpect( jsonPath("author").value(createNewBook().getAuthor()) )//"
                .andExpect( jsonPath("isbn").value("321") );   //"

    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar um livro inexistente")
    public void updateInexistentBookTest() throws Exception{
        //cenario
        String json = new ObjectMapper().writeValueAsString(createNewBook());
        BDDMockito.given(service.getById(Mockito.anyLong()))                    //quando o servico buscar por id qualquer id de entrada
                .willReturn(Optional.empty());

        //execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/"+1)) //vai atualizar o book /api/books/1, para atualizar ele precisa existir no servidor, entao vamos fazer o mock acima
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        //verificacao
        mvc.perform( request )
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve filtrar livros")
    public void findBookTest() throws Exception{
        Long id = 1l;

        Book book = Book.builder()
                .id(id)
                .title(createNewBook().getTitle())
                .author(createNewBook().getAuthor())
                .isbn(createNewBook().getIsbn())
                .build();

        BDDMockito.given(service.find(Mockito.any(Book.class), Mockito.any(Pageable.class))) //Pageable faz uma pesquisa paginada
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0,100), 1));  //se a pesquisa é paginada a resposta tbem será

        String queryString = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor()); //& significa que vou passar outro parametro

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content",Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    private BookDTO createNewBook() {
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
