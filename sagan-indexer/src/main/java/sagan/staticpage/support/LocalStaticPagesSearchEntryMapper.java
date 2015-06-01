package sagan.staticpage.support;

import sagan.search.types.SearchEntry;
import sagan.search.SearchEntryMapper;

import java.util.Date;

import org.jsoup.nodes.Document;
import sagan.search.types.SitePage;

public class LocalStaticPagesSearchEntryMapper implements SearchEntryMapper<Document> {

    @Override
    public SitePage map(Document document) {
        SitePage entry = new SitePage();
        String text = document.getElementsByClass("body--container").text();
        entry.setRawContent(text);
        entry.setSummary(text.substring(0, Math.min(500, text.length())));
        entry.setTitle(document.title());
        entry.setPath(document.baseUri());
        return entry;
    }

}
