package com.example.demo.repository;

import com.example.demo.model.Favorito;
import java.util.List;
import java.util.Optional;

public interface FavoritoRepository {
    Favorito save(Favorito favorito);
    Optional<Favorito> findById(Long id);
    List<Favorito> findAll();
    boolean deleteById(Long id);
    
    // --- Nuevo método ---
    List<Favorito> buscarPorLista(Integer listaId); 
}