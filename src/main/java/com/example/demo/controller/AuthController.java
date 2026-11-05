package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegistroRequest;
import com.example.demo.dto.TokenResponse;
import com.example.demo.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro y login. Endpoints públicos, no requieren token.")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Registrar un usuario",
            description = "Crea un usuario con rol USER. La contraseña se guarda hasheada (BCrypt).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado")
    })
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public void registrar(@RequestBody RegistroRequest request) {
        usuarioService.registrar(request.getEmail(), request.getPassword());
    }

    @Operation(summary = "Iniciar sesión",
            description = "Valida las credenciales y devuelve un JWT firmado con el id y el rol del usuario. "
                    + "Se envía luego en el header Authorization: Bearer <token>.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login correcto, devuelve el token"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        String token = usuarioService.login(request.getEmail(), request.getPassword());
        return new TokenResponse(token);
    }
}