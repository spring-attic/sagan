package integration.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.site.domain.guides.Guide;
import org.springframework.site.domain.projects.ProjectMetadataService;
import org.springframework.site.domain.projects.ProjectMetadataYamlParser;
import org.springframework.site.web.configuration.ApplicationConfiguration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.mock;

@Configuration
@Import({ApplicationConfiguration.class})
@ComponentScan("integration.stubs")
public class IntegrationTestsConfiguration {

	public static final Guide GETTING_STARTED_GUIDE =
			new Guide("gs-awesome-guide", "awesome-guide",
					"Awesome Guide","Awesome getting started guide that isn't helpful",
					"Learn awesome stuff with this guide.", "Related resources");

	@Primary
	@Bean
	public RestTemplate mockRestTemplate() {
		return mock(RestTemplate.class);
	}

	@Primary
	@Bean
	public ProjectMetadataService projectMetadataService() throws IOException {
		InputStream yaml = new ClassPathResource("/test-project-metadata.yml", getClass()).getInputStream();
		return new ProjectMetadataYamlParser().createServiceFromYaml(yaml);
	}

}
