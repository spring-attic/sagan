package sagan.guides;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

/**
 * Unit tests for {@link DefaultGuideMetadata}.
 */
public class DefaultGuideMetadataTests {

    private DefaultGuideMetadata guide;

    @Before
    public void setUp() throws Exception {
        guide = new DefaultGuideMetadata("my-org", "rest-service",
				"gs-rest-service", "Title :: Subtitle :: project-1,project-2");
    }

    @Test
    public void testGetRepoName() throws Exception {
        assertThat(guide.getRepoName(), is("gs-rest-service"));
    }

    @Test
    public void testGetZipUrl() throws Exception {
        assertThat(guide.getZipUrl(), is("https://github.com/my-org/gs-rest-service/archive/master.zip"));
    }

    @Test
    public void testGetGitRepoHttpsUrl() throws Exception {
        assertThat(guide.getGitRepoHttpsUrl(), is("https://github.com/my-org/gs-rest-service.git"));
    }

    @Test
    public void testGetGithubRepoHttpsUrl() throws Exception {
        assertThat(guide.getGithubHttpsUrl(), is("https://github.com/my-org/gs-rest-service"));
    }

    @Test
    public void testGetGitRepoSshUrl() throws Exception {
        assertThat(guide.getGitRepoSshUrl(), is("git@github.com:my-org/gs-rest-service.git"));
    }

    @Test
    public void testGetRepoSubversionUrl() throws Exception {
        assertThat(guide.getGitRepoSubversionUrl(), is("https://github.com/my-org/gs-rest-service"));
    }

    @Test
    public void testGetCiStatusImageUrl() throws Exception {
        assertThat(guide.getCiStatusImageUrl(), is("https://travis-ci.org/my-org/gs-rest-service.svg?branch=master"));
    }

    @Test
    public void testGetCiLatestUrl() throws Exception {
        assertThat(guide.getCiLatestUrl(), is("https://travis-ci.org/my-org/gs-rest-service"));
    }

	@Test
	public void testGetTitle() throws Exception {
    	assertThat(guide.getTitle(), is("Title"));
	}

	@Test
	public void testGetSubtitle() throws Exception {
    	assertThat(guide.getSubtitle(), is("Subtitle"));
	}

	@Test
	public void testGetProjects() throws Exception {
    	assertThat(guide.getProjects(), hasItems("project-1", "project-2"));
	}

	@Test
	public void testGetEmptyProjectList() throws Exception {
		guide = new DefaultGuideMetadata("my-org", "rest-service",
				"gs-rest-service", "Title :: Subtitle");
		assertThat(guide.getProjects(), empty());
	}
}
