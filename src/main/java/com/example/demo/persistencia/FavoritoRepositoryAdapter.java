package com.example.demo.persistencia;

import com.example.demo.model.Favorito;
import com.example.demo.repository.FavoritoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FavoritoRepositoryAdapter implements FavoritoRepository {

    private final FavoritoJpaRepository jpaRepository;

    public FavoritoRepositoryAdapter(FavoritoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Favorito save(Favorito favorito) {
        FavoritoEntity entity = new FavoritoEntity();
        
        if (favorito.getId() != null) {
            entity.setId(favorito.getId().intValue());
        }
        
        if (favorito.getProductoId() != null) {
            entity.setProductoId(String.valueOf(favorito.getProductoId()));
        }
        entity.setNota(favorito.getNotaPersonal());
        entity.setFechaAlta(favorito.getFechaCreacion());

        // Vinculamos la lista si viene el ID (Relación)
        if (favorito.getListaId() != null) {
            ListaEntity listaRef = new ListaEntity();
            listaRef.setId(favorito.getListaId());
            entity.setLista(listaRef);
        }

        FavoritoEntity guardado = jpaRepository.save(entity);
        return mapearADominio(guardado);
    }

    @Override
    public Optional<Favorito> findById(Long id) {
        return jpaRepository.findById(id.intValue()).map(this::mapearADominio);
    }

    @Override
    public List<Favorito> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::mapearADominio)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(Long id) {
        Integer idInt = id.intValue();
        if (jpaRepository.existsById(idInt)) {
            jpaRepository.deleteById(idInt);
            return true;
        }
        return false;
    }

    // Nuevo método para buscar por lista
    public List<Favorito> buscarPorLista(Integer listaId) {
        return jpaRepository.findByLista_Id(listaId).stream()
                .map(this::mapearADominio)
                .collect(Collectors.toList());
    }

    private Favorito mapearADominio(FavoritoEntity entity) {
        Favorito fav = new Favorito();
        
        if (entity.getId() != null) {
            fav.setId(entity.getId().longValue());
        }
        
        if (entity.getProductoId() != null) {
            fav.setProductoId(Long.valueOf(entity.getProductoId()));
        }
        fav.setNotaPersonal(entity.getNota());
        fav.setFechaCreacion(entity.getFechaAlta());
        
        // Mapeamos el ID de la lista de vuelta al dominio
        if (entity.getLista() != null) {
            fav.setListaId(entity.getLista().getId());
        }
        
        return fav;
    }
}