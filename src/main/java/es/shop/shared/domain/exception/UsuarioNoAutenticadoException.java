package es.shop.shared.domain.exception;

public class UsuarioNoAutenticadoException extends RuntimeException {
    public UsuarioNoAutenticadoException() {
        super("Usuario no autenticado");
    }
}
