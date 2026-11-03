package es.shop.pedido.domain;

import java.math.BigDecimal;

/**
 * Snapshot del producto en el momento de crear el pedido: no cambia aunque el producto se modifique después.
 */
public class PedidoLinea {
    private final Long productoId;
    private final String nombreProducto;
    private final BigDecimal precioUnitario;
    private final int cantidad;

    public PedidoLinea(Long productoId, String nombreProducto, BigDecimal precioUnitario, int cantidad) {
        if (productoId == null || nombreProducto == null || precioUnitario == null) {
            throw new IllegalArgumentException("La línea de pedido requiere producto, nombre y precio");
        }
        if (precioUnitario.signum() < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    public Long getProductoId() {
        return productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public int getCantidad() {
        return cantidad;
    }
}
