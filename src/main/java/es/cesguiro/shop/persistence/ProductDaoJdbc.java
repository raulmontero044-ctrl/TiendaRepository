package es.cesguiro.shop.persistence;

import es.cesguiro.shop.domain.Product;
import es.cesguiro.shop.domain.ProductDao;
import es.cesguiro.shop.persistence.mapper.ProductRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductDaoJdbc implements ProductDao {
    private final JdbcTemplate jdbcTemplate;
    private final ProductRowMapper rowMapper = new ProductRowMapper();

    public ProductDaoJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Product> findAll() {
        String sql = "SELECT id, id_catalogo, nombre, descripcion, precio, stock FROM product";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public List<Product> findByCatalogo(Long idCatalogo) {
        String sql = "SELECT id, id_catalogo, nombre, descripcion, precio, stock FROM product WHERE id_catalogo = ?";
        return jdbcTemplate.query(sql, rowMapper, idCatalogo);
    }

    @Override
    public Optional<Product> findById(Long id) {
        String sql = "SELECT id, id_catalogo, nombre, descripcion, precio, stock FROM product WHERE id = ?";
        List<Product> products = jdbcTemplate.query(sql, rowMapper, id);
        return products.isEmpty() ? Optional.empty() : Optional.of(products.get(0));
    }

    @Override
    public Product save(Product product) {
        String sql = "INSERT INTO product (id_catalogo, nombre, descripcion, precio, stock) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, product.getIdCatalogo());
            ps.setString(2, product.getNombre());
            ps.setString(3, product.getDescripcion());
            ps.setBigDecimal(4, product.getPrecio());
            ps.setInt(5, product.getStock());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            product.setId(keyHolder.getKey().longValue());
        }
        return product;
    }

    @Override
    public Product update(Product product) {
        String sql = "UPDATE product SET id_catalogo = ?, nombre = ?, descripcion = ?, precio = ?, stock = ? WHERE id = ?";
        jdbcTemplate.update(sql, product.getIdCatalogo(), product.getNombre(), product.getDescripcion(),
                product.getPrecio(), product.getStock(), product.getId());
        return product;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM product WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
