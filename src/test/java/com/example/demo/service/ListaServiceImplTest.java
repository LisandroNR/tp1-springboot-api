package com.example.demo.service;

import com.example.demo.model.Lista;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.ListaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListaServiceImplTest {

    @Mock
    private ListaRepository listaRepository;
    @Mock
    private FavoritoRepository favoritoRepository;

    @InjectMocks
    private ListaService listaService;

    @BeforeEach
    void setUp() {
        // Simulamos un usuario logueado con ID 99
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("user@test.com", null);
        auth.setDetails(99L);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void crearLista_asignaUsuarioAutenticadoIdComoDueno() {
        // Preparación (Arrange)
        Lista nuevaLista = new Lista();
        nuevaLista.setNombre("Mi lista de prueba");
        
        when(listaRepository.save(any(Lista.class))).thenAnswer(i -> i.getArguments()[0]);

        // Ejecución (Act)
        listaService.crearLista(nuevaLista);

        // Verificación (Assert)
        ArgumentCaptor<Lista> captor = ArgumentCaptor.forClass(Lista.class);
        verify(listaRepository).save(captor.capture());
        Lista capturada = captor.getValue();

        // Verificamos que le asignó el ID 99 del usuario simulado
        assertEquals(99L, capturada.getUsuarioId());
    }
}