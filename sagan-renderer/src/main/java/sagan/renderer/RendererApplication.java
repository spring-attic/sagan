package sagan.renderer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main configuration resource for the Sagan markdown service.
 */
@SpringBootApplication
public class RendererApplication {

    public static void main(String[] args) {
        new SpringApplication(RendererApplication.class).run(args);
    }

}
