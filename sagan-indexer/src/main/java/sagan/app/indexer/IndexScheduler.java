package sagan.app.indexer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sagan.blog.search.PublishedBlogPostsIndexer;
import sagan.docs.search.ProjectDocumentationIndexer;
import sagan.guides.search.GettingStartedGuideIndexer;
import sagan.guides.search.TutorialIndexer;
import sagan.guides.search.UnderstandingGuideIndexer;
import sagan.staticpage.search.StaticPageIndexer;
import sagan.tools.search.ToolsIndexer;

@SuppressWarnings("unused")
@Component
public class IndexScheduler {
    private static final long ONE_HOUR = 1000 * 60 * 60;
    private static final long ONE_DAY = ONE_HOUR * 24;

    private static final Log logger = LogFactory.getLog(IndexScheduler.class);

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

    @Scheduled(fixedDelay = ONE_HOUR, initialDelayString = "${search.indexer.delay:0}")
    public void indexGettingStartedGuides() {
        indexerService.index(gettingStartedGuideIndexer);
    }

    @Scheduled(fixedDelay = ONE_HOUR, initialDelayString = "${search.indexer.delay:0}")
    public void indexUnderstandingGuides() {
        indexerService.index(understandingGuideIndexer);
    }

    @Scheduled(fixedDelay = ONE_HOUR, initialDelayString = "${search.indexer.delay:0}")
    public void indexTutorials() {
        indexerService.index(tutorialIndexer);
    }

    @Scheduled(fixedDelay = ONE_DAY, initialDelayString = "${search.indexer.delay:0}")
    public void indexTools() {
        indexerService.index(toolsIndexer);
    }

    @Scheduled(fixedDelay = ONE_DAY, initialDelayString = "${search.indexer.delay:0}")
    public void indexStaticPages() {
        indexerService.index(staticPageIndexer);
    }

    @Scheduled(fixedDelay = ONE_DAY, initialDelayString = "${search.indexer.delay:0}")
    public void indexProjectDocumentation() {
        indexerService.index(projectDocumentationIndexer);
    }

    @Scheduled(fixedDelay = ONE_DAY, initialDelayString = "${search.indexer.delay:0}")
    public void setPublishedBlogPosts() {
        indexerService.index(publishedBlogPostsIndexer);
    }
}
