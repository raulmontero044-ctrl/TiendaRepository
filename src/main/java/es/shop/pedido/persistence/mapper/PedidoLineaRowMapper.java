package es.shop.pedido.persistence.mapper;

import es.shop.pedido.domain.PedidoLinea;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PedidoLineaRowMapper implements RowMapper<PedidoLinea> {
    @Override
    public PedidoLinea mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PedidoLinea(
                rs.getLong("producto_id"),
                rs.getString("nombre_producto"),
                rs.getBigDecimal("precio_unitario"),
                rs.getInt("cantidad"));
    }
}
