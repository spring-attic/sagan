package sagan;

import sagan.blog.support.PublishedBlogPostsIndexer;
import sagan.docs.support.ProjectDocumentationIndexer;
import sagan.guides.support.GettingStartedGuideIndexer;
import sagan.guides.support.TutorialIndexer;
import sagan.guides.support.UnderstandingDocIndexer;
import sagan.projects.support.ProjectPagesIndexer;
import sagan.staticpage.support.StaticPageIndexer;
import sagan.support.SetSystemProperty;
import sagan.tools.support.ToolsIndexer;

import org.junit.ClassRule;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.Mockito.*;

@ContextConfiguration(classes = { IndexerApplication.class, IndexSchedulerTests.TestConfig.class })
public class IndexSchedulerTests extends AbstractIndexerIntegrationTests {

    /*
     * @Configuration annotation intentionally omitted so as not to interfere with
     * @ComponentScan on the src/main side of this package.
     */
    public static class TestConfig {
        @Bean
        @Primary
        public ProjectDocumentationIndexer mockProjectDocumentationIndexer() {
            return mock(ProjectDocumentationIndexer.class);
        }

        @Bean
        @Primary
        public ProjectPagesIndexer mockProjectPagesIndexer() {
            return mock(ProjectPagesIndexer.class);
        }

        @Bean
        @Primary
        public GettingStartedGuideIndexer mockGettingStartedGuideIndexer() {
            return mock(GettingStartedGuideIndexer.class);
        }

        @Bean
        @Primary
        public IndexerService mockIndexerService() {
            return mock(IndexerService.class);
        }

        @Bean
        @Primary
        public ToolsIndexer mockToolsIndexer() {
            return mock(ToolsIndexer.class);
        }

        @Bean
        @Primary
        public StaticPageIndexer mockStaticPageIndexer() {
            return mock(StaticPageIndexer.class);
        }

        @Bean
        @Primary
        public UnderstandingDocIndexer mockUnderstandingGuideIndexer() {
            return mock(UnderstandingDocIndexer.class);
        }

        @Bean
        @Primary
        public TutorialIndexer mockTutorialIndexer() {
            return mock(TutorialIndexer.class);
        }

        @Bean
        @Primary
        public PublishedBlogPostsIndexer mockPublishedBlogPostsIndexer() {
            return mock(PublishedBlogPostsIndexer.class);
        }
    }

    @Autowired
    private ProjectDocumentationIndexer projectDocumentationIndexer;
    @Autowired
    private ProjectPagesIndexer projectPagesIndexer;
    @Autowired
    private GettingStartedGuideIndexer gettingStartedGuideIndexer;
    @Autowired
    private IndexerService indexerService;
    @Autowired
    private ToolsIndexer toolsIndexer;
    @Autowired
    private StaticPageIndexer staticPageIndexer;
    @Autowired
    private UnderstandingDocIndexer understandingGuideIndexer;
    @Autowired
    private TutorialIndexer tutorialIndexer;
    @Autowired
    private PublishedBlogPostsIndexer publishedBlogPostsIndexer;

    private static final int INDEXER_DELAY = 1000;

    @ClassRule
    public static SetSystemProperty delay = new SetSystemProperty("search.indexer.delay", INDEXER_DELAY + "");

    @Test
    public void schedulerStartsTheIndexers() throws Exception {
        Thread.sleep(INDEXER_DELAY);
        verify(indexerService).index(gettingStartedGuideIndexer);
        verify(indexerService).index(projectDocumentationIndexer);
        verify(indexerService).index(projectPagesIndexer);
        verify(indexerService).index(toolsIndexer);
        verify(indexerService).index(staticPageIndexer);
        verify(indexerService).index(understandingGuideIndexer);
        verify(indexerService).index(tutorialIndexer);
        verify(indexerService).index(publishedBlogPostsIndexer);
    }

    @Test
    public void indexerServiceRespectsTheConfiguredDelay() throws Exception {
        verify(indexerService, never()).index(gettingStartedGuideIndexer);
        verify(indexerService, never()).index(projectDocumentationIndexer);
        verify(indexerService, never()).index(projectPagesIndexer);
        verify(indexerService, never()).index(toolsIndexer);
        verify(indexerService, never()).index(staticPageIndexer);
        verify(indexerService, never()).index(understandingGuideIndexer);
        verify(indexerService, never()).index(tutorialIndexer);
        verify(indexerService, never()).index(publishedBlogPostsIndexer);
    }
}
