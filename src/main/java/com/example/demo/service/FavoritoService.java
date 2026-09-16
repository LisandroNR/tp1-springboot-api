package com.example.demo.service;

import com.example.demo.dto.FavoritoRequestDTO;
import com.example.demo.dto.FavoritoResponseDTO;
import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.model.Favorito;
import com.example.demo.repository.FavoritoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoritoService {

    private final FavoritoRepository repository;

    public FavoritoService(FavoritoRepository repository) {
        this.repository = repository;
    }

    public FavoritoResponseDTO crear(FavoritoRequestDTO dto) {
        Favorito favorito = new Favorito();
        favorito.setProductoId(dto.productoId());
        favorito.setNotaPersonal(dto.notaPersonal());
        
        Favorito guardado = repository.save(favorito);
        return mapearADto(guardado);
    }

    public List<FavoritoResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::mapearADto)
                .toList();
    }

    public FavoritoResponseDTO obtenerPorId(Long id) {
        Favorito favorito = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el favorito con ID: " + id));
        return mapearADto(favorito);
    }

    public FavoritoResponseDTO actualizar(Long id, FavoritoRequestDTO dto) {
        Favorito favorito = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el favorito con ID: " + id));
        
        favorito.setProductoId(dto.productoId());
        favorito.setNotaPersonal(dto.notaPersonal());
        
        Favorito actualizado = repository.save(favorito);
        return mapearADto(actualizado);
    }

    public void eliminar(Long id) {
        boolean eliminado = repository.deleteById(id);
        if (!eliminado) {
            throw new RecursoNoEncontradoException("No se encontró el favorito con ID: " + id + " para eliminar");
        }
    }

    // Método auxiliar para no repetir código
    private FavoritoResponseDTO mapearADto(Favorito f) {
        return new FavoritoResponseDTO(
                f.getId(), 
                f.getProductoId(), 
                f.getNotaPersonal(), 
                f.getFechaCreacion()
        );
    }
}