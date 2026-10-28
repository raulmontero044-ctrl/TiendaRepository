package es.shop.controller.mapper;

import es.shop.domain.Product;

import java.math.BigDecimal;

public class ProductControllerMapper {
    public static Product toDomain(Object request) {
        return new Product();
    }
}
