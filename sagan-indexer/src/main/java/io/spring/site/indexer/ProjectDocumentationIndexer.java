package io.spring.site.indexer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sagan.projects.Project;
import sagan.projects.service.ProjectMetadataService;
import sagan.projects.ProjectRelease;
import io.spring.site.indexer.crawler.CrawlerService;
import io.spring.site.indexer.mapper.ApiDocumentMapper;
import io.spring.site.indexer.mapper.ReferenceDocumentSearchEntryMapper;
import sagan.search.service.SearchService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectDocumentationIndexer implements Indexer<Project> {

    private static final Log logger = LogFactory.getLog(ProjectDocumentationIndexer.class);

    private final ProjectMetadataService metadataService;
    private final CrawlerService crawlerService;
    private final SearchService searchService;

    @Autowired
    public ProjectDocumentationIndexer(CrawlerService crawlerService, SearchService searchService, ProjectMetadataService metadataService) {
        this.searchService = searchService;
        this.metadataService = metadataService;
        this.crawlerService = crawlerService;
    }

    @Override
    public Iterable<Project> indexableItems() {
        return metadataService.getProjects();
    }

    @Override
    public void indexItem(Project project) {
        logger.debug("Indexing project: " + project.getId());

        List<String> projectVersions = new ArrayList<>();
        for (ProjectRelease projectRelease : project.getProjectReleases()) {
            projectVersions.add(projectRelease.getVersion());
        }

        searchService.removeOldProjectEntriesFromIndex(project.getId(), projectVersions);

        for (ProjectRelease version : project.getProjectReleases()) {
            String apiDocUrl = version.getApiDocUrl() + "/allclasses-frame.html";
            ApiDocumentMapper apiDocumentMapper = new ApiDocumentMapper(project, version);
            CrawledWebDocumentProcessor apiDocProcessor = new CrawledWebDocumentProcessor(searchService, apiDocumentMapper);
            crawlerService.crawl(apiDocUrl, 1, apiDocProcessor);

            String refDocUrl = version.getRefDocUrl();
            ReferenceDocumentSearchEntryMapper documentMapper = new ReferenceDocumentSearchEntryMapper(project, version);
            CrawledWebDocumentProcessor refDocProcessor = new CrawledWebDocumentProcessor(searchService, documentMapper);
            crawlerService.crawl(refDocUrl, 1, refDocProcessor);
        }
    }

    @Override
    public String counterName() {
        return "projects";
    }

    @Override
    public String getId(Project project) {
        return project.getId();
    }
}
