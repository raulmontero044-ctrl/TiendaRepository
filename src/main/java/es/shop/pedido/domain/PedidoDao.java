package es.shop.pedido.domain;

import java.util.List;
import java.util.Optional;

public interface PedidoDao {
    Pedido save(Pedido pedido);

    List<Pedido> findByUsuarioId(Long usuarioId);

    Optional<Pedido> findById(Long id);
}
