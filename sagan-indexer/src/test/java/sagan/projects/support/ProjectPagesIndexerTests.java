package sagan.projects.support;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import sagan.projects.Project;
import sagan.search.support.CrawlerService;
import sagan.search.support.SearchService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectPagesIndexerTests {

    private final Project springProject = new Project("spring", "Spring Project",
            "http://www.example.com/repo/spring-project",
            "http://www.example.com/spring-project", Collections.emptyList(), "release");

    private final Project springFramework = new Project("spring-framework", "Spring Framework",
            "http://www.example.com/repo/spring-framework",
            "https://projects.spring.io/spring-framework", Collections.emptyList(), "release");


    private final Project ioPlatform = new Project("spring-platform", "Spring IO Platform",
            "http://www.example.com/repo/spring-io-platform",
            "http://platform.spring.io/platform/", Collections.emptyList(), "release");
    @Mock
    private CrawlerService crawlerService;

    @Mock
    private ProjectMetadataService projectMetadataService;

    @Mock
    private SearchService searchService;

    private ProjectPagesIndexer indexer;


    private List<Project> projects = Arrays.asList(springProject, springFramework, ioPlatform);

    @Before
    public void setUp() {
        indexer = new ProjectPagesIndexer(projectMetadataService, crawlerService, searchService);
        ReflectionTestUtils.setField(indexer, "githubPagesDomains", "projects.spring.io,platform.spring.io");
    }

    @Test
    public void itReturnsProjects() {
        given(projectMetadataService.getProjectsWithReleases()).willReturn(this.projects);
        Iterable<Project> projects = indexer.indexableItems();
        assertThat(projects, iterableWithSize(3));
    }

    @Test
    public void itOnlyCrawlsMatchingDomains() {
        indexer.indexItem(springProject);
        verify(crawlerService, never()).crawl(eq(springProject.getSiteUrl()), eq(0), any());

        indexer.indexItem(springFramework);
        verify(crawlerService, times(1)).crawl(eq(springFramework.getSiteUrl()), eq(0), any());

        indexer.indexItem(ioPlatform);
        verify(crawlerService, times(1)).crawl(eq(ioPlatform.getSiteUrl()), eq(0), any());
    }

}
