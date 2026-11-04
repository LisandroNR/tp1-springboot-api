package com.example.demo.persistencia;

import com.example.demo.model.Lista;
import com.example.demo.model.UsuarioEntity;
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
        
        // Asignamos el usuario a la entidad para que Hibernate guarde la clave foránea
        if (lista.getUsuarioId() != null) {
            UsuarioEntity usuario = new UsuarioEntity();
            usuario.setId(lista.getUsuarioId());
            entity.setUsuario(usuario);
        }
        
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
        
        // Recuperamos el ID del usuario de la base de datos hacia el dominio
        if (entity.getUsuario() != null) {
            lista.setUsuarioId(entity.getUsuario().getId());
        }
        
        return lista;
    }
}