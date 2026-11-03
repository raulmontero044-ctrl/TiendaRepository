package es.shop.pedido.persistence.mapper;

import es.shop.pedido.domain.EstadoPedido;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PedidoFilaRowMapper implements RowMapper<PedidoFila> {
    @Override
    public PedidoFila mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PedidoFila(
                rs.getLong("id"),
                rs.getLong("usuario_id"),
                rs.getTimestamp("fecha").toLocalDateTime(),
                EstadoPedido.valueOf(rs.getString("estado")));
    }
}
