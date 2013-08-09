package org.springframework.site.indexer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.projects.Project;
import org.springframework.site.domain.projects.ProjectMetadataService;
import org.springframework.site.domain.projects.ProjectVersion;
import org.springframework.site.indexer.crawler.CrawlerService;
import org.springframework.site.indexer.mapper.ApiDocumentMapper;
import org.springframework.site.indexer.mapper.ReferenceDocumentSearchEntryMapper;
import org.springframework.site.search.SearchService;
import org.springframework.stereotype.Service;

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
		logger.info("Indexing project: " + project.getId());

		List<String> projectVersions = new ArrayList<>();
		for (ProjectVersion projectVersion : project.getProjectVersions()) {
			projectVersions.add(projectVersion.getFullName());
		}

		searchService.removeOldProjectEntriesFromIndex(project.getId(), projectVersions);

		for (ProjectVersion version : project.getProjectVersions()) {
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
