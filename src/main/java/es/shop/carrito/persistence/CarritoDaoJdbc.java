package es.shop.carrito.persistence;

import es.shop.carrito.domain.Carrito;
import es.shop.carrito.domain.CarritoDao;
import es.shop.carrito.domain.CarritoLinea;
import es.shop.carrito.persistence.mapper.CarritoLineaRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CarritoDaoJdbc implements CarritoDao {
    private final JdbcTemplate jdbcTemplate;
    private final CarritoLineaRowMapper rowMapper = new CarritoLineaRowMapper();

    public CarritoDaoJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Carrito findByUsuarioId(Long usuarioId) {
        String sql = "SELECT producto_id, cantidad FROM carrito_lineas WHERE usuario_id = ? ORDER BY producto_id";
        List<CarritoLinea> lineas = jdbcTemplate.query(sql, rowMapper, usuarioId);
        return new Carrito(usuarioId, lineas);
    }

    @Override
    public void save(Carrito carrito) {
        jdbcTemplate.update("DELETE FROM carrito_lineas WHERE usuario_id = ?", carrito.getUsuarioId());
        List<Object[]> filas = carrito.getLineas().stream()
                .map(linea -> new Object[]{carrito.getUsuarioId(), linea.getProductoId(), linea.getCantidad()})
                .toList();
        if (!filas.isEmpty()) {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO carrito_lineas (usuario_id, producto_id, cantidad) VALUES (?, ?, ?)", filas);
        }
    }
}
