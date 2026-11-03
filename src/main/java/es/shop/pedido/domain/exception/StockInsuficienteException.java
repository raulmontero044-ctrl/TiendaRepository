package es.shop.pedido.domain.exception;

public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(Long productoId, int cantidad) {
        super("Stock insuficiente para el producto " + productoId + " (solicitado: " + cantidad + ")");
    }
}
