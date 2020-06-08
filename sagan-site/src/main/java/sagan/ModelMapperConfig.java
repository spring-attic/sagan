package sagan;

import org.modelmapper.ModelMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This configuration registers a {@link ModelMapper} bean with the application.
 * We're using using this library to map domain model classes to/from DTOs.
 */
@Configuration
public class ModelMapperConfig {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
