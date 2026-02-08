package br.com.fiap.soat7.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleUserTest {

    @Test
    void valuesShouldContainAdminAndUser() {
        assertArrayEquals(new RoleUser[]{RoleUser.ROLE_ADMIN, RoleUser.ROLE_USER}, RoleUser.values());
        assertEquals(RoleUser.ROLE_ADMIN, RoleUser.valueOf("ROLE_ADMIN"));
    }
}
