package com.example.demo.exception;

public class ServicioExternoException extends RuntimeException {
    
    // Este es el constructor que le faltaba para que acepte nuestro texto
    public ServicioExternoException(String message) {
        super(message);
    }
}