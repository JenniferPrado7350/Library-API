package com.nrisk.jennifer.libraryapi.api.exception;

import com.nrisk.jennifer.libraryapi.exception.BusinessException;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiErros {
    private List<String> errors;
    public ApiErros(BindingResult bindingResult) {
        this.errors = new ArrayList<>();

        bindingResult.getAllErrors().forEach( error -> this.errors.add(error.getDefaultMessage())); //vai percorrer os erros que ocorreram na validação, e salva-los na lista errors
    }

    public ApiErros(BusinessException ex) {
        this.errors = Arrays.asList(ex.getMessage()); //estamos passando as mensagens do vetor message do objeto ex do tipo BusinessException para a lista de errors, asList passa os elementos do vetor(Array) para a lista especificada
    }

    public List<String> getErrors() {
        return errors;
    }
}
