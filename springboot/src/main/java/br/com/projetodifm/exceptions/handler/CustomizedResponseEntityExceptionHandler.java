package br.com.projetodifm.exceptions.handler;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.projetodifm.exceptions.ExceptionsResponseValidation;
import br.com.projetodifm.exceptions.ConflictException;
import br.com.projetodifm.exceptions.ExceptionsResponse;
import br.com.projetodifm.exceptions.InvalidJwtAuthenticationException;
import br.com.projetodifm.exceptions.InvalidRefreshTokenUsername;
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

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ExceptionsResponse> handleResourceNotFoundExceptions(Exception ex, WebRequest request) {

        var exceptionsResponse = new ExceptionsResponse(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(exceptionsResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidJwtAuthenticationException.class)
    public final ResponseEntity<ExceptionsResponse> handleInvalidJwtAuthenticationExceptions(Exception ex,
            WebRequest request) {

        var exceptionsResponse = new ExceptionsResponse(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(exceptionsResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidRefreshTokenUsername.class)
    public final ResponseEntity<ExceptionsResponse> handleInvalidRefreshTokenUsernameExceptions(Exception ex,
            WebRequest request) {

        var exceptionsResponse = new ExceptionsResponse(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(exceptionsResponse, HttpStatus.FORBIDDEN);
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Set<String> errors = new TreeSet<>();

        ex.getBindingResult().getAllErrors().forEach(x -> {

            String field = ((FieldError) x).getField();
            String errorMessage = x.getDefaultMessage();

            errors.add(field + ": " + errorMessage);

        });

        var exceptionsResponseValidation = new ExceptionsResponseValidation(LocalDateTime.now(), errors,
                request.getDescription(false));

        return new ResponseEntity<>(exceptionsResponseValidation, HttpStatus.BAD_REQUEST);
    }
}
