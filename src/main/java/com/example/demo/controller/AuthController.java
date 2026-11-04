package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegistroRequest;
import com.example.demo.dto.TokenResponse;
import com.example.demo.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public void registrar(@RequestBody RegistroRequest request) {
        usuarioService.registrar(request.getEmail(), request.getPassword());
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        String token = usuarioService.login(request.getEmail(), request.getPassword());
        return new TokenResponse(token);
    }
}