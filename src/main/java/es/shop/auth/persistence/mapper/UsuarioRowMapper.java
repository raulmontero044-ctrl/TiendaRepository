package es.shop.auth.persistence.mapper;

import es.shop.auth.domain.Usuario;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioRowMapper implements RowMapper<Usuario> {
    @Override
    public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setUsername(rs.getString("username"));
        usuario.setPasswordHash(rs.getString("password_hash"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setRol(rs.getString("rol"));
        usuario.setActivo(rs.getBoolean("activo"));
        return usuario;
    }
}
