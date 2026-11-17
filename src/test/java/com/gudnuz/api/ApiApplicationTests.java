package com.gudnuz.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

// Test de contexte : vérifie que toute l'application Spring démarre (tous les beans se construisent).
// @SpringBootTest ne remplace PAS la datasource (contrairement à @DataJpaTest) → on surcharge
// nous-mêmes la config pour ne dépendre ni d'un PostgreSQL local ni de variables d'environnement.
@SpringBootTest
@TestPropertySource(properties = {
		// Base H2 en mémoire, créée au démarrage du test et détruite à la fin
		"spring.datasource.url=jdbc:h2:mem:testdb",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		// application.yml force le dialecte PostgreSQL → on le remplace par celui de H2
		"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
		// Clé JWT bidon (Base64, ≥ 256 bits) : JWT_SECRET_KEY n'a pas de valeur par défaut
		"application.security.jwt.secret-key=Y2xlLWRlLXRlc3QtYmlkb24tcG91ci1hcGktYXBwbGljYXRpb24tdGVzdHMtdW5pcXVlbWVudA=="
})
class ApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
