package org.springframework.site.indexer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.actuate.metrics.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.site.domain.guides.GettingStartedGuide;
import org.springframework.site.domain.guides.GettingStartedService;
import org.springframework.site.domain.guides.GuideRepo;
import org.springframework.site.search.SearchService;
import org.springframework.site.web.guides.GuideSearchEntryMapper;
import org.springframework.stereotype.Service;

@Service
public class GettingStartedGuideIndexService {

	private static Log logger = LogFactory.getLog(GettingStartedGuideIndexService.class);

	private GuideSearchEntryMapper mapper = new GuideSearchEntryMapper();

	private static final long ONE_HOUR = 1000 * 60 * 60;
	private final SearchService searchService;
	private final GettingStartedService gettingStartedService;
	private final CounterService counters;

	@Autowired
	public GettingStartedGuideIndexService(SearchService searchService, GettingStartedService gettingStartedService, CounterService counters) {
		this.searchService = searchService;
		this.gettingStartedService = gettingStartedService;
		this.counters = counters;
	}

	@Scheduled(fixedDelay = ONE_HOUR, initialDelayString = "${search.indexer.delay:0}")
	public void indexGuides() {
		logger.info("Indexing getting started guides");
		for (GuideRepo repo : gettingStartedService.listGuides()) {
			try {
				// TODO: optimize fetch of guide content (if github supports it)?
				// TODO: only index the new ones if possible
				GettingStartedGuide guide = gettingStartedService.loadGuide(repo.getGuideId());
				searchService.saveToIndex(mapper.map(guide));
				counters.increment("search.indexes.guides.processed");
			} catch (Exception e) {
				counters.increment("search.indexes.guides.errors.count");
				logger.warn("Unable to load getting started guide: " + repo.getGuideId() + "(" + e.getClass().getName() + ", " + e.getMessage() + ")");
				continue;
			}
		}
		counters.increment("search.indexes.guides.refresh.count");
	}

}
