package com.files.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.files.exception.BusinessException;
import com.files.exception.GlobalExceptionHandler;
import com.files.exception.NotFoundException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBusinessException() {
        BusinessException ex = new BusinessException("business error");

        Mono<ResponseEntity<Map<String, Object>>> result =
                handler.handleBusiness(ex);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    assertEquals("business error", response.getBody().get("message"));
                })
                .verifyComplete();
    }

    @Test
    void handleNotFoundException() {
        NotFoundException ex = new NotFoundException("not found");

        Mono<ResponseEntity<Map<String, Object>>> result =
                handler.handleNotFound(ex);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                    assertEquals("not found", response.getBody().get("message"));
                })
                .verifyComplete();
    }

    @Test
    void handleValidationException() {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "object");

        bindingResult.addError(
                new FieldError("object", "email", "must not be blank")
        );
        bindingResult.addError(
                new FieldError("object", "password", "must not be blank")
        );

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult) {};

        Mono<ResponseEntity<Map<String, String>>> result =
                handler.handleValidation(ex);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    assertEquals("must not be blank", response.getBody().get("email"));
                    assertEquals("must not be blank", response.getBody().get("password"));
                })
                .verifyComplete();
    }
}
