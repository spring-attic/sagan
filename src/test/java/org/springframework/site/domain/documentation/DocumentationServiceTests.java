package org.springframework.site.domain.documentation;

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


public class DocumentationServiceTests {

	@Test
	public void bindingToYaml() throws IOException {
		InputStream yaml = new ClassPathResource("/test-documentation.yml", getClass()).getInputStream();
		DocumentationService documentationService = new DocumentationService(new DocumentationYamlParser().parse(yaml));

		assertEquals(3, documentationService.getProject("spring-framework")
				.getSupportedVersions().size());
	}

	@Test
	public void exposesProjectsForACategory() throws IOException {
		InputStream yaml = new ClassPathResource("/test-documentation.yml", getClass()).getInputStream();
		DocumentationService documentationService = new DocumentationService(new DocumentationYamlParser().parse(yaml));

		List<Project> activeProjects = documentationService.getProjectsForCategory("active");
		assertThat(activeProjects.size(), is(1));
		assertThat(activeProjects.get(0).getId(), is("spring-framework"));
	}

	@Test
	public void bindingToFullYaml() throws IOException {
		InputStream yaml = new ClassPathResource("/documentation.yml", getClass()).getInputStream();
		DocumentationService documentationService = new DocumentationService(new DocumentationYamlParser().parse(yaml));

		assertEquals(3, documentationService.getProject("spring-framework")
				.getSupportedVersions().size());
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
		InputStream yaml = new ClassPathResource("/documentation.yml", getClass()).getInputStream();
		DocumentationService documentationService = new DocumentationService(new DocumentationYamlParser().parse(yaml));

		StringBuilder builder = new StringBuilder();
		for (Project project : documentationService.getProjects()) {
			for (ProjectDocumentVersion version : project
					.getSupportedApiDocsDocumentVersions()) {
				String url = version.getUrl();
				ResponseEntity<String> entity = restTemplate.getForEntity(url,
						String.class);
				if (entity.getStatusCode() != HttpStatus.OK) {
					builder.append(project.getId() + ".invalid.apiUrl."
							+ version.getVersion() + ": " + url + "\n");
				}
			}
			for (ProjectDocumentVersion version : project
					.getSupportedReferenceDocumentVersions()) {
				String url = version.getUrl();
				ResponseEntity<String> entity = restTemplate.getForEntity(url,
						String.class);
				if (entity.getStatusCode() != HttpStatus.OK) {
					builder.append(project.getId() + ".invalid.referenceUrl."
							+ version.getVersion() + ": " + url + "\n");
				}
			}
		}
		System.err.println(builder);
		assertEquals(3, documentationService.getProject("spring-framework")
				.getSupportedVersions().size());
	}
}
