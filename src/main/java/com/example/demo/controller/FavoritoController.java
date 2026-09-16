package com.example.demo.controller;

import com.example.demo.dto.FavoritoRequestDTO;
import com.example.demo.dto.FavoritoResponseDTO;
import com.example.demo.service.FavoritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
@Tag(name = "Favoritos", description = "Operaciones CRUD para los productos favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo favorito")
    public ResponseEntity<FavoritoResponseDTO> crear(@Valid @RequestBody FavoritoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(favoritoService.crear(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todos los favoritos")
    public List<FavoritoResponseDTO> listar() {
        return favoritoService.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un favorito por ID")
    public FavoritoResponseDTO obtenerPorId(@PathVariable Long id) {
        return favoritoService.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un favorito existente")
    public FavoritoResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody FavoritoRequestDTO dto) {
        return favoritoService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un favorito")
    public void eliminar(@PathVariable Long id) {
        favoritoService.eliminar(id);
    }
}