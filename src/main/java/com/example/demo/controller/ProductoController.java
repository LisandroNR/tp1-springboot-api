package com.example.demo.controller;

import com.example.demo.dto.ProductoDTO;
import com.example.demo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Catálogo de productos consumidos desde API externa")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los productos")
    public List<ProductoDTO> listarProductos() {
        return productoService.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un producto por ID")
    public ProductoDTO obtenerProducto(@PathVariable Long id) {
        return productoService.obtenerPorId(id);
    }
}