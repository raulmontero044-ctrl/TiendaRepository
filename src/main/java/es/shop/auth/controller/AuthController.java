package es.shop.auth.controller;

import es.shop.auth.apln.LoginUseCase;
import es.shop.auth.apln.LogoutUseCase;
import es.shop.auth.api.AuthPublicApi;
import es.shop.auth.api.dto.AuthUserDto;
import es.shop.auth.controller.dto.LoginRequest;
import es.shop.auth.domain.Usuario;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final AuthPublicApi authPublicApi;

    public AuthController(LoginUseCase loginUseCase, LogoutUseCase logoutUseCase, AuthPublicApi authPublicApi) {
        this.loginUseCase = loginUseCase;
        this.logoutUseCase = logoutUseCase;
        this.authPublicApi = authPublicApi;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthUserDto> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        return loginUseCase.login(request.username(), request.password(), session)
                .map(usuario -> ResponseEntity.ok(toDto(usuario)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        logoutUseCase.logout(session);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthUserDto> me() {
        return authPublicApi.getCurrentUser()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    private AuthUserDto toDto(Usuario usuario) {
        return new AuthUserDto(usuario.getId(), usuario.getUsername(), usuario.getNombre(), usuario.getRol());
    }
}
