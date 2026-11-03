package es.shop.pedido.domain.exception;

public class CarritoVacioException extends RuntimeException {
    public CarritoVacioException() {
        super("No se puede crear un pedido con el carrito vacío");
    }
}
