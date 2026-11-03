package es.shop.carrito.api.impl;

import es.shop.carrito.api.CarritoPublicApi;
import es.shop.carrito.api.dto.CarritoDto;
import es.shop.carrito.api.dto.CarritoLineaDto;
import es.shop.carrito.apln.GestionarCarritoUseCase;
import es.shop.carrito.domain.Carrito;

public class CarritoPublicApiImpl implements CarritoPublicApi {
    private final GestionarCarritoUseCase gestionarCarritoUseCase;

    public CarritoPublicApiImpl(GestionarCarritoUseCase gestionarCarritoUseCase) {
        this.gestionarCarritoUseCase = gestionarCarritoUseCase;
    }

    @Override
    public CarritoDto obtenerCarrito(Long usuarioId) {
        return toDto(gestionarCarritoUseCase.obtener(usuarioId));
    }

    @Override
    public void vaciarCarrito(Long usuarioId) {
        gestionarCarritoUseCase.vaciar(usuarioId);
    }

    private CarritoDto toDto(Carrito carrito) {
        return new CarritoDto(
                carrito.getUsuarioId(),
                carrito.getLineas().stream()
                        .map(linea -> new CarritoLineaDto(linea.getProductoId(), linea.getCantidad()))
                        .toList());
    }
}
