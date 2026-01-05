package com.files.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleTicketNotFound() {
        TicketNotFoundException ex =
                new TicketNotFoundException("T-1");

        Mono<ResponseEntity<Map<String, Object>>> mono =
                handler.handleTicketNotFound(ex);

        StepVerifier.create(mono)
                .assertNext(resp -> {
                    assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
                    assertEquals(404, resp.getBody().get("status"));
                    assertEquals("Ticket not found with id: T-1", resp.getBody().get("message"));
                })
                .verifyComplete();
    }

    @Test
    void handleValidation() {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "request");

        bindingResult.addError(
                new FieldError("request", "title", "must not be blank")
        );
        bindingResult.addError(
                new FieldError("request", "priority", "must not be null")
        );

        WebExchangeBindException ex =
                new WebExchangeBindException(null, bindingResult);

        Mono<ResponseEntity<Map<String, Object>>> mono =
                handler.handleValidation(ex);

        StepVerifier.create(mono)
                .assertNext(resp -> {
                    assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
                    assertEquals("Validation failed", resp.getBody().get("message"));
                    assertTrue(resp.getBody().containsKey("errors"));
                })
                .verifyComplete();
    }

    @Test
    void handleIllegalState() {
        IllegalStateException ex =
                new IllegalStateException("Invalid transition");

        Mono<ResponseEntity<Map<String, Object>>> mono =
                handler.handleIllegalState(ex);

        StepVerifier.create(mono)
                .assertNext(resp -> {
                    assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
                    assertEquals("Invalid transition", resp.getBody().get("message"));
                })
                .verifyComplete();
    }

    @Test
    void handleGenericException() {
        Exception ex = new Exception("Something broke");

        Mono<ResponseEntity<Map<String, Object>>> mono =
                handler.handleGeneric(ex);

        StepVerifier.create(mono)
                .assertNext(resp -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
                    assertEquals("Something broke", resp.getBody().get("error"));
                })
                .verifyComplete();
    }
}
