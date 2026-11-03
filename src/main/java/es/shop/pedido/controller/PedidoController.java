package es.shop.pedido.controller;

import es.shop.pedido.apln.ConsultarPedidosUseCase;
import es.shop.pedido.apln.CrearPedidoDesdeCarritoUseCase;
import es.shop.pedido.controller.dto.PedidoResponse;
import es.shop.pedido.controller.mapper.PedidoControllerMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    private final CrearPedidoDesdeCarritoUseCase crearPedidoDesdeCarritoUseCase;
    private final ConsultarPedidosUseCase consultarPedidosUseCase;

    public PedidoController(CrearPedidoDesdeCarritoUseCase crearPedidoDesdeCarritoUseCase,
                            ConsultarPedidosUseCase consultarPedidosUseCase) {
        this.crearPedidoDesdeCarritoUseCase = crearPedidoDesdeCarritoUseCase;
        this.consultarPedidosUseCase = consultarPedidosUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse crearDesdeCarrito() {
        return PedidoControllerMapper.toResponse(crearPedidoDesdeCarritoUseCase.ejecutar());
    }

    @GetMapping
    public List<PedidoResponse> misPedidos() {
        return consultarPedidosUseCase.misPedidos().stream()
                .map(PedidoControllerMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public PedidoResponse obtener(@PathVariable Long id) {
        return PedidoControllerMapper.toResponse(consultarPedidosUseCase.obtener(id));
    }
}
