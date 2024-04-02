package Avalieaqui;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
class AvalieaquiApplicationTests {

	@DynamicPropertySource
	static void properties(DynamicPropertyRegistry registry) {
		Dotenv dotenv = Dotenv.load();
		dotenv.entries().forEach(e -> registry.add(e.getKey(), e::getValue));
	}

	@Test
	void contextLoads() {
	}

}
