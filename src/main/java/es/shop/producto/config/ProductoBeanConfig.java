package es.shop.producto.config;

import es.shop.producto.api.ProductoPublicApi;
import es.shop.producto.api.impl.ProductoPublicApiImpl;
import es.shop.producto.domain.ProductDao;
import es.shop.producto.domain.ProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductoBeanConfig {
    @Bean
    public ProductService productService(ProductDao productDao) {
        return new ProductService(productDao);
    }

    @Bean
    public ProductoPublicApi productoPublicApi(ProductService productService) {
        return new ProductoPublicApiImpl(productService);
    }
}
