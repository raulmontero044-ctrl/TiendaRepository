package es.shop.carrito.api.dto;

import java.util.List;

public record CarritoDto(
        Long usuarioId,
        List<CarritoLineaDto> lineas) {
}
