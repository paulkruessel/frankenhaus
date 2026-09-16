package de.franconia.tuebingen.adh;

import org.junit.jupiter.api.Test;
import de.franconia.tuebingen.adh.auth.dto.AuthResponse;

class AdhApplicationTests {

	@Test
	void authResponseContainsDocumentedContract() {
		AuthResponse response = new AuthResponse("token", "Bearer", 900);

		org.junit.jupiter.api.Assertions.assertEquals("token", response.accessToken());
		org.junit.jupiter.api.Assertions.assertEquals("Bearer", response.tokenType());
		org.junit.jupiter.api.Assertions.assertEquals(900, response.expiresIn());
	}

}
