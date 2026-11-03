package es.shop.carrito.apln;

import es.shop.carrito.domain.Carrito;
import es.shop.carrito.domain.CarritoDao;
import org.springframework.transaction.annotation.Transactional;

public class GestionarCarritoUseCase {
    private final CarritoDao carritoDao;

    public GestionarCarritoUseCase(CarritoDao carritoDao) {
        this.carritoDao = carritoDao;
    }

    public Carrito obtener(Long usuarioId) {
        return carritoDao.findByUsuarioId(usuarioId);
    }

    @Transactional
    public Carrito anadirLinea(Long usuarioId, Long productoId, int cantidad) {
        Carrito carrito = carritoDao.findByUsuarioId(usuarioId);
        carrito.anadir(productoId, cantidad);
        carritoDao.save(carrito);
        return carrito;
    }

    @Transactional
    public Carrito actualizarLinea(Long usuarioId, Long productoId, int cantidad) {
        Carrito carrito = carritoDao.findByUsuarioId(usuarioId);
        carrito.actualizarCantidad(productoId, cantidad);
        carritoDao.save(carrito);
        return carrito;
    }

    @Transactional
    public Carrito eliminarLinea(Long usuarioId, Long productoId) {
        Carrito carrito = carritoDao.findByUsuarioId(usuarioId);
        carrito.eliminar(productoId);
        carritoDao.save(carrito);
        return carrito;
    }

    @Transactional
    public void vaciar(Long usuarioId) {
        Carrito carrito = carritoDao.findByUsuarioId(usuarioId);
        carrito.vaciar();
        carritoDao.save(carrito);
    }
}
