package es.shop.pedido.config;

import es.shop.auth.api.AuthPublicApi;
import es.shop.carrito.api.CarritoPublicApi;
import es.shop.pedido.apln.ConsultarPedidosUseCase;
import es.shop.pedido.apln.CrearPedidoDesdeCarritoUseCase;
import es.shop.pedido.domain.PedidoDao;
import es.shop.producto.api.ProductoPublicApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PedidoConfig {
    @Bean
    public CrearPedidoDesdeCarritoUseCase crearPedidoDesdeCarritoUseCase(AuthPublicApi authPublicApi,
                                                                         CarritoPublicApi carritoPublicApi,
                                                                         ProductoPublicApi productoPublicApi,
                                                                         PedidoDao pedidoDao) {
        return new CrearPedidoDesdeCarritoUseCase(authPublicApi, carritoPublicApi, productoPublicApi, pedidoDao);
    }

    @Bean
    public ConsultarPedidosUseCase consultarPedidosUseCase(AuthPublicApi authPublicApi, PedidoDao pedidoDao) {
        return new ConsultarPedidosUseCase(authPublicApi, pedidoDao);
    }
}
