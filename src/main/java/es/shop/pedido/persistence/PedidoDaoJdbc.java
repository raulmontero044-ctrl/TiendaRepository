package es.shop.pedido.persistence;

import es.shop.pedido.domain.Pedido;
import es.shop.pedido.domain.PedidoDao;
import es.shop.pedido.domain.PedidoLinea;
import es.shop.pedido.persistence.mapper.PedidoFila;
import es.shop.pedido.persistence.mapper.PedidoFilaRowMapper;
import es.shop.pedido.persistence.mapper.PedidoLineaRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class PedidoDaoJdbc implements PedidoDao {
    private static final String SELECT_PEDIDO = "SELECT id, usuario_id, fecha, estado FROM pedidos";
    private static final String SELECT_LINEAS =
            "SELECT producto_id, nombre_producto, precio_unitario, cantidad FROM pedido_lineas WHERE pedido_id = ? ORDER BY id";

    private final JdbcTemplate jdbcTemplate;
    private final PedidoFilaRowMapper pedidoFilaRowMapper = new PedidoFilaRowMapper();
    private final PedidoLineaRowMapper pedidoLineaRowMapper = new PedidoLineaRowMapper();

    public PedidoDaoJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Pedido save(Pedido pedido) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO pedidos (usuario_id, fecha, estado, total) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, pedido.getUsuarioId());
            ps.setTimestamp(2, Timestamp.valueOf(pedido.getFecha()));
            ps.setString(3, pedido.getEstado().name());
            ps.setBigDecimal(4, pedido.getTotal());
            return ps;
        }, keyHolder);
        pedido.asignarId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        List<Object[]> lineas = pedido.getLineas().stream()
                .map(linea -> new Object[]{
                        pedido.getId(),
                        linea.getProductoId(),
                        linea.getNombreProducto(),
                        linea.getPrecioUnitario(),
                        linea.getCantidad()})
                .toList();
        jdbcTemplate.batchUpdate(
                "INSERT INTO pedido_lineas (pedido_id, producto_id, nombre_producto, precio_unitario, cantidad) VALUES (?, ?, ?, ?, ?)",
                lineas);
        return pedido;
    }

    @Override
    public List<Pedido> findByUsuarioId(Long usuarioId) {
        return jdbcTemplate.query(SELECT_PEDIDO + " WHERE usuario_id = ? ORDER BY fecha DESC, id DESC", pedidoFilaRowMapper, usuarioId)
                .stream()
                .map(this::conLineas)
                .toList();
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        return jdbcTemplate.query(SELECT_PEDIDO + " WHERE id = ?", pedidoFilaRowMapper, id)
                .stream()
                .findFirst()
                .map(this::conLineas);
    }

    private Pedido conLineas(PedidoFila fila) {
        List<PedidoLinea> lineas = jdbcTemplate.query(SELECT_LINEAS, pedidoLineaRowMapper, fila.id());
        return new Pedido(fila.id(), fila.usuarioId(), fila.fecha(), fila.estado(), lineas);
    }
}
