package com.files.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AssignmentAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Mono<Map<String, String>> handleAssignmentExists(AssignmentAlreadyExistsException ex) {
        return Mono.just(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedAssignmentException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<Map<String, String>> handleUnauthorized(UnauthorizedAssignmentException ex) {
        return Mono.just(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<Map<String, String>> handleGeneric(Exception ex) {
        return Mono.just(Map.of("error", "Internal server error"));
    }
}
