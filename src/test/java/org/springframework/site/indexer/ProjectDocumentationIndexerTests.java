package org.springframework.site.indexer;

import org.junit.Test;
import org.springframework.site.domain.projects.Project;
import org.springframework.site.domain.projects.ProjectVersion;
import org.springframework.site.domain.projects.ProjectMetadataService;
import org.springframework.site.indexer.crawler.CrawlerService;
import org.springframework.site.search.SearchService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ProjectDocumentationIndexerTests {

	private CrawlerService crawlerService = mock(CrawlerService.class);
	private ProjectMetadataService documentationService = mock(ProjectMetadataService.class);
	private final SearchService searchService = mock(SearchService.class);
	private ProjectDocumentationIndexer service = new ProjectDocumentationIndexer(crawlerService, searchService, documentationService);

	private List<ProjectVersion> documentationList = Arrays.asList(
			new ProjectVersion("3", ProjectVersion.Release.CURRENT, "http://reference.example.com/3", "http://api.example.com/3"),
			new ProjectVersion("2", ProjectVersion.Release.SUPPORTED, "http://reference.example.com/2", "http://api.example.com/2"),
			new ProjectVersion("1", ProjectVersion.Release.SUPPORTED, "http://reference.example.com/1", "http://api.example.com/1")
	);

	private Project project = new Project("spring",
			"Spring",
			"http://www.example.com/repo/spring-framework",
			"http://www.example.com/spring-framework",
			documentationList);

	private void assertThatCrawlingIsDoneFor(String url, int linkDepthLevel) {
		verify(crawlerService).crawl(eq(url), eq(linkDepthLevel), any(CrawledWebDocumentProcessor.class));
	}

	@Test
	public void referenceDocsAreIndexed() throws Exception {
		service.indexItem(project);
		int linkDepthLevel = 1;
		assertThatCrawlingIsDoneFor("http://reference.example.com/3", linkDepthLevel);
		assertThatCrawlingIsDoneFor("http://reference.example.com/2", linkDepthLevel);
		assertThatCrawlingIsDoneFor("http://reference.example.com/1", linkDepthLevel);
	}

	@Test
	public void apiDocsAreIndexed() throws Exception {
		service.indexItem(project);
		int linkDepthLevel = 1;
		assertThatCrawlingIsDoneFor("http://api.example.com/3/allclasses-frame.html", linkDepthLevel);
		assertThatCrawlingIsDoneFor("http://api.example.com/2/allclasses-frame.html", linkDepthLevel);
		assertThatCrawlingIsDoneFor("http://api.example.com/1/allclasses-frame.html", linkDepthLevel);
	}

}
