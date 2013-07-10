package org.springframework.site.web.guides;

import org.springframework.site.domain.guides.GettingStartedGuide;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchEntryMapper;

import java.util.Date;

public class GuideSearchEntryMapper implements SearchEntryMapper<GettingStartedGuide> {

	@Override
	public SearchEntry map(GettingStartedGuide guide) {
		SearchEntry entry = new SearchEntry();
		entry.setTitle(guide.getGuideId());
		// TODO: summary should be generated after a search matches, not statically here (so this is purely a hack for now)
		entry.setSummary(guide.getContent().substring(0, Math.min(500, guide.getContent().length())));
		entry.setRawContent(guide.getContent());
		entry.setPath(GettingStartedController.getPath(guide));
		// TODO: Can we get a publish date form github?
		entry.setPublishAt(new Date(0L));
		return entry;
	}
}
