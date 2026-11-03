package es.shop.auth.apln;

import es.shop.auth.service.AuthSessionService;
import jakarta.servlet.http.HttpSession;

public class LogoutUseCase {
    private final AuthSessionService authSessionService;

    public LogoutUseCase(AuthSessionService authSessionService) {
        this.authSessionService = authSessionService;
    }

    public void logout(HttpSession session) {
        authSessionService.cerrarSesion(session);
    }
}
