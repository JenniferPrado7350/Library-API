package com.nrisk.jennifer.libraryapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.ws.rs.ext.ParamConverter;
import java.util.List;

@Data //alem do @Getter e @Setter, ele cria tambem o @ToString e @EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity //vai dizer ao JPA que esta classe é uma entidade
@Table //nome da tabela da base de dados, vai ser book mesmo, ja que não especificamos nada no parametro da anotation @Table
public class Book {

    @Id //dizemos que o atributo id é a primary key
    @Column //estamos indicando que sera uma coluna na tabela do banco de dados. Nao é obrigatorio colocar o Column, o banco ja vai entender que são colunas com a anotation @Entity que colocamos na classe
    @GeneratedValue(strategy =  GenerationType.IDENTITY) //vai dizer que o camp "id" será AutoIncrement, strategy significa que passamos a estrategia de que o banco que vai se encarregar de gerar os valores de id
    private Long id;

    @Column
    private String title;
    @Column
    private String author;
    @Column
    private String isbn;

    @OneToMany(mappedBy = "book") //relacao 1 para muitos onde o relacionamento sera da propriedade book que esta na entidade Loan, ou seja, 1 livro para muitos emprestimos
    private List<Loan> loans;
}
