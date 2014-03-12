package sagan.tools.support;

import sagan.tools.Architecture;
import sagan.tools.Download;
import sagan.tools.EclipseVersion;
import sagan.tools.Release;
import sagan.tools.ToolSuiteDownloads;
import sagan.tools.ToolSuitePlatform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ToolXmlConverter_TwoDifferentEclipseVersionsTests {
    private ToolSuiteDownloads toolSuite;
    private ToolXmlConverter toolXmlConverter;

    @Before
    public void setUp() throws Exception {
        ToolSuiteXml toolSuiteXml = new ToolSuiteXml();
        List<Release> releases = new ArrayList<>();

        Download download = new Download();
        download.setDescription("Mac OS X (Cocoa)");
        download.setOs("mac");
        download.setFile("release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-macosx-cocoa-installer.dmg");
        download.setBucket("http://dist.springsource.com/");
        download.setEclipseVersion("4.3");
        download.setSize("373MB");
        download.setVersion("3.3.0.RELEASE");

        Release release = new Release();
        release.setDownloads(Collections.singletonList(download));
        release.setName("Spring Tool Suite 3.3.0.RELEASE - based on Eclipse Kepler 4.3");
        releases.add(release);

        download = new Download();
        download.setDescription("Mac OS X (Cocoa)");
        download.setOs("mac");
        download.setFile("release/STS/3.3.0/dist/e3.8/spring-tool-suite-3.3.0.RELEASE-e3.8.2-macosx-cocoa-installer.dmg");
        download.setBucket("http://dist.springsource.com/");
        download.setEclipseVersion("3.8.2");
        download.setSize("368MB");
        download.setVersion("3.3.0.RELEASE");

        release = new Release();
        release.setDownloads(Collections.singletonList(download));
        release.setName("Spring Tool Suite 3.3.0.RELEASE - based on Eclipse Juno 3.8.2");
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
    public void addsTheMacPlatform() throws Exception {
        assertThat(toolSuite.getPlatformList().get(1).getName(), equalTo("Mac"));
    }

    @Test
    public void addsBothEclipseVersionsToThePlatform() throws Exception {
        ToolSuitePlatform mac = toolSuite.getPlatformList().get(1);
        assertThat(mac.getEclipseVersions().size(), equalTo(2));
        assertThat(mac.getEclipseVersions().get(0).getName(), equalTo("4.3"));
        assertThat(mac.getEclipseVersions().get(1).getName(), equalTo("3.8.2"));
    }

    @Test
    public void addsAnArchitectureToEachEclipseVersion() throws Exception {
        ToolSuitePlatform mac = toolSuite.getPlatformList().get(1);
        EclipseVersion eclipseVersion = mac.getEclipseVersions().get(0);
        assertThat(eclipseVersion.getArchitectures().size(), equalTo(1));
        assertThat(eclipseVersion.getArchitectures().get(0).getName(), equalTo("Mac OS X (Cocoa)"));

        EclipseVersion windowsEclipseVersion = mac.getEclipseVersions().get(1);
        assertThat(windowsEclipseVersion.getArchitectures().size(), equalTo(1));
        assertThat(windowsEclipseVersion.getArchitectures().get(0).getName(), equalTo("Mac OS X (Cocoa)"));
    }

    @Test
    public void addsADownloadLinkTheArchitectureInEachPlatform() throws Exception {
        Architecture v43Architecture =
                toolSuite.getPlatformList().get(1).getEclipseVersions().get(0).getArchitectures().get(0);
        assertThat(v43Architecture.getDownloadLinks().size(), equalTo(1));
        assertThat(
                v43Architecture.getDownloadLinks().get(0).getUrl(),
                equalTo("http://dist.springsource.com/release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-macosx-cocoa-installer.dmg"));

        Architecture v38Architecture =
                toolSuite.getPlatformList().get(1).getEclipseVersions().get(1).getArchitectures().get(0);
        assertThat(v38Architecture.getDownloadLinks().size(), equalTo(1));
        assertThat(
                v38Architecture.getDownloadLinks().get(0).getUrl(),
                equalTo("http://dist.springsource.com/release/STS/3.3.0/dist/e3.8/spring-tool-suite-3.3.0.RELEASE-e3.8.2-macosx-cocoa-installer.dmg"));
    }
}
