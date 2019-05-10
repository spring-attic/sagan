package sagan.renderer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application that renders lightweight markup languages
 * and Spring guides content into HTML
 */
@SpringBootApplication
public class RendererApplication {

	public static void main(String[] args) {
		SpringApplication.run(RendererApplication.class, args);
	}
}
