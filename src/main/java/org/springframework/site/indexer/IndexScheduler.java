package org.springframework.site.indexer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class IndexScheduler {
	private static final long ONE_HOUR = 1000 * 60 * 60;
	private static final long ONE_DAY = ONE_HOUR * 24;

	private static final Log logger = LogFactory.getLog(IndexScheduler.class);

	private final ProjectDocumentationIndexer projectDocumentationIndexer;
	private final GettingStartedGuideIndexer gettingStartedGuideIndexer;
	private final IndexerService indexerService;
	private final ToolsIndexer toolsIndexer;

	@Autowired
	public IndexScheduler(IndexerService indexerService, ProjectDocumentationIndexer projectDocumentationIndexer, GettingStartedGuideIndexer gettingStartedGuideIndexer, ToolsIndexer toolsIndexer) {
		this.indexerService = indexerService;
		this.projectDocumentationIndexer = projectDocumentationIndexer;
		this.gettingStartedGuideIndexer = gettingStartedGuideIndexer;
		this.toolsIndexer = toolsIndexer;
	}

	@Scheduled(fixedDelay = ONE_DAY, initialDelayString = "${search.indexer.delay:0}")
	public void indexProjectDocumentation() {
		logger.info("Indexing project documentation");
		indexerService.index(projectDocumentationIndexer);
	}


	@Scheduled(fixedDelay = ONE_HOUR, initialDelayString = "${search.indexer.delay:0}")
	public void indexGuides() {
		logger.info("Indexing getting started guides");
		indexerService.index(gettingStartedGuideIndexer);
	}

	@Scheduled(fixedDelay = ONE_DAY, initialDelayString = "${search.indexer.delay:0}")
	public void indexTools() {
		logger.info("Indexing tools");
		indexerService.index(toolsIndexer);
	}
}
