package es.shop.carrito.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AnadirLineaRequest(
        @NotNull Long productoId,
        @NotNull @Positive Integer cantidad) {
}
