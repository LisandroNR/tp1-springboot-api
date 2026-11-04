package com.example.demo.dominio;

import java.util.Optional;

public interface UsuarioRepository {
    Usuario guardar(Usuario usuario);
    Optional<Usuario> buscarPorEmail(String email);
    boolean existePorEmail(String email);
}