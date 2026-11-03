package es.shop.pedido.controller.mapper;

import es.shop.pedido.controller.dto.PedidoLineaResponse;
import es.shop.pedido.controller.dto.PedidoResponse;
import es.shop.pedido.domain.Pedido;
import es.shop.pedido.domain.PedidoLinea;

public class PedidoControllerMapper {
    public static PedidoResponse toResponse(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getFecha(),
                pedido.getEstado().name(),
                pedido.getTotal(),
                pedido.getLineas().stream().map(PedidoControllerMapper::toResponse).toList());
    }

    private static PedidoLineaResponse toResponse(PedidoLinea linea) {
        return new PedidoLineaResponse(
                linea.getProductoId(),
                linea.getNombreProducto(),
                linea.getPrecioUnitario(),
                linea.getCantidad(),
                linea.getSubtotal());
    }
}
