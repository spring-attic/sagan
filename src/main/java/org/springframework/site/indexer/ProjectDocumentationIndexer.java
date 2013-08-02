package org.springframework.site.indexer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.projects.Project;
import org.springframework.site.domain.projects.ProjectMetadataService;
import org.springframework.site.domain.projects.Version;
import org.springframework.site.indexer.crawler.CrawlerService;
import org.springframework.site.indexer.mapper.ApiDocumentMapper;
import org.springframework.site.indexer.mapper.ReferenceDocumentSearchEntryMapper;
import org.springframework.site.search.SearchService;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

@Service
public class ProjectDocumentationIndexer implements Indexer<Project> {

	private static final Log logger = LogFactory.getLog(ProjectDocumentationIndexer.class);

	private final ProjectMetadataService documentationService;
	private final CrawlerService crawlerService;
	private final SearchService searchService;

	@Autowired
	public ProjectDocumentationIndexer(CrawlerService crawlerService, SearchService searchService, ProjectMetadataService documentationService) {
		this.searchService = searchService;
		this.documentationService = documentationService;
		this.crawlerService = crawlerService;
	}

	@Override
	public Iterable<Project> indexableItems() {
		return documentationService.getProjects();
	}

	@Override
	public void indexItem(Project project) {
		logger.info("Indexing project: " + project.getId());
		for (Version version : project.getSupportedVersions()) {
			UriTemplate rawUrl = new UriTemplate(project.getApiAllClassesUrl());
			String url = rawUrl.expand(version.getFullVersion()).toString();
			crawlerService.crawl(url, 1, new CrawledWebDocumentProcessor(searchService, new ApiDocumentMapper(project, version)));

			rawUrl = new UriTemplate(project.getReferenceUrl());
			url = rawUrl.expand(version.getFullVersion()).toString();
			crawlerService.crawl(url, 1, new CrawledWebDocumentProcessor(searchService, new ReferenceDocumentSearchEntryMapper(project, version)));
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
