package es.shop.auth.apln;

import es.shop.auth.domain.Usuario;
import es.shop.auth.domain.UsuarioDao;
import es.shop.auth.service.AuthSessionService;
import es.shop.auth.service.PasswordService;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

public class LoginUseCase {
    private final UsuarioDao usuarioDao;
    private final PasswordService passwordService;
    private final AuthSessionService authSessionService;

    public LoginUseCase(UsuarioDao usuarioDao, PasswordService passwordService, AuthSessionService authSessionService) {
        this.usuarioDao = usuarioDao;
        this.passwordService = passwordService;
        this.authSessionService = authSessionService;
    }

    public Optional<Usuario> login(String username, String password, HttpSession session) {
        return usuarioDao.findByUsername(username)
                .filter(Usuario::isActivo)
                .filter(usuario -> passwordService.matches(password, usuario.getPasswordHash()))
                .map(usuario -> {
                    authSessionService.iniciarSesion(session, usuario);
                    return usuario;
                });
    }
}
