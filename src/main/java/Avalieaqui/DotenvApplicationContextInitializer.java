package Avalieaqui;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import io.github.cdimascio.dotenv.Dotenv;

public class DotenvApplicationContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        String profile = System.getenv("SPRING_PROFILES_ACTIVE");
        if (profile == null || !profile.equals("prod")) {
            Dotenv dotenv = Dotenv.load();
            dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
        }
    }
}
