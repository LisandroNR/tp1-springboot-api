package com.example.demo.dto;

import java.time.LocalDateTime;

public record FavoritoResponseDTO(
    Long id,
    Long productoId,
    String notaPersonal,
    LocalDateTime fechaCreacion,
    
    // --- Agregamos el ID de la lista acá para que coincida ---
    Integer listaId 
) {}