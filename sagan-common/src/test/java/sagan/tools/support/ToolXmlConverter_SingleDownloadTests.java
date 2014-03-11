package sagan.tools.support;

import sagan.tools.Architecture;
import sagan.tools.Download;
import sagan.tools.EclipseVersion;
import sagan.tools.Release;
import sagan.tools.ToolSuiteDownloads;
import sagan.tools.ToolSuitePlatform;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ToolXmlConverter_SingleDownloadTests {

    private ToolSuiteDownloads toolSuite;
    private ToolXmlConverter toolXmlConverter;

    @Before
    public void setUp() throws Exception {
        ToolSuiteXml toolSuiteXml = new ToolSuiteXml();
        List<Release> releases = new ArrayList<>();
        Release release = new Release();
        List<Download> downloads = new ArrayList<>();

        Download download = new Download();
        download.setDescription("Mac OS X (Cocoa)");
        download.setOs("mac");
        download.setFile("release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-macosx-cocoa-installer.dmg");
        download.setBucket("http://dist.springsource.com/");
        download.setEclipseVersion("4.3");
        download.setSize("373MB");
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
    public void addsAReleaseName() throws Exception {
        assertThat(toolSuite.getReleaseName(), equalTo("3.3.0.RELEASE"));
    }

    @Test
    public void addsAPlatform() throws Exception {
        assertThat(toolSuite.getPlatformList().size(), equalTo(3));
        assertThat(toolSuite.getPlatformList().get(1).getName(), equalTo("Mac"));
    }

    @Test
    public void addsAnEclipseVersionToThePlatform() throws Exception {
        ToolSuitePlatform platform = toolSuite.getPlatformList().get(1);
        assertThat(platform.getEclipseVersions().size(), equalTo(1));
        assertThat(platform.getEclipseVersions().get(0).getName(), equalTo("4.3"));
    }

    @Test
    public void addsAnArchitectureToTheEclipseVersion() throws Exception {
        ToolSuitePlatform platform = toolSuite.getPlatformList().get(1);
        EclipseVersion eclipseVersion = platform.getEclipseVersions().get(0);
        assertThat(eclipseVersion.getArchitectures().size(), equalTo(1));
        assertThat(eclipseVersion.getArchitectures().get(0).getName(), equalTo("Mac OS X (Cocoa)"));
    }

    @Test
    public void addsADownloadLinkTheArchitecture() throws Exception {
        ToolSuitePlatform platform = toolSuite.getPlatformList().get(1);
        EclipseVersion eclipseVersion = platform.getEclipseVersions().get(0);
        Architecture architecture = eclipseVersion.getArchitectures().get(0);

        assertThat(architecture.getDownloadLinks().size(), equalTo(1));
        assertThat(
                architecture.getDownloadLinks().get(0).getUrl(),
                equalTo("http://dist.springsource.com/release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-macosx-cocoa-installer.dmg"));
        assertThat(architecture.getDownloadLinks().get(0).getOs(), equalTo("mac"));
        assertThat(architecture.getDownloadLinks().get(0).getArchitecture(), equalTo("32"));
    }
}
