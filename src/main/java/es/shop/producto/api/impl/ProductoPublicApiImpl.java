package es.shop.producto.api.impl;

import es.shop.producto.api.ProductoPublicApi;
import es.shop.producto.api.dto.ProductoSnapshotDto;
import es.shop.producto.domain.Product;
import es.shop.producto.domain.ProductService;

import java.util.List;
import java.util.Optional;

public class ProductoPublicApiImpl implements ProductoPublicApi {
    private final ProductService productService;

    public ProductoPublicApiImpl(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public Optional<ProductoSnapshotDto> obtenerProducto(Long id) {
        return productService.obtenerProducto(id).map(this::toSnapshot);
    }

    @Override
    public List<ProductoSnapshotDto> listarProductos() {
        return productService.listarProductos().stream().map(this::toSnapshot).toList();
    }

    @Override
    public List<ProductoSnapshotDto> listarPorCatalogo(Long idCatalogo) {
        return productService.obtenerPorCatalogo(idCatalogo).stream().map(this::toSnapshot).toList();
    }

    @Override
    public boolean existeProducto(Long id) {
        return productService.obtenerProducto(id).isPresent();
    }

    @Override
    public boolean hayStock(Long id, int cantidad) {
        return productService.obtenerProducto(id)
                .map(product -> product.getStock() != null && product.getStock() >= cantidad)
                .orElse(false);
    }

    private ProductoSnapshotDto toSnapshot(Product product) {
        return new ProductoSnapshotDto(
                product.getId(),
                product.getIdCatalogo(),
                product.getNombre(),
                product.getDescripcion(),
                product.getPrecio(),
                product.getStock()
        );
    }
}
