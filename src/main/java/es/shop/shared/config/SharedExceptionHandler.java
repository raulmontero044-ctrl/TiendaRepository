package es.shop.shared.config;

import es.shop.shared.domain.exception.UsuarioNoAutenticadoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SharedExceptionHandler {
    @ExceptionHandler(UsuarioNoAutenticadoException.class)
    public ProblemDetail handleNoAutenticado(UsuarioNoAutenticadoException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
    }
}
