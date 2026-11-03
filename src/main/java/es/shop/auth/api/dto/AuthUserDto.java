package es.shop.auth.api.dto;

public record AuthUserDto(
        Long id,
        String username,
        String nombre,
        String rol) {
}
