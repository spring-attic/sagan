package org.springframework.site.guides;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.actuate.metrics.CounterService;
import org.springframework.bootstrap.actuate.metrics.GaugeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.site.search.SearchService;
import org.springframework.stereotype.Service;

@Service
public class GettingStartedGuideIndexService {

	private static Log logger = LogFactory.getLog(GettingStartedGuideIndexService.class);

	private GuideSearchEntryMapper mapper = new GuideSearchEntryMapper();

	private static final long ONE_HOUR = 1000 * 60 * 60;
	private final SearchService searchService;
	private final GettingStartedService gettingStartedService;

	@Autowired
	private CounterService counters;

	@Autowired
	private GaugeService gauges;

	@Autowired
	public GettingStartedGuideIndexService(SearchService searchService, GettingStartedService gettingStartedService) {
		this.searchService = searchService;
		this.gettingStartedService = gettingStartedService;
	}

	// ten minute delay initially by default
	@Scheduled(fixedDelay = ONE_HOUR, initialDelayString = "${guides.index.delay:600000}")
	public void indexGuides() {
		logger.info("Indexing getting started guides");
		int count = 0;
		for (GuideRepo repo : gettingStartedService.listGuides()) {
			try {
				// TODO: optimize fetch of guide content (if github supports it)?
				// TODO: only index the new ones if possible
				GettingStartedGuide guide = gettingStartedService.loadGuide(repo.getGuideId());
				searchService.saveToIndex(mapper.map(guide));
				count++;
				if (gauges != null) {
					gauges.set("search.indexes.guides.processed", count);
				}
			} catch (Exception e) {
				if (counters != null) {
					counters.increment("search.indexes.guides.errors.count");

				}
				logger.warn("Unable to load getting started guide: " + repo.getGuideId() + "(" + e.getClass().getName() + ", " + e.getMessage() + ")");
				continue;
			}
		}
		if (counters != null) {
			counters.increment("search.indexes.guides.refresh.count");
		}
	}

}
