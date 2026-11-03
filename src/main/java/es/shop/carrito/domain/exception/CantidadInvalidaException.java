package es.shop.carrito.domain.exception;

public class CantidadInvalidaException extends RuntimeException {
    public CantidadInvalidaException(int cantidad) {
        super("Cantidad no válida: " + cantidad);
    }
}
