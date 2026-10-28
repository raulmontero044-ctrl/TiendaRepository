package es.cesguiro.shop.persistence.mapper;

import es.cesguiro.shop.domain.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setIdCatalogo(rs.getLong("id_catalogo"));
        product.setNombre(rs.getString("nombre"));
        product.setDescripcion(rs.getString("descripcion"));
        product.setPrecio(rs.getBigDecimal("precio"));
        product.setStock(rs.getInt("stock"));
        return product;
    }
}
