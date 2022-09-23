package com.nrisk.jennifer.libraryapi.model.repository;

import com.nrisk.jennifer.libraryapi.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
