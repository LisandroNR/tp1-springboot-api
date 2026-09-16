package com.example.demo.repository;

import com.example.demo.model.Favorito;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class FavoritoRepository {
    
    // Simula nuestra tabla de base de datos
    private final Map<Long, Favorito> db = new ConcurrentHashMap<>();
    // Simula el ID autoincremental
    private final AtomicLong secuenciador = new AtomicLong(1);

    public Favorito save(Favorito favorito) {
        if (favorito.getId() == null) {
            favorito.setId(secuenciador.getAndIncrement());
        }
        db.put(favorito.getId(), favorito);
        return favorito;
    }

    public Optional<Favorito> findById(Long id) {
        return Optional.ofNullable(db.get(id));
    }

    public List<Favorito> findAll() {
        return new ArrayList<>(db.values());
    }

    public boolean deleteById(Long id) {
        return db.remove(id) != null;
    }
}