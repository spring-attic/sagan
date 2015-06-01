package sagan.guides.support;

import org.jsoup.Jsoup;
import sagan.guides.Guide;
import sagan.search.SearchEntryMapper;
import sagan.search.types.GuideDoc;

class TutorialMapper implements SearchEntryMapper<Guide> {

    @Override
    public GuideDoc map(Guide tutorial) {
        GuideDoc entry = new GuideDoc();
        entry.setTitle(tutorial.getTitle());
        entry.setSubTitle("Tutorial");

        String text = Jsoup.parse(tutorial.getContent()).text();

        entry.setSummary(text.substring(0, Math.min(500, text.length())));
        entry.setRawContent(text);
        entry.setPath("/guides/tutorials/" + tutorial.getGuideId() + "/");
        entry.addFacetPaths("Guides", "Guides/Tutorials");
        return entry;
    }
}
