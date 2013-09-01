package io.spring.site.indexer.mapper;

import org.jsoup.Jsoup;

import io.spring.site.domain.guides.Guide;
import io.spring.site.search.SearchEntry;
import io.spring.site.search.SearchEntryMapper;

import java.util.Date;

public class GuideSearchEntryMapper implements SearchEntryMapper<Guide> {

    @Override
    public SearchEntry map(Guide guide) {
        SearchEntry entry = new SearchEntry();
        entry.setTitle(guide.getTitle());
        entry.setSubTitle("Getting Started Guide");

        String text = Jsoup.parse(guide.getContent()).text();

        entry.setSummary(text.substring(0, Math.min(500, text.length())));
        entry.setRawContent(text);
        entry.setPath("/guides/gs/" + guide.getGuideId() + "/");
        entry.addFacetPaths("Guides", "Guides/Getting Started");
        // TODO: Can we get a publish date form github?
        entry.setPublishAt(new Date(0L));
        return entry;
    }
}
