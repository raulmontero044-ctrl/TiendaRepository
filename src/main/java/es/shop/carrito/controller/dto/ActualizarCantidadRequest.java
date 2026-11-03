package es.shop.carrito.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ActualizarCantidadRequest(
        @NotNull @PositiveOrZero Integer cantidad) {
}
