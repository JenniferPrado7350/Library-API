package com.nrisk.jennifer.libraryapi;

import com.nrisk.jennifer.libraryapi.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {
//estamos configurando para que todos os dia em um certo horario ela execute uma operacao, que é a de enviar um email para quem pegou livro emprestado, para lembrar de devolver

//	@Autowired
//	private EmailService emailService;

	@Bean
	public ModelMapper modelMapper(){ //vai criar uma instancia singleton do modelMapper para servir toda a aplicação
		return new ModelMapper();
	}

//Parte para testar envio de email:
//
//	@Bean
//	public CommandLineRunner runner(){ //vai executar assim que subir a aplicação
//		return args -> {
//			List<String> emails = Arrays.asList("bf3e465981-8a7193@inbox.mailtrap.io");
//			emailService.sendMails("Testando serviço de emails.", emails);
//			System.out.println("EMAILS ENVIADOS");
//		};
//	}

	//ao executar, abra na pagina http://localhost:8080/swagger-ui/index.html para visualizar os Swagger
	public static void main(String[] args) {

		SpringApplication.run(LibraryApiApplication.class, args);
	}

}
