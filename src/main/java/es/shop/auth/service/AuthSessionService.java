package es.shop.auth.service;

import es.shop.auth.domain.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Service
public class AuthSessionService {
    private static final String ATTR_USUARIO_ID = "es.shop.auth.usuarioId";

    public void iniciarSesion(HttpSession session, Usuario usuario) {
        session.setAttribute(ATTR_USUARIO_ID, usuario.getId());
    }

    public void cerrarSesion(HttpSession session) {
        session.invalidate();
    }

    public Optional<Long> usuarioActualId() {
        HttpSession session = sesionActual();
        if (session == null) {
            return Optional.empty();
        }
        Object usuarioId = session.getAttribute(ATTR_USUARIO_ID);
        if (usuarioId instanceof Long id) {
            return Optional.of(id);
        }
        return Optional.empty();
    }

    private HttpSession sesionActual() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest().getSession(false);
        }
        return null;
    }
}
