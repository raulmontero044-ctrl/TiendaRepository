package es.shop.carrito.domain;

public interface CarritoDao {
    Carrito findByUsuarioId(Long usuarioId);

    void save(Carrito carrito);
}
