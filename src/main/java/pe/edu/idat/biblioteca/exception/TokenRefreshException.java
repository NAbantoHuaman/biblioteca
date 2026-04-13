package pe.edu.idat.biblioteca.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para errores de refresh token (403).
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class TokenRefreshException extends RuntimeException {

    public TokenRefreshException(String token, String mensaje) {
        super(String.format("Error con el token [%s]: %s", token, mensaje));
    }
}
