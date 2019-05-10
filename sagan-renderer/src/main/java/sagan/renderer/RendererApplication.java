package sagan.renderer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Application that renders lightweight markup languages
 * and Spring guides content into HTML
 */
@SpringBootApplication
@EnableConfigurationProperties(RendererProperties.class)
public class RendererApplication {

	public static void main(String[] args) {
		SpringApplication.run(RendererApplication.class, args);
	}
}
