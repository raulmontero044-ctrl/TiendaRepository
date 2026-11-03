package es.shop.pedido.domain.exception;

public class ProductoNoDisponibleException extends RuntimeException {
    public ProductoNoDisponibleException(Long productoId) {
        super("Producto no disponible: " + productoId);
    }
}
