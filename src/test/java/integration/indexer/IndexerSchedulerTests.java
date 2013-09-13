package integration.indexer;

import io.spring.site.indexer.GettingStartedGuideIndexer;
import io.spring.site.indexer.IndexerService;
import io.spring.site.indexer.ProjectDocumentationIndexer;
import io.spring.site.indexer.StaticPageIndexer;
import io.spring.site.indexer.ToolsIndexer;
import io.spring.site.indexer.TutorialIndexer;
import io.spring.site.indexer.UnderstandingGuideIndexer;
import io.spring.site.indexer.configuration.IndexerConfiguration;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import utils.SetSystemProperty;

import static integration.indexer.IndexerSchedulerTests.TestConfiguration;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestConfiguration.class },
        initializers = ConfigFileApplicationContextInitializer.class)
public class IndexerSchedulerTests {

    @Configuration
    @Import({IndexerConfiguration.class})
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
    }

    @Test
    public void indexerServiceRespectsTheConfiguredDelay() throws Exception {
        verify(indexerService, never()).index(gettingStartedGuideIndexer);
        verify(indexerService, never()).index(projectDocumentationIndexer);
        verify(indexerService, never()).index(toolsIndexer);
        verify(indexerService, never()).index(staticPageIndexer);
        verify(indexerService, never()).index(understandingGuideIndexer);
        verify(indexerService, never()).index(tutorialIndexer);
    }
}
