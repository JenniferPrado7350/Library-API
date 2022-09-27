package com.nrisk.jennifer.libraryapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.nrisk.jennifer.libraryapi.api.resource")) //aqui dizemos quai as apis do projeto que vamos documentar. Como colocamos basePackage sera todas as apis que estao no pacote com.nrisk.jennifer.libraryapi.api.resource
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo()); //aqui é os detalhes da api, criado no metodo abaixo
    }

    private ApiInfo apiInfo(){   //aqui voce declara os detalhes da api
        return new ApiInfoBuilder()
                .title("Library API") //titulo da api
                .description("API do projeto de bibliotecas")
                .version("1.0") //qual a versao da api, como é a primeira vamos colocar 1.0
                .contact(contact()) //é o contato do desenvolvedor, criado no metodo abaixo
                .build();
    }

    private Contact contact(){ //aqui voce declara o contato do desenvolvedor da api

        return new Contact("Jennifer do Prado", "https://github.com/JenniferPrado7350", "jennifertadeusilva@gmail.com");
    }

}
