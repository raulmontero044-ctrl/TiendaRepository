package es.shop.pedido.domain;

import es.shop.pedido.domain.exception.CarritoVacioException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Pedido {
    private Long id;
    private final Long usuarioId;
    private final LocalDateTime fecha;
    private final EstadoPedido estado;
    private final List<PedidoLinea> lineas;

    public Pedido(Long id, Long usuarioId, LocalDateTime fecha, EstadoPedido estado, List<PedidoLinea> lineas) {
        if (usuarioId == null || fecha == null || estado == null) {
            throw new IllegalArgumentException("El pedido requiere usuario, fecha y estado");
        }
        this.id = id;
        this.usuarioId = usuarioId;
        this.fecha = fecha;
        this.estado = estado;
        this.lineas = List.copyOf(lineas);
    }

    public static Pedido crear(Long usuarioId, LocalDateTime fecha, List<PedidoLinea> lineas) {
        if (lineas.isEmpty()) {
            throw new CarritoVacioException();
        }
        return new Pedido(null, usuarioId, fecha, EstadoPedido.CREADO, lineas);
    }

    public BigDecimal getTotal() {
        return lineas.stream()
                .map(PedidoLinea::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean perteneceA(Long usuarioId) {
        return this.usuarioId.equals(usuarioId);
    }

    public void asignarId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("El pedido ya tiene id");
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public List<PedidoLinea> getLineas() {
        return lineas;
    }
}
