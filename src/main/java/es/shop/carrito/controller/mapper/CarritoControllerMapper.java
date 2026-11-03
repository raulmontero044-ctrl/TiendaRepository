package es.shop.carrito.controller.mapper;

import es.shop.carrito.api.dto.CarritoDto;
import es.shop.carrito.api.dto.CarritoLineaDto;
import es.shop.carrito.domain.Carrito;

public class CarritoControllerMapper {
    public static CarritoDto toDto(Carrito carrito) {
        return new CarritoDto(
                carrito.getUsuarioId(),
                carrito.getLineas().stream()
                        .map(linea -> new CarritoLineaDto(linea.getProductoId(), linea.getCantidad()))
                        .toList());
    }
}
