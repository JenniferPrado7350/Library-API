package com.nrisk.jennifer.libraryapi.api;

import com.nrisk.jennifer.libraryapi.api.exception.ApiErros;
import com.nrisk.jennifer.libraryapi.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice //diz para o sistema que essa classe vai ter configurações globais para todas as APIs
public class ApplicationControllerAdvice {

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

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity handleResponseStatusException(ResponseStatusException ex){
        return new ResponseEntity(new ApiErros(ex), ex.getStatus());
    }
}
