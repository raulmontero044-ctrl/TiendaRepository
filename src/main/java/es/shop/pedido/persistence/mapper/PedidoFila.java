package es.shop.pedido.persistence.mapper;

import es.shop.pedido.domain.EstadoPedido;

import java.time.LocalDateTime;

/**
 * Cabecera de pedido leída de BD, antes de cargar sus líneas.
 */
public record PedidoFila(
        Long id,
        Long usuarioId,
        LocalDateTime fecha,
        EstadoPedido estado) {
}
