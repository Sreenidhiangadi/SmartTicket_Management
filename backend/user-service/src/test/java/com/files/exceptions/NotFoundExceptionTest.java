package com.files.exceptions;

import org.junit.jupiter.api.Test;

import com.files.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class NotFoundExceptionTest {

    @Test
    void notFoundException_message() {
        NotFoundException ex = new NotFoundException("not found");
        assertEquals("not found", ex.getMessage());
    }
}
