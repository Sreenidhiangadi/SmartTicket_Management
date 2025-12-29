package com.files.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(TicketNotFoundException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleTicketNotFound(
            TicketNotFoundException ex
    ) {
        return Mono.just(
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(error(
                                HttpStatus.NOT_FOUND,
                                ex.getMessage()
                        ))
        );
    }


    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidation(
            WebExchangeBindException ex
    ) {
        Map<String, Object> body = error(
                HttpStatus.BAD_REQUEST,
                "Validation failed"
        );

        body.put(
                "errors",
                ex.getFieldErrors().stream()
                        .map(err -> err.getField() + ": " + err.getDefaultMessage())
                        .toList()
        );

        return Mono.just(
                ResponseEntity.badRequest().body(body)
        );
    }


    @ExceptionHandler(IllegalStateException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleIllegalState(
            IllegalStateException ex
    ) {
        return Mono.just(
                ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(error(
                                HttpStatus.CONFLICT,
                                ex.getMessage()
                        ))
        );
    }


    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGeneric(
            Exception ex
    ) {
        return Mono.just(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ex.getMessage()))
        );
    }


    private Map<String, Object> error(HttpStatus status, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", Instant.now());
        map.put("status", status.value());
        map.put("error", status.getReasonPhrase());
        map.put("message", message);
        return map;
    }
}
