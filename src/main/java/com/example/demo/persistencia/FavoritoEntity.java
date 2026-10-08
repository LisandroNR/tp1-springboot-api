package com.example.demo.persistencia;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favoritos")
public class FavoritoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "producto_id")
    private String productoId;
    
    private String nota;
    
    @Column(name = "fecha_alta")
    private LocalDateTime fechaAlta;

    // --- Acá agregamos la relación ManyToOne que pide el TP ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lista_id")
    private ListaEntity lista;

    public FavoritoEntity() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getProductoId() { return productoId; }
    public void setProductoId(String productoId) { this.productoId = productoId; }
    
    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }
    
    public LocalDateTime getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(LocalDateTime fechaAlta) { this.fechaAlta = fechaAlta; }

    public ListaEntity getLista() { return lista; }
    public void setLista(ListaEntity lista) { this.lista = lista; }
}