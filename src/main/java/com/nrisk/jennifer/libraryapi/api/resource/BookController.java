package com.nrisk.jennifer.libraryapi.api.resource;

import com.nrisk.jennifer.libraryapi.api.dto.BookDTO;
import com.nrisk.jennifer.libraryapi.api.dto.LoanDTO;
import com.nrisk.jennifer.libraryapi.api.exception.ApiErros;
import com.nrisk.jennifer.libraryapi.exception.BusinessException;
import com.nrisk.jennifer.libraryapi.model.entity.Book;
import com.nrisk.jennifer.libraryapi.model.entity.Loan;
import com.nrisk.jennifer.libraryapi.service.BookService;
import com.nrisk.jennifer.libraryapi.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books") //vai ser a url para as requisicoes
@Api("Book API")
@RequiredArgsConstructor //com o final nos atributos, nao precisamos de um construtor
@Slf4j //para usar os logs do actuator
public class BookController {

    private final BookService service;
    private final ModelMapper modelMapper; //é uma biblioteca que mapeia uma classe, uma instancia dela e transforma em uma classe DTO

    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("CREATE A BOOK") // Vai ser uma explicacao sobre o que essa requisicao faz, aparece no Swagger
    public BookDTO create(@RequestBody @Valid BookDTO dto){ //RequestBody vai fzr com que o json enviado como corpo da requisição seja convertido nesse dto, @Valid vai fazer com que o springBoot valide o objeto dto com base nas anotations @NotEmpty dos atributos da classe BookDTO
        //log do actuator:
        //log.info("creating a book for isbn: {}", dto.getIsbn());
        Book entity = modelMapper.map( dto, Book.class); //vai pegar a instancia dto, vai criar uma instancia de Book e vai transferir todas as propriedades de mesmo nome, entre a instancia dto e a classe Book, para a instancia criada da classe Book

       /* O codigo a seguir é o mesmo que o codigo acima, porem o cod acima foi refatorado
       Book entity = Book.builder()
                .author(dto.getAuthor())
                .title(dto.getTitle())
                .isbn(dto.getIsbn())
                .build(); //como nao temos o livro ainda, vamos criar uma instancia dele com .builder()*/


        entity = service.save(entity);
        return modelMapper.map(entity, BookDTO.class);

        /* O codigo a seguir é o mesmo que o codigo acima, porem o cod acima foi refatorado
        entity = service.save(entity); //salva a instancia criada com o builder
        return BookDTO.builder()
                .id(entity.getId())  //passamos o id pois ele deve retornar o id da entidade
                .author(entity.getAuthor())
                .title(entity.getTitle())
                .isbn(entity.getIsbn())
                .build();*/
    }

    @GetMapping("{id}")
    @ApiOperation("OBTAINS A BOOK DETAILS BY ID")
    public BookDTO get(@PathVariable Long id){
        //log do actuator:
        //log.info("obtaining details for book id: {}", id);
        return service
                .getById(id)
                .map( book -> modelMapper.map(book, BookDTO.class) ) //getById vai retornar um book, entao vamos mapear(percorrer) esse book para o BookDTO
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND)); //caso getById nao encontrar um book, lance exception NOT_FOUND
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("DELETE A BOOK BY ID")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Book succesfully deleted")  //isso faz com que ao inves de mostrar codigo OK quando deletar o livro, ele mostre a mensagem  "Book succesfully deleted" no Swagger
    })
    public void  delete(@PathVariable Long id){
        //log do actuator:
        //log.info("deleting book of id: {}", id);

        Book book = service.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND) );
        service.delete(book);
    }

    @PutMapping("{id}")
    @ApiOperation("UPDATES A BOOK")
    public BookDTO update(@PathVariable Long id, @RequestBody @Valid BookDTO dto){
        //log do actuator:
        //log.info("updating book of id: {}", id);

        /*Book book = service.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND) );
        book.setAuthor(dto.getAuthor());
        book.setTitle(dto.getTitle());
        book = service.update(book);
        return modelMapper.map(book, BookDTO.class);

        O CODIGO ACIMA FOI REFATORADO E TRANSFORMOU-SE NO CODIGO ABAIXO
        */
        return service.getById(id).map(book ->{ //quando encontrar o livro por id, ele vai atualizando as informacoes title author
            book.setAuthor(dto.getAuthor());
            book.setTitle(dto.getTitle());
            book = service.update(book);  //servico vai atualizar na base
            return modelMapper.map(book, BookDTO.class); // depois mapeia para o dto, transforma para o dto que tem que retornar

        }).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND) ); //caso contrario, retorna NOT_FOUND

    }

    @GetMapping
    @ApiOperation("FIND BOOKS BY PARAMS")
    public Page<BookDTO> find(BookDTO dto, Pageable pageRequest){
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = service.find(filter, pageRequest);
        List<BookDTO> list = result.getContent().stream()
                .map(entity -> modelMapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements());
    }

    @GetMapping("{id}/loans")
    public Page<LoanDTO> loansByBook(@PathVariable Long id, Pageable pageable){ //vai retornar uma pagina
        Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<Loan> result = loanService.getLoansByBook(book, pageable);
        List<LoanDTO> list = result.getContent()
                .stream()
                .map(loan -> { //vamos mapear loan
                    Book loanBook = loan.getBook(); //pegando o book salvo em loan(emprestimo)
                    BookDTO bookDTO = modelMapper.map(loanBook, BookDTO.class); //convertendo o book do tipo Book que estava salvo em loan no tipo BookDTO
                    LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);     //convertendo o loan do tipo Loan no tipo LoanDTO
                    loanDTO.setBook(bookDTO); //guardando novamente o book, agora no tipo BookDTO, no objeto loan, que agora é do tipo LoanDTO
                    return loanDTO;
                }).collect(Collectors.toList());//vai coletar o conteudo da string para uma lista

        return new PageImpl<LoanDTO>(list, pageable, result.getTotalElements());
    }


}
