package br.com.projetodifm.exceptions.handler;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.projetodifm.exceptions.ConflictException;
import br.com.projetodifm.exceptions.ExceptionsResponse;
import br.com.projetodifm.exceptions.InvalidJwtAuthenticationException;
import br.com.projetodifm.exceptions.InvalidRefreshTokenUsername;
import br.com.projetodifm.exceptions.RequiredObjectIsNullException;
import br.com.projetodifm.exceptions.ResourceNotFoundException;

@RestController
@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionsResponse> handleAllExceptions(Exception ex, WebRequest request) {

        var exceptionsResponse = new ExceptionsResponse(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(exceptionsResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConflictException.class)
    public final ResponseEntity<ExceptionsResponse> handleConflictExceptions(Exception ex, WebRequest request) {

        var exceptionsResponse = new ExceptionsResponse(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(exceptionsResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RequiredObjectIsNullException.class)
    public final ResponseEntity<ExceptionsResponse> handleRequiredObjectsNullExceptions(Exception ex,
            WebRequest request) {

        var exceptionsResponse = new ExceptionsResponse(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(exceptionsResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ExceptionsResponse> handleResourceNotFoundExceptions(Exception ex, WebRequest request) {

        var exceptionsResponse = new ExceptionsResponse(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(exceptionsResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidJwtAuthenticationException.class)
    public final ResponseEntity<ExceptionsResponse> handleInvalidJwtAuthenticationExceptions(Exception ex, WebRequest request) {

        var exceptionsResponse = new ExceptionsResponse(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(exceptionsResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidRefreshTokenUsername.class)
    public final ResponseEntity<ExceptionsResponse> handleInvalidRefreshTokenUsernameExceptions(Exception ex, WebRequest request) {

        var exceptionsResponse = new ExceptionsResponse(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(exceptionsResponse, HttpStatus.FORBIDDEN);
    }
}
