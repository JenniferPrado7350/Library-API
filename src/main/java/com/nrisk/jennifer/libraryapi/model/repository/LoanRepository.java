package com.nrisk.jennifer.libraryapi.model.repository;

import com.nrisk.jennifer.libraryapi.model.entity.Book;
import com.nrisk.jennifer.libraryapi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    //@Query serve quando nao conseguimos criar a query completa apenas com a sintaxe do query method
    @Query(value = " select case when ( count(l.id) > 0 ) then true else false end " + //caso quando a contagem de emprestimos seja maior do que 0, então true(o select vai ser true), caso contrario sera falso, end é pq sempre tem q terminar com end
            " from Loan l where l.book = :book and ( l.returned is null or l.returned is false )") //para a tabela Loan onde book seja igual a book. Obs: o book tem que estar colado do :. Queremos que ele nao tenha retornado(seja null) ou seja falso
    boolean existsByBookAndNotReturned(@Param("book") Book book); //book do parametro deve ser o mesmo de cima

    @Query( value = "select l from Loan as l join l.book as b where b.isbn = :isbn or l.customer =:customer ") //vai selecionar um emprestimo da tabela Loan de book (com id selecionado como join na classe Loan.java) nomeado como b onde o isbn do book recebe isbn ou customer do book recebe customer
    Page<Loan> findByBookIsbnOrCustomer(@Param("isbn") String isbn, @Param("customer") String customer, Pageable pageable);

    Page<Loan> findByBook(Book book, Pageable pageable);

    @Query( " select l from Loan l where l.loanDate <= :threeDaysAgo and ( l.returned is null or l.returned is false) ")
    List<Loan> findByLoanDateLessThanAndNotReturned( @Param("threeDaysAgo") LocalDate threeDaysAgo);
}
