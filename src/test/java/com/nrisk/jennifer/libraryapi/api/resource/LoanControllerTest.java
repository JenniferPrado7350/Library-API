package com.nrisk.jennifer.libraryapi.api.resource;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nrisk.jennifer.libraryapi.api.dto.LoanDTO;
import com.nrisk.jennifer.libraryapi.api.dto.LoanFilterDTO;
import com.nrisk.jennifer.libraryapi.api.dto.ReturnedLoanDTO;
import com.nrisk.jennifer.libraryapi.exception.BusinessException;
import com.nrisk.jennifer.libraryapi.model.entity.Book;
import com.nrisk.jennifer.libraryapi.model.entity.Loan;
import com.nrisk.jennifer.libraryapi.service.BookService;
import com.nrisk.jennifer.libraryapi.service.LoanService;
import com.nrisk.jennifer.libraryapi.service.LoanServiceTest;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WebMvcTest(controllers = LoanController.class)
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    private BookService bookService;
    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("Deve realizar um emprestimo")
    public void createLoanTest() throws Exception{

        LoanDTO dto = LoanDTO.builder().isbn("123").email("customer@email.com").customer("Fulano").build(); //criando um emprestimo
        String json = new ObjectMapper().writeValueAsString(dto);               //transformando em json

        Book book = Book.builder().id(1l).isbn("123").build();                  //criando um livro
        BDDMockito.given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.of(book));  //quando for pegar o livro do bookService passando o isbn como parametro, que é o livro que o usuario que pegar emprestado
                                                                                      // retorna uma simulacao de um livro da base de dados, que é o book acima
        Loan loan = Loan.builder()  //cria um objeto do Tipo Loan, que é emprestimo, esse objeto tem com um de seus atributos o objeto book do tipo Book
                .id(1l)
                .customer("Fulano")
                .book(book)
                .loanDate(LocalDate.now())
                .build();
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        //requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isCreated())  //esperamos que o resultado seja criado
                .andExpect( content().string("1")); //e esperamos que o id do objeto emprestimo retornado da requisicao seja igual a 1, que é o id do objeto loan
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro inexistente")
    public void  invalidIsbnCreateLoanTest() throws Exception{

        LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn("123"))  //quando ele tentar buscar o livro com o isbn indicado ele vai retornar um Optional empty, um resultado vazio
                .willReturn(Optional.empty());

        //requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect( status().isBadRequest())  //esperamos que o resultado seja BadRequest
                .andExpect( jsonPath("errors", Matchers.hasSize(1)))  //esperamos que tenha apenas 1 mensagem de erro
                .andExpect(jsonPath("errors[0]").value("Book not found for passed isbn")); // esperamos que a mensagem desse unico erro da lista errors seja "Book not found for passed isbn"
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro inexistente")
    public void  loanedBookErrorOnCreateLoanTest() throws Exception{

        LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1l).isbn("123").build();                  //criando um livro
        BDDMockito.given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.of(book));  //quando for pegar o livro do bookService passando o isbn como parametro, que é o livro que o usuario que pegar emprestado

        BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
                .willThrow(new BusinessException("Book already loaned"));

        //requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        //verificacao
        mvc.perform(request)
                .andExpect( status().isBadRequest())  //esperamos que o resultado seja BadRequest
                .andExpect( jsonPath("errors", Matchers.hasSize(1)))  //esperamos que tenha apenas 1 mensagem de erro
                .andExpect(jsonPath("errors[0]").value("Book already loaned")); // esperamos que a mensagem desse unico erro da lista errors seja "Book already loaned"
    }

    @Test
    @DisplayName("Deve retornar um livro")
    public void returnBookTest() throws Exception{
        //cenario { returned: true }
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        Loan loan = Loan.builder().id(1l).build();
        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.of(loan));

        String json = new ObjectMapper().writeValueAsString(dto);

        mvc.perform(
                patch(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isOk());

        Mockito.verify(loanService, Mockito.times(1)).update(loan);
    }


    @Test
    @DisplayName("Deve retornar 404 quando tentar devolver um livro inexistente.")
    public void returnInexistentBookTest() throws Exception{
        //cenario { returned: true }
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);
        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        mvc.perform(
                patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve filtrar emprestimos")
    public void findLoansTest() throws Exception{
        Long id = 1l;

        Loan loan = LoanServiceTest.createLoan();
        loan.setId(id);
        Book book = Book.builder().id(1l).isbn("321").build();
        loan.setBook(book);

        BDDMockito.given(loanService.find(Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class))) //Pageable faz uma pesquisa paginada
                .willReturn(new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0,10), 1));  //se a pesquisa é paginada a resposta tbem será

        String queryString = String.format("?isbn=%s&customer=%s&page=0&size=10", //vai pesquisar esses atibutos
                book.getIsbn(), loan.getCustomer()); //& significa que vou passar outro parametro

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content",Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(10)) //DEVE SER O MESMO SIZE QUE O DE PageRequest.of
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }
}
