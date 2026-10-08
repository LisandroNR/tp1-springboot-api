package com.example.demo.persistencia;

import com.example.demo.model.Lista;
import com.example.demo.repository.ListaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ListaRepositoryAdapter implements ListaRepository {

    private final ListaJpaRepository jpaRepository;

    public ListaRepositoryAdapter(ListaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Lista save(Lista lista) {
        ListaEntity entity = new ListaEntity();
        if (lista.getId() != null) {
            entity.setId(lista.getId());
        }
        entity.setNombre(lista.getNombre());
        
        ListaEntity guardado = jpaRepository.save(entity);
        return mapearADominio(guardado);
    }

    @Override
    public Optional<Lista> findById(Integer id) {
        return jpaRepository.findById(id).map(this::mapearADominio);
    }

    @Override
    public List<Lista> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::mapearADominio)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(Integer id) {
        if (jpaRepository.existsById(id)) {
            jpaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private Lista mapearADominio(ListaEntity entity) {
        Lista lista = new Lista();
        lista.setId(entity.getId());
        lista.setNombre(entity.getNombre());
        return lista;
    }
}
