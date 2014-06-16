package sagan.guides.support;

import sagan.guides.UnderstandingDoc;
import sagan.search.SearchEntry;
import sagan.search.SearchEntryMapper;

import org.jsoup.Jsoup;
import sagan.support.time.DateTimeUtils;

class UnderstandingDocMapper implements SearchEntryMapper<UnderstandingDoc> {
    @Override
    public SearchEntry map(UnderstandingDoc guide) {
        SearchEntry entry = new SearchEntry();
        String rawContent = Jsoup.parse(guide.getContent()).text();
        entry.setRawContent(rawContent);
        entry.setTitle(guide.getSubject());
        entry.setPath("understanding/" + guide.getSubject());
        entry.setSummary(rawContent.substring(0, Math.min(500, rawContent.length())));
        entry.addFacetPaths("Guides", "Guides/Understanding");
        entry.setPublishAt(DateTimeUtils.epoch());
        entry.setSubTitle("Understanding Doc");
        return entry;
    }
}
