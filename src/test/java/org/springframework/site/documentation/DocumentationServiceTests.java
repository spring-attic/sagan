package org.springframework.site.documentation;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.site.configuration.ApplicationConfiguration;

public class DocumentationServiceTests {

	@Test
	public void bindingToYaml() {
		DocumentationService documentationService = new DocumentationService();
		ApplicationConfiguration.bind("test-documentation.yml", documentationService);
		assertEquals(3, documentationService.getProject("spring-framework").getSupportedVersions().size());
	}

	@Test
	public void bindingToFullYaml() {
		DocumentationService documentationService = new DocumentationService();
		ApplicationConfiguration.bind("documentation.yml", documentationService);
		assertEquals(3, documentationService.getProject("spring-framework").getSupportedVersions().size());
	}

}
