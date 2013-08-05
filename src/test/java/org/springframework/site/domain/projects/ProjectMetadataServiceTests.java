package org.springframework.site.domain.projects;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;


public class ProjectMetadataServiceTests {

	@Test
	public void bindingToYaml() throws IOException {
		InputStream yaml = new ClassPathResource("/test-project-metadata.yml", getClass()).getInputStream();
		ProjectMetadataService documentationService = new ProjectMetadataService(new ProjectMetadataYamlParser().parse(yaml));

		assertEquals(3, documentationService.getProject("spring-framework")
				.getProjectVersions().size());

		List<Project> activeProjects = documentationService.getProjectsForCategory("active");
		assertThat(activeProjects.get(0).getId(), is("spring-framework"));
	}

	@Test
	public void bindingToFullYaml() throws IOException {
		InputStream yaml = new ClassPathResource("/project-metadata.yml", getClass()).getInputStream();
		ProjectMetadataService documentationService = new ProjectMetadataService(new ProjectMetadataYamlParser().parse(yaml));

		assertEquals(3, documentationService.getProject("spring-framework")
				.getProjectVersions().size());
	}

	@Test
	@Ignore("Disabled as this test only needs to run on demand")
	public void linkTests() throws IOException {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new ResponseErrorHandler() {

			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}

			@Override
			public void handleError(ClientHttpResponse response) throws IOException {
			}
		});
		InputStream yaml = new ClassPathResource("/project-metadata.yml", getClass()).getInputStream();
		ProjectMetadataService documentationService = new ProjectMetadataService(new ProjectMetadataYamlParser().parse(yaml));

		StringBuilder builder = new StringBuilder();
		for (Project project : documentationService.getProjects()) {
			for (ProjectVersion documentation : project.getProjectVersions()) {
				String apiDocUrl = documentation.getApiDocUrl();
				ResponseEntity<String> entity = restTemplate.getForEntity(apiDocUrl,String.class);
				if (entity.getStatusCode() != HttpStatus.OK) {
					builder.append(project.getId() + ".invalid.apiUrl."
							+ documentation.getVersion() + ": " + apiDocUrl + "\n");
				}

				String refDocUrl = documentation.getRefDocUrl();
				entity = restTemplate.getForEntity(refDocUrl,
						String.class);
				if (entity.getStatusCode() != HttpStatus.OK) {
					builder.append(project.getId() + ".invalid.referenceUrl."
							+ documentation.getVersion() + ": " + refDocUrl + "\n");
				}
			}
		}
		System.err.println(builder);
		assertEquals(3, documentationService.getProject("spring-framework").getProjectVersions().size());
	}
}
