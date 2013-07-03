package org.springframework.site.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.site.guides.GettingStartedGuide;
import org.springframework.site.guides.GettingStartedService;
import org.springframework.site.guides.GuideRepo;
import org.springframework.stereotype.Service;

@Service
public class GettingStartedGuideIndexService {

	private static Log logger = LogFactory.getLog(GettingStartedGuideIndexService.class);

	private static final long TEN_MINUTES = 1000 * 60 * 10;
	private final SearchService searchService;
	private final GettingStartedService gettingStartedService;

	@Autowired
	public GettingStartedGuideIndexService(SearchService searchService, GettingStartedService gettingStartedService) {
		this.searchService = searchService;
		this.gettingStartedService = gettingStartedService;
	}

	@Scheduled(fixedDelay = TEN_MINUTES, initialDelay = 0L)
	public void indexGuides() {
		logger.info("Indexing getting started guides");
		for (GuideRepo repo : gettingStartedService.listGuides()) {
			// TODO: optimize fetch of guide content (if github supports it)?
			// TODO: only index the new ones if possible
			GettingStartedGuide guide = gettingStartedService.loadGuide(repo.getGuideId());
			searchService.saveGuideToSearchIndex(guide);
		}
	}
}
