package sagan.renderer;

import org.asciidoctor.Asciidoctor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * Lazily create the Asciidoctor engine
 */
@Configuration
public class AsciidoctorConfig {

	@Bean
	@Lazy
	@Scope(proxyMode = ScopedProxyMode.INTERFACES)
	public Asciidoctor asciidoctor() {
		return Asciidoctor.Factory.create();
	}
}