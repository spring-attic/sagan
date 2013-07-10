package org.springframework.site.indexer;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.actuate.metrics.CounterService;
import org.springframework.site.domain.documentation.DocumentationService;
import org.springframework.site.domain.documentation.Project;

import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DocumentationIndexServiceTests {

	private IndexerService indexerService = mock(IndexerService.class);
	private DocumentationService documentationService = mock(DocumentationService.class);
	private DocumentationIndexService service = new DocumentationIndexService(indexerService, documentationService, mock(CounterService.class));
	private Project project = new Project("spring", "Spring",  //
			"https://github.com/SpringSource/spring-framework", //
			"http://static.springsource.org/spring/docs/{version}/reference/", //
			"http://static.springsource.org/spring/docs/{version}/api/", //
			Arrays.asList("3.2.3.RELEASE"));

	@Test
	public void apiDocsAreIndexed() throws Exception {
		service.process(project);
		int linkDepthLevel = 1;
		verify(indexerService).index(contains("api"), eq(linkDepthLevel));
	}

	@Test
	public void githubDocsAreIndexed() throws Exception {
		service.process(project);
		int linkDepthLevel = 0;
		verify(indexerService).index(contains("github"), eq(linkDepthLevel));
	}
}
