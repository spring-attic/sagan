package integration.indexer;

import integration.IndexerIntegrationTestBase;
import sagan.guides.service.index.GettingStartedGuideIndexer;
import sagan.app.indexer.IndexerService;
import sagan.docs.service.index.ProjectDocumentationIndexer;
import sagan.blog.service.index.PublishedBlogPostsIndexer;
import sagan.staticpage.service.index.StaticPageIndexer;
import sagan.tools.service.index.ToolsIndexer;
import sagan.guides.service.index.TutorialIndexer;
import sagan.guides.service.index.UnderstandingGuideIndexer;
import sagan.app.indexer.config.ApplicationConfiguration;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import sagan.util.SetSystemProperty;


import static org.mockito.Mockito.*;
import static integration.indexer.IndexerSchedulerTests.TestConfiguration;

@ContextConfiguration(classes = { TestConfiguration.class })
public class IndexerSchedulerTests extends IndexerIntegrationTestBase {

    @Configuration
    @Import({ApplicationConfiguration.class})
    public static class TestConfiguration {
        @Bean
        @Primary
        public ProjectDocumentationIndexer mockProjectDocumentationIndexer() {
            return mock(ProjectDocumentationIndexer.class);
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
        public UnderstandingGuideIndexer mockUnderstandingGuideIndexer() {
            return mock(UnderstandingGuideIndexer.class);
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
    private GettingStartedGuideIndexer gettingStartedGuideIndexer;
    @Autowired
    private IndexerService indexerService;
    @Autowired
    private ToolsIndexer toolsIndexer;
    @Autowired
    private StaticPageIndexer staticPageIndexer;
    @Autowired
    private UnderstandingGuideIndexer understandingGuideIndexer;
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
        verify(indexerService, never()).index(toolsIndexer);
        verify(indexerService, never()).index(staticPageIndexer);
        verify(indexerService, never()).index(understandingGuideIndexer);
        verify(indexerService, never()).index(tutorialIndexer);
        verify(indexerService, never()).index(publishedBlogPostsIndexer);
    }
}
