package com.example.demo.controller;

import com.example.demo.dto.ListaRequest;
import com.example.demo.model.Favorito;
import com.example.demo.model.Lista;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.service.ListaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listas")
public class ListaController {

    private final ListaService listaService;
    private final FavoritoRepository favoritoRepository;

    public ListaController(ListaService listaService, FavoritoRepository favoritoRepository) {
        this.listaService = listaService;
        this.favoritoRepository = favoritoRepository;
    }

    @Operation(summary = "Crear una nueva lista")
    @PostMapping
    public ResponseEntity<Lista> crearLista(@Valid @RequestBody ListaRequest request) {
        Lista nuevaLista = new Lista();
        nuevaLista.setNombre(request.getNombre());
        
        Lista listaGuardada = listaService.crearLista(nuevaLista);
        return ResponseEntity.status(HttpStatus.CREATED).body(listaGuardada);
    }

    @Operation(summary = "Obtener todas las listas")
    @GetMapping
    public ResponseEntity<List<Lista>> obtenerListas() {
        return ResponseEntity.ok(listaService.obtenerTodas());
    }

    @Operation(summary = "Obtener una lista por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<Lista> obtenerListaPorId(@PathVariable Integer id) {
        return listaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener los favoritos dentro de una lista específica")
    @GetMapping("/{id}/favoritos")
    public ResponseEntity<List<Favorito>> obtenerFavoritosDeLista(@PathVariable Integer id) {
        // Primero validamos si la lista existe
        if (listaService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(favoritoRepository.buscarPorLista(id));
    }

    @Operation(summary = "Eliminar una lista (falla si tiene favoritos)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLista(@PathVariable Integer id) {
        listaService.eliminarLista(id);
        return ResponseEntity.noContent().build();
    }

    // ... adentro de ListaController ...

    @Operation(summary = "Mover todos los favoritos de una lista a otra y borrar la de origen")
    @PostMapping("/{origenId}/mover-favoritos")
    public ResponseEntity<Void> moverFavoritos(
            @PathVariable Integer origenId,
            @Valid @RequestBody com.example.demo.dto.MoverFavoritosRequest request) {
        
        listaService.moverFavoritosYBorrarLista(origenId, request.getDestinoId());
        return ResponseEntity.noContent().build();
    }
}