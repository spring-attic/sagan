package org.springframework.site.indexer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.actuate.metrics.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

@Service
public class IndexerService {

	private final ExecutorService executorService;
	private final CounterService countersService;

	private static final Log logger = LogFactory.getLog(IndexerService.class);

	@Autowired
	public IndexerService(ExecutorService executorService, CounterService countersService) {
		this.executorService = executorService;
		this.countersService = countersService;
	}

	public <T> void index(final Indexer<T> indexer) {
		for (final T indexable : indexer.indexableItems()) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					try {
						indexer.indexItem(indexable);
						countersService.increment("search.indexes." + indexer.counterName() + ".processed");
					} catch (Exception e) {
						logger.warn("Unable to load project: " + indexer.getId(indexable) + "(" + e.getClass().getName() + ", " + e.getMessage() + ")");
						countersService.increment("search.indexes." + indexer.counterName() + ".errors.count");
					}
				}
			});
		}
		countersService.increment("search.indexes." + indexer.counterName() + ".refresh.count");
	}
}