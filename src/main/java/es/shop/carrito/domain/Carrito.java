package es.shop.carrito.domain;

import es.shop.carrito.domain.exception.CantidadInvalidaException;
import es.shop.carrito.domain.exception.LineaCarritoNoEncontradaException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Carrito {
    private final Long usuarioId;
    private final List<CarritoLinea> lineas = new ArrayList<>();

    public Carrito(Long usuarioId) {
        this(usuarioId, List.of());
    }

    public Carrito(Long usuarioId, List<CarritoLinea> lineas) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El carrito debe pertenecer a un usuario");
        }
        this.usuarioId = usuarioId;
        this.lineas.addAll(lineas);
    }

    public void anadir(Long productoId, int cantidad) {
        if (cantidad <= 0) {
            throw new CantidadInvalidaException(cantidad);
        }
        Optional<CarritoLinea> existente = buscarLinea(productoId);
        if (existente.isPresent()) {
            existente.get().cambiarCantidad(existente.get().getCantidad() + cantidad);
        } else {
            lineas.add(new CarritoLinea(productoId, cantidad));
        }
    }

    public void actualizarCantidad(Long productoId, int cantidad) {
        if (cantidad < 0) {
            throw new CantidadInvalidaException(cantidad);
        }
        CarritoLinea linea = buscarLinea(productoId)
                .orElseThrow(() -> new LineaCarritoNoEncontradaException(productoId));
        if (cantidad == 0) {
            lineas.remove(linea);
        } else {
            linea.cambiarCantidad(cantidad);
        }
    }

    public void eliminar(Long productoId) {
        if (!lineas.removeIf(linea -> linea.getProductoId().equals(productoId))) {
            throw new LineaCarritoNoEncontradaException(productoId);
        }
    }

    public void vaciar() {
        lineas.clear();
    }

    public boolean estaVacio() {
        return lineas.isEmpty();
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public List<CarritoLinea> getLineas() {
        return Collections.unmodifiableList(lineas);
    }

    private Optional<CarritoLinea> buscarLinea(Long productoId) {
        return lineas.stream()
                .filter(linea -> linea.getProductoId().equals(productoId))
                .findFirst();
    }
}
