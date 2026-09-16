package com.example.demo.service;

import com.example.demo.client.DummyJsonClient;
import com.example.demo.client.dummyjson.DummyJsonProducto;
import com.example.demo.dto.ProductoDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final DummyJsonClient dummyJsonClient;

    public ProductoService(DummyJsonClient dummyJsonClient) {
        this.dummyJsonClient = dummyJsonClient;
    }

    public List<ProductoDTO> listarTodos() {
        // Llamamos al cliente, sacamos la lista de productos y los transformamos a nuestro DTO
        return dummyJsonClient.obtenerProductos().products().stream()
                .map(this::mapearADto)
                .toList();
    }

    public ProductoDTO obtenerPorId(Long id) {
        // Buscamos un producto puntual y lo transformamos
        DummyJsonProducto productoExterno = dummyJsonClient.obtenerProductoPorId(id);
        return mapearADto(productoExterno);
    }

    // Método auxiliar para traducir del formato externo al nuestro
    private ProductoDTO mapearADto(DummyJsonProducto p) {
        return new ProductoDTO(
                p.id(),
                p.title(),
                p.description(),
                p.price()
        );
    }
}