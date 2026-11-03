package es.shop.carrito.api;

import es.shop.carrito.api.dto.CarritoDto;

public interface CarritoPublicApi {
    CarritoDto obtenerCarrito(Long usuarioId);

    void vaciarCarrito(Long usuarioId);
}
