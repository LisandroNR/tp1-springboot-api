package com.example.demo.client;

import com.example.demo.client.dummyjson.DummyJsonProducto;
import com.example.demo.client.dummyjson.DummyJsonProductosResponse;
import com.example.demo.exception.ServicioExternoException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class DummyJsonClient {

    private final RestClient restClient;

    public DummyJsonClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public DummyJsonProductosResponse obtenerProductos() {
        try {
            return restClient.get()
                    .uri("/products")
                    .retrieve()
                    .body(DummyJsonProductosResponse.class);
        } catch (RestClientException e) {
            throw new ServicioExternoException("Error al comunicarse con DummyJSON");
        }
    }

    public DummyJsonProducto obtenerProductoPorId(Long id) {
        try {
            return restClient.get()
                    .uri("/products/{id}", id)
                    .retrieve()
                    .body(DummyJsonProducto.class);
        } catch (RestClientException e) {
            throw new ServicioExternoException("Error al comunicarse con DummyJSON para el producto " + id);
        }
    }
}