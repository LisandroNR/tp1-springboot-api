package com.example.demo.service;

import com.example.demo.model.Lista;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.ListaRepository;
import com.example.demo.exception.AccesoDenegadoException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ListaService {

    private final ListaRepository listaRepository;
    private final FavoritoRepository favoritoRepository;

    public ListaService(ListaRepository listaRepository, FavoritoRepository favoritoRepository) {
        this.listaRepository = listaRepository;
        this.favoritoRepository = favoritoRepository;
    }

    // --- MÉTODOS AUXILIARES DE SEGURIDAD ---
    private Long getUsuarioAutenticadoId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // El JwtAuthenticationFilter guardó el ID acá
        return (Long) auth.getDetails(); 
    }

    private boolean isUsuarioAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private void validarPropietario(Lista lista) {
        if (isUsuarioAdmin()) return; // El admin puede hacer todo
        
        Long usuarioId = getUsuarioAutenticadoId();
        if (!usuarioId.equals(lista.getUsuarioId())) {
            throw new AccesoDenegadoException("No tenés permiso para operar sobre esta lista");
        }
    }
    // ---------------------------------------

    public Lista crearLista(Lista lista) {
        // Ignoramos lo que venga en el body y forzamos el dueño
        lista.setUsuarioId(getUsuarioAutenticadoId());
        return listaRepository.save(lista);
    }

    public List<Lista> obtenerTodas() {
        List<Lista> todas = listaRepository.findAll();
        
        if (isUsuarioAdmin()) {
            return todas; // Si es admin, devuelve todas las listas de la base de datos
        }
        
        // Si es un usuario común, filtramos y devolvemos solo las suyas
        Long usuarioId = getUsuarioAutenticadoId();
        return todas.stream()
                .filter(lista -> usuarioId.equals(lista.getUsuarioId()))
                .collect(Collectors.toList());
    }

    public Optional<Lista> obtenerPorId(Integer id) {
        Optional<Lista> listaOpt = listaRepository.findById(id);
        if (listaOpt.isPresent()) {
            validarPropietario(listaOpt.get());
        }
        return listaOpt;
    }

    public void eliminarLista(Integer id) {
        Lista lista = listaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La lista no existe"));

        validarPropietario(lista);

        List<com.example.demo.model.Favorito> favoritos = favoritoRepository.buscarPorLista(id);
        if (favoritos != null && !favoritos.isEmpty()) {
            throw new com.example.demo.exception.ListaConFavoritosException("No se puede eliminar una lista que contiene favoritos");
        }

        listaRepository.deleteById(id);
    }

    @Transactional
    public void moverFavoritosYBorrarLista(Integer origenId, Integer destinoId) {
        Lista listaOrigen = listaRepository.findById(origenId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La lista de origen no existe"));
        
        Lista listaDestino = listaRepository.findById(destinoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La lista de destino no existe"));

        // Verificamos que sea dueño de ambas listas involucradas
        validarPropietario(listaOrigen);
        validarPropietario(listaDestino);

        List<com.example.demo.model.Favorito> favoritosAMover = favoritoRepository.buscarPorLista(origenId);

        for (com.example.demo.model.Favorito fav : favoritosAMover) {
            fav.setListaId(destinoId);
            favoritoRepository.save(fav);
        }

        listaRepository.deleteById(origenId);
    }
}