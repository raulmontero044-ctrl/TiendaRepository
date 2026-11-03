package es.shop.carrito.domain.exception;

public class LineaCarritoNoEncontradaException extends RuntimeException {
    public LineaCarritoNoEncontradaException(Long productoId) {
        super("El carrito no contiene el producto: " + productoId);
    }
}
