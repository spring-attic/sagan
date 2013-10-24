package sagan.projects.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
class ProjectMetadataConfig {

    public static final String PROJECT_METADATA_YAML = "classpath:/project-metadata.yml";

    @Value(PROJECT_METADATA_YAML)
    private Resource metadataYaml;

    @Bean
    public ProjectMetadataService projectMetadataService() throws IOException {
        return new ProjectMetadataYamlParser().parse(this.metadataYaml);
    }
}
