package com.nrisk.jennifer.libraryapi.service.impl;

import com.nrisk.jennifer.libraryapi.model.entity.Loan;
import com.nrisk.jennifer.libraryapi.model.repository.LoanRepository;
import com.nrisk.jennifer.libraryapi.service.LoanService;

public class LoanServiceImpl implements LoanService {
    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        return repository.save(loan);
    }
}
