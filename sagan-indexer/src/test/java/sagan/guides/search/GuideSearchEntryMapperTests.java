package sagan.guides.search;

import sagan.guides.ContentProvider;
import sagan.guides.DefaultGuideMetadata;
import sagan.guides.GettingStartedGuide;
import sagan.guides.Guide;
import sagan.guides.ImageProvider;
import sagan.search.SearchEntry;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class GuideSearchEntryMapperTests {

    private Guide guide = new GettingStartedGuide(
            new DefaultGuideMetadata("my-org", "xyz", "gs-xyz", "Guide XYZ Title::Guide XYZ Subtitle"),
                    new ContentProvider<GettingStartedGuide>() {
                        @Override
                        public void populate(GettingStartedGuide guide) {
                            guide.setContent("Some Guide Content");
                            guide.setSidebar("Some Sidebar Content");
                        }
                    },
                    new ImageProvider() {
                        @Override
                        public byte[] loadImage(Guide guide, String imageName) {
                            return new byte[0];
                        }
                    });

    private GuideSearchEntryMapper guideMapper = new GuideSearchEntryMapper();
    private SearchEntry searchEntry;

    @Before
    public void setUp() throws Exception {
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
