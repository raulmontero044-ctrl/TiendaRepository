package es.shop.auth.api;

import es.shop.auth.api.dto.AuthUserDto;

import java.util.Optional;

public interface AuthPublicApi {
    Optional<Long> getCurrentUserId();

    Optional<AuthUserDto> getCurrentUser();

    boolean isAuthenticated();
}
