package com.nrisk.jennifer.libraryapi.api.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter   //lombok vai criar gets em tempo de execução
@Setter   //lombok vai criar sets em tempo de execução
@Builder  //lombok vai gerar um builder para a classe alvo, com nosso DTO, ou seja, com as propriedade id, title, author e isbn
@NoArgsConstructor //lombok vai criar um construtor sem argumentos
@AllArgsConstructor ////lombok vai criar um construtor com todos os argumentos
public class BookDTO {

    private Long id;

    @NotEmpty
    private String title;

    @NotEmpty
    private String author;

    @NotEmpty
    private String isbn;


}
