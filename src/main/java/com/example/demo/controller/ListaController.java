package com.example.demo.controller;

import com.example.demo.dto.ListaRequest;
import com.example.demo.dto.MoverFavoritosRequest;
import com.example.demo.model.Favorito;
import com.example.demo.model.Lista;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.service.ListaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listas")
@Tag(name = "Listas",
        description = "Requiere autenticación (Bearer JWT). Un usuario solo accede a sus propias listas; "
                + "un ADMIN accede a las de cualquiera.")
@SecurityRequirement(name = "bearerAuth")
public class ListaController {

    private final ListaService listaService;
    private final FavoritoRepository favoritoRepository;

    public ListaController(ListaService listaService, FavoritoRepository favoritoRepository) {
        this.listaService = listaService;
        this.favoritoRepository = favoritoRepository;
    }

    @Operation(summary = "Crear una nueva lista",
            description = "Requiere autenticación. El dueño es el usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lista creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido o vencido")
    })
    @PostMapping
    public ResponseEntity<Lista> crearLista(@Valid @RequestBody ListaRequest request) {
        Lista nuevaLista = new Lista();
        nuevaLista.setNombre(request.getNombre());

        Lista listaGuardada = listaService.crearLista(nuevaLista);
        return ResponseEntity.status(HttpStatus.CREATED).body(listaGuardada);
    }

    @Operation(summary = "Obtener las listas",
            description = "Requiere autenticación. Devuelve solo las listas del usuario autenticado "
                    + "(todas, si es ADMIN).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de listas"),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido o vencido")
    })
    @GetMapping
    public ResponseEntity<List<Lista>> obtenerListas() {
        return ResponseEntity.ok(listaService.obtenerTodas());
    }

    @Operation(summary = "Obtener una lista por su ID",
            description = "Requiere autenticación. Solo el dueño o un ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista encontrada"),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido o vencido"),
            @ApiResponse(responseCode = "403", description = "La lista pertenece a otro usuario"),
            @ApiResponse(responseCode = "404", description = "La lista no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Lista> obtenerListaPorId(@PathVariable Integer id) {
        return listaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener los favoritos dentro de una lista específica",
            description = "Requiere autenticación. Solo el dueño o un ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Favoritos de la lista"),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido o vencido"),
            @ApiResponse(responseCode = "403", description = "La lista pertenece a otro usuario"),
            @ApiResponse(responseCode = "404", description = "La lista no existe")
    })
    @GetMapping("/{id}/favoritos")
    public ResponseEntity<List<Favorito>> obtenerFavoritosDeLista(@PathVariable Integer id) {
        // Primero validamos si la lista existe
        if (listaService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(favoritoRepository.buscarPorLista(id));
    }

    @Operation(summary = "Eliminar una lista (falla si tiene favoritos)",
            description = "Requiere autenticación. Solo el dueño o un ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Lista eliminada"),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido o vencido"),
            @ApiResponse(responseCode = "403", description = "La lista pertenece a otro usuario"),
            @ApiResponse(responseCode = "404", description = "La lista no existe"),
            @ApiResponse(responseCode = "409", description = "La lista tiene favoritos")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLista(@PathVariable Integer id) {
        listaService.eliminarLista(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Mover todos los favoritos de una lista a otra y borrar la de origen",
            description = "Requiere autenticación. La lista de origen debe ser del usuario (o ser ADMIN).")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Favoritos movidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido o vencido"),
            @ApiResponse(responseCode = "403", description = "La lista pertenece a otro usuario"),
            @ApiResponse(responseCode = "404", description = "Lista de origen o destino inexistente")
    })
    @PostMapping("/{origenId}/mover-favoritos")
    public ResponseEntity<Void> moverFavoritos(
            @PathVariable Integer origenId,
            @Valid @RequestBody MoverFavoritosRequest request) {

        listaService.moverFavoritosYBorrarLista(origenId, request.getDestinoId());
        return ResponseEntity.noContent().build();
    }
}