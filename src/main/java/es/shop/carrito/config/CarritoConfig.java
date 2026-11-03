package es.shop.carrito.config;

import es.shop.carrito.api.CarritoPublicApi;
import es.shop.carrito.api.impl.CarritoPublicApiImpl;
import es.shop.carrito.apln.GestionarCarritoUseCase;
import es.shop.carrito.domain.CarritoDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CarritoConfig {
    @Bean
    public GestionarCarritoUseCase gestionarCarritoUseCase(CarritoDao carritoDao) {
        return new GestionarCarritoUseCase(carritoDao);
    }

    @Bean
    public CarritoPublicApi carritoPublicApi(GestionarCarritoUseCase gestionarCarritoUseCase) {
        return new CarritoPublicApiImpl(gestionarCarritoUseCase);
    }
}
