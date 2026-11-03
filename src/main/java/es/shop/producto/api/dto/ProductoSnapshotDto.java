package es.shop.producto.api.dto;

import java.math.BigDecimal;

public record ProductoSnapshotDto(
        Long id,
        Long idCatalogo,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer stock) {
}
