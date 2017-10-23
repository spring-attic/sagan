package sagan.guides.support;

import org.hamcrest.core.IsEqual;
import org.junit.Test;
import sagan.guides.UnderstandingDoc;
import sagan.search.types.GuideDoc;
import sagan.search.types.SearchEntry;
import sagan.support.Fixtures;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

/**
 * Unit tests for {@link UnderstandingDocMapper}.
 */
public class UnderstandingDocMapperTests {

    private final UnderstandingDocMapper mapper = new UnderstandingDocMapper();
    private final UnderstandingDoc doc = new UnderstandingDoc("foo",
            "<h1>Understanding: foo</h1><p>content</p>");

    private final GuideDoc entry = mapper.map(doc);

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
        String content = Fixtures.load("/fixtures/understanding/amqp/README.html");
        UnderstandingDoc guide = new UnderstandingDoc("foo", content);
        SearchEntry entry = mapper.map(guide);
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
