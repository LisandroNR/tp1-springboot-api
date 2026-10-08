package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;

public class MoverFavoritosRequest {

    @NotNull(message = "El id de la lista destino es obligatorio")
    private Integer destinoId;

    public Integer getDestinoId() { return destinoId; }
    public void setDestinoId(Integer destinoId) { this.destinoId = destinoId; }
}