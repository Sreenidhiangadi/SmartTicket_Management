package com.files.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void role_enum_values_exist() {
        assertNotNull(Role.USER);
        assertNotNull(Role.AGENT);
        assertNotNull(Role.MANAGER);
        assertNotNull(Role.ADMIN);
    }

    @Test
    void role_valueOf_works() {
        assertEquals(Role.USER, Role.valueOf("USER"));
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
    }
}
