package es.shop.pedido.apln;

import es.shop.auth.api.AuthPublicApi;
import es.shop.pedido.domain.Pedido;
import es.shop.pedido.domain.PedidoDao;
import es.shop.pedido.domain.exception.PedidoNotFoundException;
import es.shop.shared.domain.exception.UsuarioNoAutenticadoException;

import java.util.List;

public class ConsultarPedidosUseCase {
    private final AuthPublicApi authPublicApi;
    private final PedidoDao pedidoDao;

    public ConsultarPedidosUseCase(AuthPublicApi authPublicApi, PedidoDao pedidoDao) {
        this.authPublicApi = authPublicApi;
        this.pedidoDao = pedidoDao;
    }

    public List<Pedido> misPedidos() {
        return pedidoDao.findByUsuarioId(usuarioActual());
    }

    public Pedido obtener(Long id) {
        Long usuarioId = usuarioActual();
        // Un pedido ajeno se trata como inexistente para no revelar qué ids existen
        return pedidoDao.findById(id)
                .filter(pedido -> pedido.perteneceA(usuarioId))
                .orElseThrow(() -> new PedidoNotFoundException(id));
    }

    private Long usuarioActual() {
        return authPublicApi.getCurrentUserId().orElseThrow(UsuarioNoAutenticadoException::new);
    }
}
