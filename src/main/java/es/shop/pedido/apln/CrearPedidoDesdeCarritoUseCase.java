package es.shop.pedido.apln;

import es.shop.auth.api.AuthPublicApi;
import es.shop.carrito.api.CarritoPublicApi;
import es.shop.carrito.api.dto.CarritoDto;
import es.shop.carrito.api.dto.CarritoLineaDto;
import es.shop.pedido.domain.Pedido;
import es.shop.pedido.domain.PedidoDao;
import es.shop.pedido.domain.PedidoLinea;
import es.shop.pedido.domain.exception.ProductoNoDisponibleException;
import es.shop.pedido.domain.exception.StockInsuficienteException;
import es.shop.producto.api.ProductoPublicApi;
import es.shop.producto.api.dto.ProductoSnapshotDto;
import es.shop.shared.domain.exception.UsuarioNoAutenticadoException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public class CrearPedidoDesdeCarritoUseCase {
    private final AuthPublicApi authPublicApi;
    private final CarritoPublicApi carritoPublicApi;
    private final ProductoPublicApi productoPublicApi;
    private final PedidoDao pedidoDao;

    public CrearPedidoDesdeCarritoUseCase(AuthPublicApi authPublicApi, CarritoPublicApi carritoPublicApi,
                                          ProductoPublicApi productoPublicApi, PedidoDao pedidoDao) {
        this.authPublicApi = authPublicApi;
        this.carritoPublicApi = carritoPublicApi;
        this.productoPublicApi = productoPublicApi;
        this.pedidoDao = pedidoDao;
    }

    @Transactional
    public Pedido ejecutar() {
        Long usuarioId = authPublicApi.getCurrentUserId().orElseThrow(UsuarioNoAutenticadoException::new);
        CarritoDto carrito = carritoPublicApi.obtenerCarrito(usuarioId);
        List<PedidoLinea> lineas = carrito.lineas().stream()
                .map(this::lineaConSnapshot)
                .toList();
        Pedido pedido = pedidoDao.save(Pedido.crear(usuarioId, LocalDateTime.now(), lineas));
        carritoPublicApi.vaciarCarrito(usuarioId);
        return pedido;
    }

    private PedidoLinea lineaConSnapshot(CarritoLineaDto lineaCarrito) {
        Long productoId = lineaCarrito.productoId();
        ProductoSnapshotDto producto = productoPublicApi.obtenerProducto(productoId)
                .orElseThrow(() -> new ProductoNoDisponibleException(productoId));
        if (!productoPublicApi.hayStock(productoId, lineaCarrito.cantidad())) {
            throw new StockInsuficienteException(productoId, lineaCarrito.cantidad());
        }
        return new PedidoLinea(productoId, producto.nombre(), producto.precio(), lineaCarrito.cantidad());
    }
}
