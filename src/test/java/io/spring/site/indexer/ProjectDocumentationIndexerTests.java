package io.spring.site.indexer;

import org.junit.Test;

import io.spring.site.domain.projects.Project;
import io.spring.site.domain.projects.ProjectMetadataService;
import io.spring.site.domain.projects.ProjectRelease;
import io.spring.site.indexer.CrawledWebDocumentProcessor;
import io.spring.site.indexer.ProjectDocumentationIndexer;
import io.spring.site.indexer.crawler.CrawlerService;
import io.spring.site.search.SearchService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ProjectDocumentationIndexerTests {

	private CrawlerService crawlerService = mock(CrawlerService.class);
	private ProjectMetadataService metadataService = mock(ProjectMetadataService.class);
	private final SearchService searchService = mock(SearchService.class);
	private ProjectDocumentationIndexer service = new ProjectDocumentationIndexer(crawlerService, searchService, metadataService);

	private List<ProjectRelease> documentationList = Arrays.asList(
			new ProjectRelease("3", ProjectRelease.ReleaseStatus.CURRENT, "http://reference.example.com/3", "http://api.example.com/3", "com.example", "example-framework"),
			new ProjectRelease("2", ProjectRelease.ReleaseStatus.SUPPORTED, "http://reference.example.com/2", "http://api.example.com/2", "com.example", "example-framework"),
			new ProjectRelease("1", ProjectRelease.ReleaseStatus.SUPPORTED, "http://reference.example.com/1", "http://api.example.com/1", "com.example", "example-framework")
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
