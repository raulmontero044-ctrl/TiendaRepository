package es.shop.carrito.persistence.mapper;

import es.shop.carrito.domain.CarritoLinea;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CarritoLineaRowMapper implements RowMapper<CarritoLinea> {
    @Override
    public CarritoLinea mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CarritoLinea(rs.getLong("producto_id"), rs.getInt("cantidad"));
    }
}
