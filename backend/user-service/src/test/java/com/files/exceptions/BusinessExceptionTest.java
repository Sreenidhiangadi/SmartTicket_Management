package com.files.exceptions;

import org.junit.jupiter.api.Test;

import com.files.exception.BusinessException;

import static org.junit.jupiter.api.Assertions.*;

class BusinessExceptionTest {

    @Test
    void businessException_message() {
        BusinessException ex = new BusinessException("error");
        assertEquals("error", ex.getMessage());
    }
}
