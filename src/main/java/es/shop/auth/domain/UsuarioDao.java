package es.shop.auth.domain;

import java.util.Optional;

public interface UsuarioDao {
    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findById(Long id);
}
