package es.shop.producto.api;

import es.shop.producto.api.dto.ProductoSnapshotDto;

import java.util.List;
import java.util.Optional;

public interface ProductoPublicApi {
    Optional<ProductoSnapshotDto> obtenerProducto(Long id);

    List<ProductoSnapshotDto> listarProductos();

    List<ProductoSnapshotDto> listarPorCatalogo(Long idCatalogo);

    boolean existeProducto(Long id);

    boolean hayStock(Long id, int cantidad);
}
