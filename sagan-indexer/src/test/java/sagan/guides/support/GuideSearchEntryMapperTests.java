package sagan.guides.support;

import sagan.guides.DefaultGuideMetadata;
import sagan.guides.GettingStartedGuide;
import sagan.search.types.SearchEntry;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class GuideSearchEntryMapperTests {

    private GettingStartedGuide guide;
    private GuideSearchEntryMapper guideMapper = new GuideSearchEntryMapper();
    private SearchEntry searchEntry;

    @Before
    public void setUp() throws Exception {
        guide = new GettingStartedGuide(
                new DefaultGuideMetadata("my-org", "xyz", "gs-xyz", "Guide XYZ Title::Guide XYZ Subtitle"));
        guide.setContent("Some Guide Content");
        searchEntry = guideMapper.map(guide);
    }

    @Test
    public void mapsRawContent() throws Exception {
        assertThat(searchEntry.getRawContent(), equalTo("Some Guide Content"));
    }

    @Test
    public void mapsTitle() throws Exception {
        assertThat(searchEntry.getTitle(), equalTo("Guide XYZ Title"));
    }

    @Test
    public void mapsSubTitle() throws Exception {
        assertThat(searchEntry.getSubTitle(), equalTo("Getting Started Guide"));
    }

}
