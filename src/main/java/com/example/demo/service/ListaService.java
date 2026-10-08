package com.example.demo.service;

import com.example.demo.model.Lista;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.ListaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ListaService {

    private final ListaRepository listaRepository;
    private final FavoritoRepository favoritoRepository;

    public ListaService(ListaRepository listaRepository, FavoritoRepository favoritoRepository) {
        this.listaRepository = listaRepository;
        this.favoritoRepository = favoritoRepository;
    }

    public Lista crearLista(Lista lista) {
        return listaRepository.save(lista);
    }

    public List<Lista> obtenerTodas() {
        return listaRepository.findAll();
    }

    public Optional<Lista> obtenerPorId(Integer id) {
        return listaRepository.findById(id);
    }

    public void eliminarLista(Integer id) {
        // 1. Validamos si la lista existe
        if (listaRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La lista no existe");
        }

        // 2. Buscamos los favoritos. Si la lista devuelta tiene al menos 1 elemento, tiramos el 409
        List<com.example.demo.model.Favorito> favoritos = favoritoRepository.buscarPorLista(id);
        if (favoritos != null && !favoritos.isEmpty()) {
            // --- ACÁ USAMOS LA NUEVA EXCEPCIÓN ---
            throw new com.example.demo.exception.ListaConFavoritosException("No se puede eliminar una lista que contiene favoritos");
        }

        // 3. Si pasó las dos validaciones de arriba, borramos tranquilo
        listaRepository.deleteById(id);
    }

    // --- Punto 7: Transacciones ---
    @Transactional
    public void moverFavoritosYBorrarLista(Integer origenId, Integer destinoId) {
        // 1. Validamos que ambas listas existan
        if (listaRepository.findById(origenId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La lista de origen no existe");
        }
        if (listaRepository.findById(destinoId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La lista de destino no existe");
        }

        // 2. Buscamos todos los favoritos de la lista origen
        List<com.example.demo.model.Favorito> favoritosAMover = favoritoRepository.buscarPorLista(origenId);

        // 3. Movemos los favoritos uno por uno a la lista destino
        for (com.example.demo.model.Favorito fav : favoritosAMover) {
            fav.setListaId(destinoId);
            favoritoRepository.save(fav);
        }

        // 4. Eliminamos la lista de origen (ahora que está vacía, no tira error 409)
        listaRepository.deleteById(origenId);
    }
}