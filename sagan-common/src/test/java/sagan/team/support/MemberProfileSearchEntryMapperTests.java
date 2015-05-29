package sagan.team.support;

import sagan.search.types.SearchEntry;
import sagan.team.MemberProfile;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MemberProfileSearchEntryMapperTests {

    private MemberProfile profile = new MemberProfile();
    private MemberProfileSearchEntryMapper mapper = new MemberProfileSearchEntryMapper();
    private SearchEntry searchEntry;

    @Before
    public void setUp() throws Exception {
        profile.setUsername("jdoe");
        profile.setName("First Last");
        profile.setBio("A very good developer!");
        searchEntry = mapper.map(profile);
    }

    @Test
    public void mapFullNameToTitle() {
        assertThat(searchEntry.getTitle(), equalTo("First Last"));
    }

    @Test
    public void mapBioToSummary() {
        assertThat(searchEntry.getSummary(), equalTo("A very good developer!"));
    }

    @Test
    public void mapBioToContent() {
        assertThat(searchEntry.getRawContent(), equalTo("A very good developer!"));
    }

    @Test
    public void setPath() {
        assertThat(searchEntry.getPath(), equalTo("/team/jdoe"));
    }
}
