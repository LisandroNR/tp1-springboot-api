package com.example.demo.dto;

public record ProductoDTO(
    Long id, 
    String titulo, 
    String descripcion, 
    Double precio
) {}