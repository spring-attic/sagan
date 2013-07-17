package org.springframework.site.indexer;

import org.junit.Test;
import org.springframework.site.domain.documentation.DocumentationService;
import org.springframework.site.domain.documentation.Project;
import org.springframework.site.indexer.crawler.CrawlerService;
import org.springframework.site.search.SearchService;

import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ProjectDocumentationIndexerTests {

	private CrawlerService crawlerService = mock(CrawlerService.class);
	private DocumentationService documentationService = mock(DocumentationService.class);
	private final SearchService searchService = mock(SearchService.class);
	private ProjectDocumentationIndexer service = new ProjectDocumentationIndexer(crawlerService, searchService, documentationService);
	private Project project = new Project("spring", "Spring",  //
			"https://github.com/SpringSource/spring-framework", //
			"http://static.springsource.org/spring/docs/{version}/reference/", //
			"http://static.springsource.org/spring/docs/{version}/api/", //
			Arrays.asList("3.2.3.RELEASE"));

	@Test
	public void apiDocsAreIndexed() throws Exception {
		service.indexItem(project);
		int linkDepthLevel = 1;
		verify(crawlerService).crawl(contains("api"), eq(linkDepthLevel), any(CrawledWebDocumentProcessor.class));
	}

	@Test
	public void referenceDocsAreIndexed() throws Exception {
		service.indexItem(project);
		int linkDepthLevel = 1;
		verify(crawlerService).crawl(contains("reference"), eq(linkDepthLevel), any(CrawledWebDocumentProcessor.class));
	}

	@Test
	public void githubDocsAreIndexed() throws Exception {
		service.indexItem(project);
		int linkDepthLevel = 0;
		verify(crawlerService).crawl(contains("github"), eq(linkDepthLevel), any(CrawledWebDocumentProcessor.class));
	}
}
