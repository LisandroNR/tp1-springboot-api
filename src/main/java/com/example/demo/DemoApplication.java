package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone; // <-- Importamos la clase TimeZone

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        // 1. Forzamos la zona horaria a UTC para que no haya conflictos con la base de datos
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        
        // 2. Arrancamos Spring Boot normalmente
        SpringApplication.run(DemoApplication.class, args);
    }
}