package com.nrisk.jennifer.libraryapi.service;

import com.nrisk.jennifer.libraryapi.model.entity.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?"; //tempo em que queremos enviar o email, sera em 0 segundos 0 minutos 0 horas 1/1 significa todos os dias * significa em qualquer mes e ? significa em qualquer ano

    @Value("${application.mail.lateloans.message}")
    private String message;

    private final LoanService loanService;
    private final EmailService emailService;

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendMailToLateLoans(){
        List<Loan> allLateLoans = loanService.getAllLateLoans(); //pega todos os emprestimos atrasados e coloca em uma lista Loan
        List<String> mailsList = allLateLoans.stream() //percorre a lista de emprestimos atrasados, vai pegando o email de cada customer de emprestimo e salvando em uma lista de String
                .map(loan -> loan.getCustomerEmail())
                .collect(Collectors.toList());

        emailService.sendMails(message, mailsList);
    }
}
