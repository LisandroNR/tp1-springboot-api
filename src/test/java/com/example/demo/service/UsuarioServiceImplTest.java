package com.example.demo.service;

import com.example.demo.config.JwtUtil;
import com.example.demo.dominio.Rol;
import com.example.demo.dominio.Usuario;
import com.example.demo.dominio.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void registrar_conContrasena_guardaHashNoTextoPlano() {
        // Preparación (Arrange)
        String email = "test@test.com";
        String passwordPlano = "123456";
        String passwordHash = "hash_falso_encriptado";

        when(usuarioRepository.existePorEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(passwordPlano)).thenReturn(passwordHash);
        
        Usuario usuarioGuardado = new Usuario(1L, email, passwordHash, Rol.USER);
        when(usuarioRepository.guardar(any(Usuario.class))).thenReturn(usuarioGuardado);

        // Ejecución (Act)
        usuarioService.registrar(email, passwordPlano);

        // Verificación (Assert)
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).guardar(captor.capture());
        Usuario capturado = captor.getValue();

        assertNotEquals(passwordPlano, capturado.getPasswordHash());
        assertEquals(passwordHash, capturado.getPasswordHash());
    }
}