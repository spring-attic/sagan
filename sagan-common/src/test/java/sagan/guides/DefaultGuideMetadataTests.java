package sagan.guides;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link DefaultGuideMetadata}.
 *
 * @author Chris Beams
 */
public class DefaultGuideMetadataTests {

    private DefaultGuideMetadata guide;

    @Before
    public void setUp() throws Exception {
        guide = new DefaultGuideMetadata("my-org", "rest-service", "gs-rest-service", "Title :: Description");
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
        assertThat(guide.getCiStatusImageUrl(), is("https://drone.io/github.com/my-org/gs-rest-service/status.png"));
    }

    @Test
    public void testGetCiLatestUrl() throws Exception {
        assertThat(guide.getCiLatestUrl(), is("https://drone.io/github.com/my-org/gs-rest-service/latest"));
    }
}
