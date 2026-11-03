package es.shop.auth.api.impl;

import es.shop.auth.api.AuthPublicApi;
import es.shop.auth.api.dto.AuthUserDto;
import es.shop.auth.domain.Usuario;
import es.shop.auth.domain.UsuarioDao;
import es.shop.auth.service.AuthSessionService;

import java.util.Optional;

public class AuthPublicApiImpl implements AuthPublicApi {
    private final AuthSessionService authSessionService;
    private final UsuarioDao usuarioDao;

    public AuthPublicApiImpl(AuthSessionService authSessionService, UsuarioDao usuarioDao) {
        this.authSessionService = authSessionService;
        this.usuarioDao = usuarioDao;
    }

    @Override
    public Optional<Long> getCurrentUserId() {
        return authSessionService.usuarioActualId();
    }

    @Override
    public Optional<AuthUserDto> getCurrentUser() {
        return getCurrentUserId()
                .flatMap(usuarioDao::findById)
                .filter(Usuario::isActivo)
                .map(this::toDto);
    }

    @Override
    public boolean isAuthenticated() {
        return getCurrentUserId().isPresent();
    }

    private AuthUserDto toDto(Usuario usuario) {
        return new AuthUserDto(usuario.getId(), usuario.getUsername(), usuario.getNombre(), usuario.getRol());
    }
}
