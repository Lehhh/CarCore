package br.com.fiap.soat7.data.dto.user;

import br.com.fiap.soat7.data.RoleUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleUpdateRequestTest {

    @Test
    void recordShouldExposeRole() {
        UserRoleUpdateRequest req = new UserRoleUpdateRequest(RoleUser.ROLE_ADMIN);
        assertEquals(RoleUser.ROLE_ADMIN, req.role());
    }
}
