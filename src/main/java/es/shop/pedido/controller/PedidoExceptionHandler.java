package es.shop.pedido.controller;

import es.shop.pedido.domain.exception.CarritoVacioException;
import es.shop.pedido.domain.exception.PedidoNotFoundException;
import es.shop.pedido.domain.exception.ProductoNoDisponibleException;
import es.shop.pedido.domain.exception.StockInsuficienteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = PedidoController.class)
public class PedidoExceptionHandler {
    @ExceptionHandler(CarritoVacioException.class)
    public ProblemDetail handleCarritoVacio(CarritoVacioException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler({StockInsuficienteException.class, ProductoNoDisponibleException.class})
    public ProblemDetail handleNoServible(RuntimeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(PedidoNotFoundException.class)
    public ProblemDetail handleNotFound(PedidoNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
