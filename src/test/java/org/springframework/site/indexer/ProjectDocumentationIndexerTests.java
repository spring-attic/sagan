package org.springframework.site.indexer;

import org.junit.Test;
import org.springframework.site.domain.projects.ProjectMetadataService;
import org.springframework.site.domain.projects.Project;
import org.springframework.site.domain.projects.SupportedVersions;
import org.springframework.site.indexer.crawler.CrawlerService;
import org.springframework.site.search.SearchService;

import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ProjectDocumentationIndexerTests {

	private CrawlerService crawlerService = mock(CrawlerService.class);
	private ProjectMetadataService documentationService = mock(ProjectMetadataService.class);
	private final SearchService searchService = mock(SearchService.class);
	private ProjectDocumentationIndexer service = new ProjectDocumentationIndexer(crawlerService, searchService, documentationService);

	private SupportedVersions versions = SupportedVersions.build(Arrays.asList("3.2.3.RELEASE", "3.1.4.RELEASE", "4.0.0.M1"));

	private Project project = new Project("spring", "Spring",  //
			//
			"http://static.springsource.org/spring/docs/{version}/reference/", //
			"http://static.springsource.org/spring/docs/{version}/api/", //
			versions,
			"http://www.example.com/repo/spring-framework",
			"http://www.example.com/spring-framework");

	private void assertThatCrawlingIsDoneFor(String url, int linkDepthLevel) {
		verify(crawlerService).crawl(eq(url), eq(linkDepthLevel), any(CrawledWebDocumentProcessor.class));
	}

	@Test
	public void apiDocsAreIndexed() throws Exception {
		service.indexItem(project);
		int linkDepthLevel = 1;
		assertThatCrawlingIsDoneFor("http://static.springsource.org/spring/docs/3.2.3.RELEASE/api/allclasses-frame.html", linkDepthLevel);
		assertThatCrawlingIsDoneFor("http://static.springsource.org/spring/docs/3.1.4.RELEASE/api/allclasses-frame.html", linkDepthLevel);
		assertThatCrawlingIsDoneFor("http://static.springsource.org/spring/docs/4.0.0.M1/api/allclasses-frame.html", linkDepthLevel);
	}

	@Test
	public void referenceDocsAreIndexed() throws Exception {
		service.indexItem(project);
		int linkDepthLevel = 1;
		assertThatCrawlingIsDoneFor("http://static.springsource.org/spring/docs/3.2.3.RELEASE/reference/", linkDepthLevel);
		assertThatCrawlingIsDoneFor("http://static.springsource.org/spring/docs/3.1.4.RELEASE/reference/", linkDepthLevel);
		assertThatCrawlingIsDoneFor("http://static.springsource.org/spring/docs/4.0.0.M1/reference/", linkDepthLevel);
	}

}
