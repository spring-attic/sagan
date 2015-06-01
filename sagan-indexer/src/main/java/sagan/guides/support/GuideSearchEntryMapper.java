package sagan.guides.support;

import org.jsoup.Jsoup;
import sagan.guides.Guide;
import sagan.search.SearchEntryMapper;
import sagan.search.types.GuideDoc;
import sagan.search.types.SearchEntry;

class GuideSearchEntryMapper implements SearchEntryMapper<Guide> {

    @Override
    public SearchEntry map(Guide guide) {
        GuideDoc entry = new GuideDoc();
        entry.setTitle(guide.getTitle());
        entry.setSubTitle("Getting Started Guide");

        String text = Jsoup.parse(guide.getContent()).text();

        entry.setSummary(text.substring(0, Math.min(500, text.length())));
        entry.setRawContent(text);
        entry.setPath("/guides/gs/" + guide.getGuideId() + "/");
        entry.addFacetPaths("Guides", "Guides/Getting Started");
        return entry;
    }
}
