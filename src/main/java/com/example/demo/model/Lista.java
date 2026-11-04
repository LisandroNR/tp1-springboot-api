package com.example.demo.model;

public class Lista {
    private Integer id;
    private String nombre;
    private Long usuarioId; // Agregado para TP3 (dueño de la lista)

    public Lista() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
}