package es.cesguiro.shop.config;

import es.cesguiro.shop.domain.ProductDao;
import es.cesguiro.shop.domain.ProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public ProductService productService(ProductDao productDao) {
        return new ProductService(productDao);
    }
}
