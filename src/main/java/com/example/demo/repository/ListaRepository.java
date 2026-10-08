package com.example.demo.repository;

import com.example.demo.model.Lista;
import java.util.List;
import java.util.Optional;

public interface ListaRepository {
    Lista save(Lista lista);
    Optional<Lista> findById(Integer id);
    List<Lista> findAll();
    boolean deleteById(Integer id);
}