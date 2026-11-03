package es.shop.carrito.domain;

import es.shop.carrito.domain.exception.CantidadInvalidaException;

public class CarritoLinea {
    private final Long productoId;
    private int cantidad;

    public CarritoLinea(Long productoId, int cantidad) {
        if (productoId == null) {
            throw new IllegalArgumentException("La línea debe referenciar un producto");
        }
        this.productoId = productoId;
        cambiarCantidad(cantidad);
    }

    void cambiarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new CantidadInvalidaException(cantidad);
        }
        this.cantidad = cantidad;
    }

    public Long getProductoId() {
        return productoId;
    }

    public int getCantidad() {
        return cantidad;
    }
}
