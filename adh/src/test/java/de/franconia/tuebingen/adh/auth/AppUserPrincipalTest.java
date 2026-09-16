package de.franconia.tuebingen.adh.auth;

import de.franconia.tuebingen.adh.user.Role;
import de.franconia.tuebingen.adh.user.User;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AppUserPrincipalTest {

    @Test
    void exposesUserIdentityAuthoritiesAndDisabledState() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setEmail("person@example.com");
        user.setPasswordHash("hash");
        user.setEnabled(false);
        user.getRoles().add(Role.USER);

        AppUserPrincipal principal = new AppUserPrincipal(user);

        assertEquals(id, principal.getId());
        assertEquals("person@example.com", principal.getUsername());
        assertEquals("hash", principal.getPassword());
        assertEquals("ROLE_USER", principal.getAuthorities().iterator().next().getAuthority());
        assertFalse(principal.isEnabled());
    }
}