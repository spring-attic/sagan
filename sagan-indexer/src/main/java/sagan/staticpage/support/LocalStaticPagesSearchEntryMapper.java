package sagan.staticpage.support;

import sagan.search.SearchEntry;
import sagan.search.SearchEntryMapper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.jsoup.nodes.Document;

public class LocalStaticPagesSearchEntryMapper implements SearchEntryMapper<Document> {

    @Override
    public SearchEntry map(Document document) {
        SearchEntry entry = new SearchEntry();
        entry.setPublishAt(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC));
        String text = document.getElementsByClass("body--container").text();
        entry.setRawContent(text);
        entry.setSummary(text.substring(0, Math.min(500, text.length())));
        entry.setTitle(document.title());
        entry.setPath(document.baseUri());
        return entry;
    }

}
