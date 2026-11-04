package com.example.demo.service;

import com.example.demo.config.JwtUtil;
import com.example.demo.dominio.Rol;
import com.example.demo.dominio.Usuario;
import com.example.demo.dominio.UsuarioRepository;
import com.example.demo.exception.CredencialesInvalidasException;
import com.example.demo.exception.EmailYaExisteException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Usuario registrar(String email, String password) {
        if (usuarioRepository.existePorEmail(email)) {
            throw new EmailYaExisteException("El email ya está registrado");
        }
        String hash = passwordEncoder.encode(password);
        Usuario nuevo = new Usuario(null, email, hash, Rol.USER);
        return usuarioRepository.guardar(nuevo);
    }

    @Override
    public String login(String email, String password) {
        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new CredencialesInvalidasException("Email o contraseña incorrectos"));

        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException("Email o contraseña incorrectos");
        }

        return jwtUtil.generarToken(usuario);
    }
}