package es.shop.auth.persistence;

import es.shop.auth.domain.Usuario;
import es.shop.auth.domain.UsuarioDao;
import es.shop.auth.persistence.mapper.UsuarioRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioDaoJdbc implements UsuarioDao {
    private static final String SELECT_COLUMNS = "SELECT id, username, password_hash, nombre, rol, activo FROM usuarios";

    private final JdbcTemplate jdbcTemplate;
    private final UsuarioRowMapper rowMapper = new UsuarioRowMapper();

    public UsuarioDaoJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        List<Usuario> usuarios = jdbcTemplate.query(SELECT_COLUMNS + " WHERE username = ?", rowMapper, username);
        return usuarios.isEmpty() ? Optional.empty() : Optional.of(usuarios.get(0));
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        List<Usuario> usuarios = jdbcTemplate.query(SELECT_COLUMNS + " WHERE id = ?", rowMapper, id);
        return usuarios.isEmpty() ? Optional.empty() : Optional.of(usuarios.get(0));
    }
}
