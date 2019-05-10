package sagan.renderer;

import org.asciidoctor.Asciidoctor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Create the Asciidoctor engine
 */
@Configuration
public class AsciidoctorConfig {

	@Bean
	public Asciidoctor asciidoctor() {
		return Asciidoctor.Factory.create();
	}
}