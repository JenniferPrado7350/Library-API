package com.nrisk.jennifer.libraryapi.service.impl;

import com.nrisk.jennifer.libraryapi.api.dto.LoanDTO;
import com.nrisk.jennifer.libraryapi.api.dto.LoanFilterDTO;
import com.nrisk.jennifer.libraryapi.exception.BusinessException;
import com.nrisk.jennifer.libraryapi.model.entity.Book;
import com.nrisk.jennifer.libraryapi.model.entity.Loan;
import com.nrisk.jennifer.libraryapi.model.repository.LoanRepository;
import com.nrisk.jennifer.libraryapi.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {
    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if(repository.existsByBookAndNotReturned(loan.getBook())){
            throw new BusinessException("Book already loaned");
        }
        return repository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return repository.save(loan);
    }

    @Override
    public Page<Loan> find(LoanFilterDTO filterDTO, Pageable pageable) {
        return repository.findByBookIsbnOrCustomer(filterDTO.getIsbn(), filterDTO.getCustomer(), pageable);
    }

    @Override
    public Page<Loan> getLoansByBook(Book book, Pageable pageable) {
        return repository.findByBook(book, pageable); //se passarmos o pageable como ultimo parametro do metodo, o springData ja vai entender que a consulta é paginada
    }

    @Override
    public List<Loan> getAllLateLoans() {
        final Integer loanDays = 4 ; //dias de prazo para o emprestimo
        LocalDate threeDaysAgo = LocalDate.now().minusDays(loanDays); //vai obter a data dos dias atrasados, sendo a data de hoje - (menos) a quantidade de dias que ele tem pra ficar atrasado
        return repository.findByLoanDateLessThanAndNotReturned(threeDaysAgo);// encontre pela data  de emprestimo menor que a data que eu passar no parametro e nao retornada(pq o emprestimo nao foi retornado)
    }
}
