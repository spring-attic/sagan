package sagan.projects.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class ProjectMetadataConfig {

    public static final String PROJECT_METADATA_YML = "classpath:/project-metadata.yml";

    @Value(PROJECT_METADATA_YML)
    private Resource metadataYaml;

    @Bean
    public ProjectMetadataService projectMetadataService() throws IOException {
        return new ProjectMetadataYamlParser().parseMetadataYaml(this.metadataYaml);
    }

}
