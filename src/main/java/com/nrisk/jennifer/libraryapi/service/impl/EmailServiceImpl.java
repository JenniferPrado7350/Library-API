package com.nrisk.jennifer.libraryapi.service.impl;

import com.nrisk.jennifer.libraryapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${application.mail.default-remetent}")
    private String remetent;
    private final JavaMailSender javaMailSender;
    @Override
    public void sendMails(String message, List<String> mailsList){
        String[] mails = mailsList.toArray(new String[mailsList.size()]); //pegando a lista de emails e transformando em array

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(remetent); //quem enviou
        mailMessage.setSubject("Livro com emprestimo atrasado"); //vai ser o assunto do email
        mailMessage.setText(message);  //o que vai estar escrito no email
        mailMessage.setTo(mails); //para quem vai o email

        javaMailSender.send(mailMessage);
    }
}
