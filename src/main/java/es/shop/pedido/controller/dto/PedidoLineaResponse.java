package es.shop.pedido.controller.dto;

import java.math.BigDecimal;

public record PedidoLineaResponse(
        Long productoId,
        String nombreProducto,
        BigDecimal precioUnitario,
        int cantidad,
        BigDecimal subtotal) {
}
