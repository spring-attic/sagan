package sagan.guides.support;

import org.jsoup.Jsoup;
import sagan.guides.UnderstandingDoc;
import sagan.search.SearchEntryMapper;
import sagan.search.types.GuideDoc;

class UnderstandingDocMapper implements SearchEntryMapper<UnderstandingDoc> {
    @Override
    public GuideDoc map(UnderstandingDoc guide) {
        GuideDoc entry = new GuideDoc();
        String rawContent = Jsoup.parse(guide.getContent()).text();
        entry.setRawContent(rawContent);
        entry.setTitle(guide.getSubject());
        entry.setPath("understanding/" + guide.getSubject());
        entry.setSummary(rawContent.substring(0, Math.min(500, rawContent.length())));
        entry.addFacetPaths("Guides", "Guides/Understanding");
        entry.setSubTitle("Understanding Doc");
        return entry;
    }
}
