package com.nrisk.jennifer.libraryapi.api.resource;

import com.nrisk.jennifer.libraryapi.api.dto.BookDTO;
import com.nrisk.jennifer.libraryapi.api.exception.ApiErros;
import com.nrisk.jennifer.libraryapi.exception.BusinessException;
import com.nrisk.jennifer.libraryapi.model.entity.Book;
import com.nrisk.jennifer.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books") //vai ser a url para as requisicoes
public class BookController {

    private BookService service;
    private ModelMapper modelMapper; //é uma biblioteca que mapeia uma classe, uma instancia dela e transforma em uma classe DTO
    public BookController(BookService service, ModelMapper mapper) {
        this.service = service;
        this.modelMapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO dto){ //RequestBody vai fzr com que o json enviado como corpo da requisição seja convertido nesse dto, @Valid vai fazer com que o springBoot valide o objeto dto com base nas anotations @NotEmpty dos atributos da classe BookDTO
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

    @ExceptionHandler(MethodArgumentNotValidException.class) ///toda vez que essa classe receber a exception MethodArgumentNotValidException(erro de validação), vai ser executado o metodo abaixo
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErros handleValidationExceptions(MethodArgumentNotValidException  ex) { // a exception MethodArgumentNotValidException vai ser lançada toda vez em que ele tentar validar um objeto, com o @Valid e o objeto não esta valido
        BindingResult bindingResult = ex.getBindingResult(); //BindingResult é o resultado da validação que ocorreu durante a validação do objeto com a anotations @Valid
        return  new ApiErros(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)// sempre que acontecer uma  BusinessException vai ser executado o metodo abaixo
    @ResponseStatus(HttpStatus.BAD_REQUEST) //precisa retornar  o BAD_REQUEST
    public ApiErros handleBusinessException(BusinessException ex){  // a exception BusinessException vai ser lançada toda vez em que ele tentar salvar outro livro que ja exista
        return  new ApiErros(ex);
    }
}
