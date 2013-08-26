package io.spring.site.indexer.mapper;

import org.jsoup.Jsoup;

import io.spring.site.domain.guides.Guide;
import io.spring.site.search.SearchEntry;
import io.spring.site.search.SearchEntryMapper;

import java.util.Date;

public class TutorialMapper implements SearchEntryMapper<Guide> {

	@Override
	public SearchEntry map(Guide tutorial) {
		SearchEntry entry = new SearchEntry();
		entry.setTitle(tutorial.getTitle());
		entry.setSubTitle("Tutorial");

		String text = Jsoup.parse(tutorial.getContent()).text();

		entry.setSummary(text.substring(0, Math.min(500, text.length())));
		entry.setRawContent(text);
		entry.setPath("/guides/tutorials/" + tutorial.getGuideId() + "/");
		entry.addFacetPaths("Guides", "Guides/Tutorials");
		entry.setPublishAt(new Date(0L));
		return entry;
	}
}
