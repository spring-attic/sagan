package org.springframework.site.indexer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.actuate.metrics.CounterService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.site.domain.documentation.DocumentationService;
import org.springframework.site.domain.documentation.Project;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DocumentationIndexService {

	private static Log logger = LogFactory.getLog(DocumentationIndexService.class);

	private static final long ONE_HOUR = 1000 * 60 * 60;
	private final DocumentationService documentationService;
	private final IndexerService indexerService;
	private final CounterService counters;

	private ExecutorService executor = Executors.newFixedThreadPool(10);

	@Autowired
	public DocumentationIndexService(IndexerService indexerService, DocumentationService documentationService, CounterService counters) {
		this.indexerService = indexerService;
		this.documentationService = documentationService;
		this.counters = counters;
	}

	@Scheduled(fixedDelay = ONE_HOUR, initialDelayString = "${search.indexer.delay:0}")
	public void indexDocumentation() {
		logger.info("Indexing project documentation");
		for (final Project project : documentationService.getProjects()) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					try {
						process(project);
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

	void process(Project project) {
		logger.info("Indexing project: " + project.getId());
		if (!project.getSupportedVersions().isEmpty()) {
			indexerService.index(new UriTemplate(project.getApiAllClassesUrl()).expand(project.getSupportedVersions().get(0)).toString(), 1);
			// TODO: support reference docs when we can work out a way to break them up into manageable pieces
		}
		indexerService.index(project.getGithubUrl(), 0);
	}


}
