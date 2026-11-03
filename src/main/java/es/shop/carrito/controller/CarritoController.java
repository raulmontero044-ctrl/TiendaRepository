package es.shop.carrito.controller;

import es.shop.auth.api.AuthPublicApi;
import es.shop.carrito.api.dto.CarritoDto;
import es.shop.carrito.apln.GestionarCarritoUseCase;
import es.shop.carrito.controller.dto.ActualizarCantidadRequest;
import es.shop.carrito.controller.dto.AnadirLineaRequest;
import es.shop.carrito.controller.mapper.CarritoControllerMapper;
import es.shop.shared.domain.exception.UsuarioNoAutenticadoException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {
    private final GestionarCarritoUseCase gestionarCarritoUseCase;
    private final AuthPublicApi authPublicApi;

    public CarritoController(GestionarCarritoUseCase gestionarCarritoUseCase, AuthPublicApi authPublicApi) {
        this.gestionarCarritoUseCase = gestionarCarritoUseCase;
        this.authPublicApi = authPublicApi;
    }

    @GetMapping
    public CarritoDto verCarrito() {
        return CarritoControllerMapper.toDto(gestionarCarritoUseCase.obtener(usuarioActual()));
    }

    @PostMapping("/lineas")
    public CarritoDto anadirLinea(@Valid @RequestBody AnadirLineaRequest request) {
        return CarritoControllerMapper.toDto(
                gestionarCarritoUseCase.anadirLinea(usuarioActual(), request.productoId(), request.cantidad()));
    }

    @PutMapping("/lineas/{productoId}")
    public CarritoDto actualizarLinea(@PathVariable Long productoId,
                                      @Valid @RequestBody ActualizarCantidadRequest request) {
        return CarritoControllerMapper.toDto(
                gestionarCarritoUseCase.actualizarLinea(usuarioActual(), productoId, request.cantidad()));
    }

    @DeleteMapping("/lineas/{productoId}")
    public CarritoDto eliminarLinea(@PathVariable Long productoId) {
        return CarritoControllerMapper.toDto(gestionarCarritoUseCase.eliminarLinea(usuarioActual(), productoId));
    }

    @DeleteMapping
    public ResponseEntity<Void> vaciar() {
        gestionarCarritoUseCase.vaciar(usuarioActual());
        return ResponseEntity.noContent().build();
    }

    private Long usuarioActual() {
        return authPublicApi.getCurrentUserId().orElseThrow(UsuarioNoAutenticadoException::new);
    }
}
