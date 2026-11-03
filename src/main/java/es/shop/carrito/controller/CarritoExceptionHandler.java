package es.shop.carrito.controller;

import es.shop.carrito.domain.exception.CantidadInvalidaException;
import es.shop.carrito.domain.exception.LineaCarritoNoEncontradaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = CarritoController.class)
public class CarritoExceptionHandler {
    @ExceptionHandler(CantidadInvalidaException.class)
    public ProblemDetail handleCantidadInvalida(CantidadInvalidaException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(LineaCarritoNoEncontradaException.class)
    public ProblemDetail handleLineaNoEncontrada(LineaCarritoNoEncontradaException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
