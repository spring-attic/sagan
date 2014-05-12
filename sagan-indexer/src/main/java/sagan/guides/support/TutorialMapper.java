package sagan.guides.support;

import sagan.guides.Guide;
import sagan.search.SearchEntry;
import sagan.search.SearchEntryMapper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.jsoup.Jsoup;

class TutorialMapper implements SearchEntryMapper<Guide> {

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
        entry.setPublishAt(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC));
        return entry;
    }
}
