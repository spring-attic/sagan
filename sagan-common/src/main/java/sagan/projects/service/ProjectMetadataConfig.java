package sagan.projects.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
class ProjectMetadataConfig {

	@Bean
	public ProjectMetadataService projectMetadataService(ProjectDataRepository repository) throws IOException {
		return new ProjectMetadataService(repository);
	}
}
