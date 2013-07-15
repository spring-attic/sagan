package org.springframework.site.indexer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.actuate.metrics.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.site.domain.documentation.DocumentationService;
import org.springframework.site.domain.documentation.Project;
import org.springframework.site.indexer.crawler.CrawlerService;
import org.springframework.site.indexer.crawler.DocumentProcessor;
import org.springframework.site.search.SearchService;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DocumentationIndexService {

	private static Log logger = LogFactory.getLog(DocumentationIndexService.class);

	private static final long ONE_HOUR = 1000 * 60 * 60;
	private final DocumentationService documentationService;
	private final CrawlerService crawlerService;
	private final CounterService counters;

	private ExecutorService executor = Executors.newFixedThreadPool(10);

	private final DocumentProcessor documentProcessor;
	private final DocumentProcessor apiProcessor;

	@Autowired
	public DocumentationIndexService(CrawlerService crawlerService, DocumentationService documentationService, CounterService counters, SearchService searchService) {
		this.crawlerService = crawlerService;
		this.documentationService = documentationService;
		this.counters = counters;
		this.documentProcessor = new CrawledWebDocumentProcessor(searchService, new WebDocumentSearchEntryMapper());
		this.apiProcessor = new CrawledWebDocumentProcessor(searchService, new ApiDocumentMapper());
	}

	@Scheduled(fixedDelay = ONE_HOUR, initialDelayString = "${search.indexer.delay:0}")
	public void indexDocumentation() {
		logger.info("Indexing project documentation");
		for (final Project project : documentationService.getProjects()) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					try {
						index(project);
						counters.increment("search.indexes.projects.processed");
					} catch (Exception e) {
						logger.warn("Unable to load project: " + project.getId() + "(" + e.getClass().getName() + ", " + e.getMessage() + ")");
						counters.increment("search.indexes.projects.errors.count");
					}
				}
			});
		}
		counters.increment("search.indexes.projects.refresh.count");
	}

	void index(Project project) {
		logger.info("Indexing project: " + project.getId());
		if (!project.getSupportedVersions().isEmpty()) {
			String currentVersion = project.getSupportedVersions().get(0);

			UriTemplate rawUrl = new UriTemplate(project.getApiAllClassesUrl());
			String url = rawUrl.expand(currentVersion).toString();
			crawlerService.crawl(url, 1, apiProcessor);

			rawUrl = new UriTemplate(project.getReferenceUrl());
			url = rawUrl.expand(currentVersion).toString();
			crawlerService.crawl(url, 1, documentProcessor);
		}
		crawlerService.crawl(project.getGithubUrl(), 0, documentProcessor);
	}


}
