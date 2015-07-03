package sagan;

import sagan.blog.support.PublishedBlogPostsIndexer;
import sagan.docs.support.ProjectDocumentationIndexer;
import sagan.guides.support.GettingStartedGuideIndexer;
import sagan.guides.support.TutorialIndexer;
import sagan.guides.support.UnderstandingDocIndexer;
import sagan.projects.support.ProjectPagesIndexer;
import sagan.staticpage.support.StaticPageIndexer;
import sagan.tools.support.ToolsIndexer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class IndexScheduler {
    private static final long ONE_HOUR = 1000 * 60 * 60;
    private static final long ONE_DAY = ONE_HOUR * 24;

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
    private UnderstandingDocIndexer understandingGuideIndexer;
    @Autowired
    private TutorialIndexer tutorialIndexer;
    @Autowired
    private PublishedBlogPostsIndexer publishedBlogPostsIndexer;
    @Autowired
    private ProjectPagesIndexer projectPagesIndexer;

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
    public void indexProjectPages() {
        indexerService.index(projectPagesIndexer);
    }

    @Scheduled(fixedDelay = ONE_DAY, initialDelayString = "${search.indexer.delay:0}")
    public void indexPublishedBlogPosts() {
        indexerService.index(publishedBlogPostsIndexer);
    }
}
