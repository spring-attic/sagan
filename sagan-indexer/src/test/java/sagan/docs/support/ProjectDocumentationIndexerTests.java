package sagan.docs.support;

import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.projects.support.ProjectMetadataService;
import sagan.search.support.CrawledWebDocumentProcessor;
import sagan.search.support.CrawlerService;
import sagan.search.support.SearchService;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static sagan.projects.ProjectRelease.ReleaseStatus.*;

public class ProjectDocumentationIndexerTests {

    private CrawlerService crawlerService = mock(CrawlerService.class);
    private ProjectMetadataService metadataService = mock(ProjectMetadataService.class);
    private final SearchService searchService = mock(SearchService.class);
    private ProjectDocumentationIndexer service = new ProjectDocumentationIndexer(crawlerService, searchService,
            metadataService);

    private List<ProjectRelease> documentationList = Arrays.asList(
            new ProjectRelease("3", GENERAL_AVAILABILITY, true, "https://reference.example.com/3",
                    "https://api.example.com/3", "com.example", "example-framework"),
            new ProjectRelease("2", SNAPSHOT, false, "https://reference.example.com/2", "https://api.example.com/2",
                    "com.example", "example-framework"),
            new ProjectRelease("1", SNAPSHOT, false, "https://reference.example.com/1", "https://api.example.com/1",
                    "com.example", "example-framework")
            );

    private Project project = new Project("spring",
            "Spring",
            "https://www.example.com/repo/spring-framework",
            "https://www.example.com/spring-framework",
            documentationList,
            "release");

    private void assertThatCrawlingIsDoneFor(String url, int linkDepthLevel) {
        verify(crawlerService).crawl(eq(url), eq(linkDepthLevel), any(CrawledWebDocumentProcessor.class));
    }

    @Test
    public void referenceDocsAreIndexed() throws Exception {
        service.indexItem(project);
        int linkDepthLevel = 1;
        assertThatCrawlingIsDoneFor("https://reference.example.com/3", linkDepthLevel);
        assertThatCrawlingIsDoneFor("https://reference.example.com/2", linkDepthLevel);
        assertThatCrawlingIsDoneFor("https://reference.example.com/1", linkDepthLevel);
    }

    @Test
    public void apiDocsAreIndexed() throws Exception {
        service.indexItem(project);
        int linkDepthLevel = 2;
        assertThatCrawlingIsDoneFor("https://api.example.com/3/allclasses-frame.html", linkDepthLevel);
        assertThatCrawlingIsDoneFor("https://api.example.com/2/allclasses-frame.html", linkDepthLevel);
        assertThatCrawlingIsDoneFor("https://api.example.com/1/allclasses-frame.html", linkDepthLevel);
    }

}
