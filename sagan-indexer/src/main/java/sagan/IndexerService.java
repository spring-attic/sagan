package sagan;

import java.util.concurrent.ExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.stereotype.Service;

@Service
class IndexerService {

    private final ExecutorService executorService;
    private final CounterService countersService;

    private static final Log logger = LogFactory.getLog(IndexerService.class);

    @Autowired
    public IndexerService(ExecutorService executorService, CounterService countersService) {
        this.executorService = executorService;
        this.countersService = countersService;
    }

    public <T> void index(final Indexer<T> indexer) {
        logger.debug("Indexing " + indexer.counterName());
        for (final T indexable : indexer.indexableItems()) {
            executorService.submit(() -> {
                try {
                    indexer.indexItem(indexable);
                    countersService.increment("search.indexes." + indexer.counterName() + ".processed");
                } catch (Exception e) {
                    String message =
                            String.format("Unable to index an entry of '%s' with id: '%s' -> (%s, %s)", indexer
                                    .counterName(), indexer.getId(indexable), e.getClass().getName(), e
                                    .getMessage());

                    logger.warn(message);
                    countersService.increment("search.indexes." + indexer.counterName() + ".errors.count");
                }
            });
        }
        countersService.increment("search.indexes." + indexer.counterName() + ".refresh.count");
    }
}
