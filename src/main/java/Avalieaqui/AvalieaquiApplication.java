package Avalieaqui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class AvalieaquiApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(AvalieaquiApplication.class);
		app.addInitializers(new DotenvApplicationContextInitializer());
		app.run(args);
	}
}
