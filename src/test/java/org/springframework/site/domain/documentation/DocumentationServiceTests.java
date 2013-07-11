package org.springframework.site.domain.documentation;

import java.io.IOException;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.bootstrap.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

@Configuration
@EnableConfigurationProperties
public class DocumentationServiceTests {

	private ConfigurableApplicationContext context;

	@After
	public void close() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void bindingToYaml() {
		DocumentationService documentationService = getService("classpath:test-documentation.yml");
		assertEquals(3, documentationService.getProject("spring-framework")
				.getSupportedVersions().size());
	}

	@Test
	public void bindingToFullYaml() {
		DocumentationService documentationService = getService("classpath:documentation.yml");
		assertEquals(3, documentationService.getProject("spring-framework")
				.getSupportedVersions().size());
	}

	@Test
	@Ignore("Disabled as this test only needs to run on demand")
	public void linkTests() {
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
		DocumentationService documentationService = getService("classpath:documentation.yml");
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

	private DocumentationService getService(String path) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(DocumentationService.class, DocumentationServiceTests.class);
		context.refresh();
		this.context = context;
		return context.getBean(DocumentationService.class);
	}

}
