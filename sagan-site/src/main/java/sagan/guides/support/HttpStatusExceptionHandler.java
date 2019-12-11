package sagan.guides.support;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * Exception handler for exceptions that come from the sagan-renderer traverse interaction,
 * It returns the error page depending on the status code returned by the exception.
 */
@ControllerAdvice
public class HttpStatusExceptionHandler {
    @ExceptionHandler(HttpStatusCodeException.class)
    public String handleHttpStatusException(HttpStatusCodeException ex) {
        switch (ex.getStatusCode()) {
            case NOT_FOUND:
                return "error/404";
            default:
                return "error/500";
        }
    }
}
