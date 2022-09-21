package com.nrisk.jennifer.libraryapi.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String s) {
        super(s); //chama a superclasse RuntimeException e passa a string s como parametro
    }
}
