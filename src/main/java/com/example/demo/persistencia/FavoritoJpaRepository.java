package com.example.demo.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoritoJpaRepository extends JpaRepository<FavoritoEntity, Integer> {
    // Consulta derivada requerida por la consigna (Punto 5.3)
    List<FavoritoEntity> findByLista_Id(Integer listaId);
}