package sagan.projects.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sagan.Indexer;
import sagan.projects.Project;
import sagan.search.support.CrawledWebDocumentProcessor;
import sagan.search.support.CrawlerService;
import sagan.search.support.DocumentProcessor;
import sagan.search.support.SearchService;

@Service
public class ProjectPagesIndexer implements Indexer<Project> {

    private static final Log logger = LogFactory.getLog(ProjectPagesIndexer.class);

    @Value(value = "${search.indexer.gh_pages.domains:projects.spring.io}")
    private String githubPagesDomains;

    private final ProjectMetadataService metadataService;
    private final CrawlerService crawlerService;
    private final SearchService searchService;

    @Autowired
    public ProjectPagesIndexer(ProjectMetadataService metadataService, CrawlerService crawlerService, SearchService searchService) {
        this.metadataService = metadataService;
        this.crawlerService = crawlerService;
        this.searchService = searchService;
    }

    @Override
    public Iterable<Project> indexableItems() {
        return metadataService.getProjectsWithReleases();
    }

    @Override
    public void indexItem(Project project) {
        logger.debug("Indexing project page for: " + project.getId());
        String projectPageUrl = project.getSiteUrl();
        GithubPagesSearchEntryMapper mapper = new GithubPagesSearchEntryMapper(project);
        DocumentProcessor documentProcessor = new CrawledWebDocumentProcessor(searchService, mapper);
        if (StringUtils.commaDelimitedListToSet(githubPagesDomains).stream()
                .anyMatch(domain -> projectPageUrl.startsWith("http://" + domain) ||
                        projectPageUrl.startsWith("https://" + domain))) {
            crawlerService.crawl(projectPageUrl, 0, documentProcessor);
        }
        else {
            logger.debug(projectPageUrl + " does not match allowed domains");
        }
    }

    @Override
    public String counterName() {
        return "projects_pages";
    }

    @Override
    public String getId(Project project) {
        return project.getId();
    }
}
