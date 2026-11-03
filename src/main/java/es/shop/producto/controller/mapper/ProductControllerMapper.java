package es.shop.producto.controller.mapper;

import es.shop.producto.domain.Product;

public class ProductControllerMapper {
    public static Product toDomain(Object request) {
        return new Product();
    }
}
