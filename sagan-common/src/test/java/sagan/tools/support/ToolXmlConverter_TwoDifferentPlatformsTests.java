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

public class ToolXmlConverter_TwoDifferentPlatformsTests {
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

        download = new Download();
        download.setDescription("Windows (64bit)");
        download.setOs("windows");
        download.setFile("release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-win32-x86_64-installer.exe");
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
    public void setsTheReleaseName() {
        assertThat(toolSuite.getReleaseName(), equalTo("3.3.0.RELEASE"));
    }

    @Test
    public void addsBothPlatforms() throws Exception {
        assertThat(toolSuite.getPlatformList().get(0).getName(), equalTo("Windows"));
        assertThat(toolSuite.getPlatformList().get(1).getName(), equalTo("Mac"));
    }

    @Test
    public void addsAnEclipseVersionToEachPlatform() throws Exception {
        ToolSuitePlatform mac = toolSuite.getPlatformList().get(0);
        assertThat(mac.getEclipseVersions().size(), equalTo(1));
        assertThat(mac.getEclipseVersions().get(0).getName(), equalTo("4.3"));

        ToolSuitePlatform windows = toolSuite.getPlatformList().get(0);
        assertThat(windows.getEclipseVersions().size(), equalTo(1));
        assertThat(windows.getEclipseVersions().get(0).getName(), equalTo("4.3"));
    }

    @Test
    public void addsAnArchitectureToTheEclipseVersionInEachPlatform() throws Exception {
        ToolSuitePlatform mac = toolSuite.getPlatformList().get(1);
        EclipseVersion eclipseVersion = mac.getEclipseVersions().get(0);
        assertThat(eclipseVersion.getArchitectures().size(), equalTo(1));
        assertThat(eclipseVersion.getArchitectures().get(0).getName(), equalTo("Mac OS X (Cocoa)"));

        ToolSuitePlatform windows = toolSuite.getPlatformList().get(0);
        EclipseVersion windowsEclipseVersion = windows.getEclipseVersions().get(0);
        assertThat(windowsEclipseVersion.getArchitectures().size(), equalTo(1));
        assertThat(windowsEclipseVersion.getArchitectures().get(0).getName(), equalTo("Windows (64bit)"));
    }

    @Test
    public void addsADownloadLinkTheArchitectureInEachPlatform() throws Exception {
        Architecture macArchitecture =
                toolSuite.getPlatformList().get(1).getEclipseVersions().get(0).getArchitectures().get(0);
        assertThat(macArchitecture.getDownloadLinks().size(), equalTo(1));
        assertThat(
                macArchitecture.getDownloadLinks().get(0).getUrl(),
                equalTo("http://dist.springsource.com/release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-macosx-cocoa-installer.dmg"));

        Architecture windowsArchitecture =
                toolSuite.getPlatformList().get(0).getEclipseVersions().get(0).getArchitectures().get(0);
        assertThat(windowsArchitecture.getDownloadLinks().size(), equalTo(1));
        assertThat(
                windowsArchitecture.getDownloadLinks().get(0).getUrl(),
                equalTo("http://dist.springsource.com/release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-win32-x86_64-installer.exe"));
    }
}
