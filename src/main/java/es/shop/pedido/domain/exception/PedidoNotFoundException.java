package es.shop.pedido.domain.exception;

public class PedidoNotFoundException extends RuntimeException {
    public PedidoNotFoundException(Long id) {
        super("Pedido not found with id: " + id);
    }
}
