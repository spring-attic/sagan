package sagan.guides.search;

import sagan.guides.Guide;
import sagan.search.SearchEntry;
import sagan.search.SearchEntryMapper;

import java.util.Date;

import org.jsoup.Jsoup;

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
        entry.setPublishAt(new Date(0L));
        return entry;
    }
}
