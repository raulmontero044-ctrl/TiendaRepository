package es.shop.carrito.api.dto;

public record CarritoLineaDto(
        Long productoId,
        int cantidad) {
}
