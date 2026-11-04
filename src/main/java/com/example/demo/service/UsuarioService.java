package com.example.demo.service;

import com.example.demo.dominio.Usuario;

public interface UsuarioService {
    Usuario registrar(String email, String password);
    String login(String email, String password);
}