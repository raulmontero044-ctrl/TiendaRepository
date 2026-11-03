package es.shop.auth.config;

import es.shop.auth.apln.LoginUseCase;
import es.shop.auth.apln.LogoutUseCase;
import es.shop.auth.api.AuthPublicApi;
import es.shop.auth.api.impl.AuthPublicApiImpl;
import es.shop.auth.domain.UsuarioDao;
import es.shop.auth.service.AuthSessionService;
import es.shop.auth.service.PasswordService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LoginUseCase loginUseCase(UsuarioDao usuarioDao, PasswordService passwordService,
                                     AuthSessionService authSessionService) {
        return new LoginUseCase(usuarioDao, passwordService, authSessionService);
    }

    @Bean
    public LogoutUseCase logoutUseCase(AuthSessionService authSessionService) {
        return new LogoutUseCase(authSessionService);
    }

    @Bean
    public AuthPublicApi authPublicApi(AuthSessionService authSessionService, UsuarioDao usuarioDao) {
        return new AuthPublicApiImpl(authSessionService, usuarioDao);
    }
}
