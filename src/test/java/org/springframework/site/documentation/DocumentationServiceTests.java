package org.springframework.site.documentation;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.site.configuration.DocumentationConfiguration;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class DocumentationServiceTests {

	@Test
	public void bindingToYaml() {
		DocumentationService documentationService = new DocumentationService();
		DocumentationConfiguration.bind("test-documentation.yml",
				documentationService);
		assertEquals(3, documentationService.getProject("spring-framework")
				.getSupportedVersions().size());
	}

	@Test
	public void bindingToFullYaml() {
		DocumentationService documentationService = new DocumentationService();
		DocumentationConfiguration
				.bind("documentation.yml", documentationService);
		assertEquals(3, documentationService.getProject("spring-framework")
				.getSupportedVersions().size());
	}

//	@Test
	// Disabled as this test only needs to run on demand
	public void linkTests() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new ResponseErrorHandler() {

			@Override
			public boolean hasError(ClientHttpResponse response)
					throws IOException {
				return false;
			}

			@Override
			public void handleError(ClientHttpResponse response)
					throws IOException {
			}
		});
		DocumentationService documentationService = new DocumentationService();
		DocumentationConfiguration
				.bind("documentation.yml", documentationService);
		StringBuilder builder = new StringBuilder();
		for (Project project : documentationService.getProjects()) {
			for (ProjectDocumentVersion version : project
					.getSupportedApiDocsDocumentVersions()) {
				String url = version.getUrl();
				ResponseEntity<String> entity = restTemplate.getForEntity(url,
						String.class);
				if (entity.getStatusCode() != HttpStatus.OK) {
					builder.append(project.getId() + ".invalid.apiUrl." + version.getVersion()
							+ ": " + url + "\n");
				}
			}
			for (ProjectDocumentVersion version : project
					.getSupportedReferenceDocumentVersions()) {
				String url = version.getUrl();
				ResponseEntity<String> entity = restTemplate.getForEntity(url,
						String.class);
				if (entity.getStatusCode() != HttpStatus.OK) {
					builder.append(project.getId() + ".invalid.referenceUrl." + version.getVersion()
							+ ": " + url + "\n");
				}
			}
		}
		System.err.println(builder);
		assertEquals(3, documentationService.getProject("spring-framework")
				.getSupportedVersions().size());
	}

}
