package com.nrisk.jennifer.libraryapi.api.resource;


import com.nrisk.jennifer.libraryapi.api.dto.BookDTO;
import com.nrisk.jennifer.libraryapi.api.dto.LoanDTO;
import com.nrisk.jennifer.libraryapi.api.dto.LoanFilterDTO;
import com.nrisk.jennifer.libraryapi.api.dto.ReturnedLoanDTO;
import com.nrisk.jennifer.libraryapi.model.entity.Book;
import com.nrisk.jennifer.libraryapi.model.entity.Loan;
import com.nrisk.jennifer.libraryapi.service.BookService;
import com.nrisk.jennifer.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService service;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO dto){
        Book book = bookService
                .getBookByIsbn(dto.getIsbn())
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
        Loan entity = Loan.builder()
                .book(book)
                .customer(dto.getCustomer())
                .loanDate(LocalDate.now())
                .build();

    entity = service.save(entity);
        return  entity.getId();
    }

    @PatchMapping("{id}")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto){
        Loan loan = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)); //vamos pegar o emprestimo pelo id que sera recebido pno parametro do metodo
        loan.setReturned(dto.getReturned()); //quando tivermos o emprestimo, vamos setar o retorno com o retorno do objeto dto

        service.update(loan); //aqui vai atualizar as informacoes de retorno do objeto loan
    }

    @GetMapping
    public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pageRequest){
        Page<Loan> result = service.find(dto, pageRequest); //pagina

        List<LoanDTO> loans = result.
                getContent()//getContent vai retornar a lista que contem o resultado da pagina, chamamos o stream e o map para mapear o resultado que é o objeto do tipo Loan para o LoanDTO
                .stream()
                .map(entity -> { //iterando sobre cada um
                    Book book = entity.getBook(); //pegamos o livro do emprestimo, que é uma entidade livro
                    BookDTO bookDTO = modelMapper.map(book, BookDTO.class); //transformamos em DTO
                    LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class); //depois transformamos a entidade em DTO
                    loanDTO.setBook(bookDTO); // e setamos o livro DTO que obtemos transformando o livro em DTO
                    return loanDTO;
                }).collect(Collectors.toList());// para obter o conteudo como lista
        return new PageImpl<LoanDTO>(loans, pageRequest, result.getTotalElements());
    }
}
