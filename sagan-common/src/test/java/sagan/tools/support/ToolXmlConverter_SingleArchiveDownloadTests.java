package sagan.tools.support;

import sagan.tools.Download;
import sagan.tools.Release;
import sagan.tools.ToolSuiteDownloads;
import sagan.tools.UpdateSiteArchive;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ToolXmlConverter_SingleArchiveDownloadTests {
    private ToolSuiteDownloads toolSuite;
    private ToolXmlConverter toolXmlConverter;

    @Before
    public void setUp() throws Exception {
        ToolSuiteXml toolSuiteXml = new ToolSuiteXml();
        List<Release> releases = new ArrayList<>();
        Release release = new Release();
        List<Download> downloads = new ArrayList<>();

        Download download = new Download();
        download.setDescription("Update Site");
        download.setOs("all");
        download.setFile("release/TOOLS/update/3.3.0.RELEASE/e4.3/springsource-tool-suite-3.3.0.RELEASE-e4.3-updatesite.zip");
        download.setBucket("http://dist.springsource.com/");
        download.setEclipseVersion("4.3.x");
        download.setSize("172MB");
        download.setVersion("3.3.0.RELEASE");
        downloads.add(download);

        release.setDownloads(downloads);
        release.setName("Spring Tool Suite 3.3.0.RELEASE - based on Eclipse Kepler 4.3");
        releases.add(release);

        toolSuiteXml.setReleases(releases);

        toolXmlConverter = new ToolXmlConverter();
        toolSuite = toolXmlConverter.convert(toolSuiteXml, "Spring Tool Suite", "STS");
    }

    @Test
    public void addsAnUpdateSiteArchive() {
        assertThat(toolSuite.getArchives().size(), equalTo(1));
        UpdateSiteArchive archive = toolSuite.getArchives().get(0);
        assertThat(archive.getVersion(), equalTo("4.3.x"));
        assertThat(
                archive.getUrl(),
                equalTo("http://dist.springsource.com/release/TOOLS/update/3.3.0.RELEASE/e4.3/springsource-tool-suite-3.3.0.RELEASE-e4.3-updatesite.zip"));
        assertThat(archive.getFileSize(), equalTo("172MB"));
        assertThat(archive.getFileName(), equalTo("springsource-tool-suite-3.3.0.RELEASE-e4.3-updatesite.zip"));
    }
}
