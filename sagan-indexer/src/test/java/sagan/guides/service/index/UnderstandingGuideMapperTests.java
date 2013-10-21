package sagan.guides.service.index;

import sagan.guides.UnderstandingGuide;
import sagan.guides.service.index.UnderstandingGuideMapper;
import sagan.search.SearchEntry;

import java.util.Date;

import org.hamcrest.core.IsEqual;
import org.junit.Test;

import sagan.util.FixtureLoader;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class UnderstandingGuideMapperTests {
    private UnderstandingGuideMapper guideMapper = new UnderstandingGuideMapper();
    private UnderstandingGuide guide = new UnderstandingGuide("foo", "<h1>Understanding: foo</h1><p>content</p>", "<p>sidebar</p>");
    private SearchEntry entry = guideMapper.map(guide);

    @Test
    public void publishAtDateDefaultsTo0() throws Exception {
        assertThat(entry.getPublishAt(), equalTo(new Date(0L)));
    }

    @Test
    public void titleIsSubject() throws Exception {
        assertThat(entry.getTitle(), equalTo("foo"));
    }

    @Test
    public void rawContentDoesNotHaveAnyHtml() throws Exception {
        assertThat(entry.getRawContent(), equalTo("Understanding: foo content"));
    }

    @Test
    public void facetTypeIsUnderstanding() throws Exception {
        assertThat(entry.getFacetPaths(), containsInAnyOrder("Guides", "Guides/Understanding"));
    }

    @Test
    public void summaryIsFirst500Characters() throws Exception {
        String content = FixtureLoader.load("/fixtures/understanding/amqp/README.html");
        UnderstandingGuide guide = new UnderstandingGuide("foo", content, "sidebar");
        SearchEntry entry = guideMapper.map(guide);
        assertThat(entry.getSummary().length(), equalTo(500));
    }

    @Test
    public void summaryIsWholeContentIfLessThan500Characters() throws Exception {
        assertThat(entry.getSummary(), equalTo("Understanding: foo content"));
    }

    @Test
    public void hasPath() throws Exception {
        assertThat(entry.getPath(), equalTo("understanding/foo"));
    }

    @Test
    public void mapsSubTitle() throws Exception {
        assertThat(entry.getSubTitle(), IsEqual.equalTo("Understanding Doc"));
    }
}
