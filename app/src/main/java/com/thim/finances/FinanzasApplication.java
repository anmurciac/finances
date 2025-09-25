package com.thim.finances;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


//Aplicacion para iniciar la base de datos, si quieres probarla, aunque no hace mucho sin las request
//Estaba probandolas mediante un programa llamado Postman.
@SpringBootApplication
@ComponentScan(basePackages = "com.thim.finances")
public class FinanzasApplication {
    public static void main(String[] args) {
        SpringApplication.run(FinanzasApplication.class, args);
    }
}