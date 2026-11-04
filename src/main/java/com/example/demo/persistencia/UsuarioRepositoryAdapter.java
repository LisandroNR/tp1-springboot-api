package com.example.demo.persistencia;

import com.example.demo.dominio.Usuario;
import com.example.demo.dominio.UsuarioRepository;
import com.example.demo.model.UsuarioEntity;
import com.example.demo.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getPasswordHash(),
                usuario.getRol()
        );
        UsuarioEntity saved = jpaRepository.save(entity);
        return new Usuario(saved.getId(), saved.getEmail(), saved.getPasswordHash(), saved.getRol());
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(entity -> new Usuario(entity.getId(), entity.getEmail(), entity.getPasswordHash(), entity.getRol()));
    }

    @Override
    public boolean existePorEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}