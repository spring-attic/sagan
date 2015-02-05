package sagan.tools.support;

import org.junit.Before;
import org.junit.Test;
import sagan.tools.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

public class ToolXmlConverter_SingleLegacyDownloadTests {

    private List<ToolSuiteDownloads> toolSuites;
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
        download.setFile("release/STS/3.5.1/dist/e4.3/spring-tool-suite-3.5.1.RELEASE-e4.3.2-macosx-cocoa-installer.dmg");
        download.setBucket("http://download.springsource.com/");
        download.setEclipseVersion("4.3.2");
        download.setSize("361MB");
        download.setVersion("3.5.1.RELEASE");
        downloads.add(download);
        release.setDownloads(downloads);
        release.setName("Spring Tool Suite 3.5.1.RELEASE - based on Eclipse Kepler 4.3.2");
        releases.add(release);

        release = new Release();
        downloads = new ArrayList<>();
        download = new Download();
        download.setDescription("Mac OS X (Cocoa)");
        download.setOs("mac");
        download.setFile("release/STS/3.5.1/dist/e3.8/spring-tool-suite-3.5.1.RELEASE-e3.8.2-macosx-cocoa-installer.dmg");
        download.setBucket("http://download.springsource.com/");
        download.setEclipseVersion("3.8.2");
        download.setSize("349MB");
        download.setVersion("3.5.1.RELEASE");
        downloads.add(download);
        release.setDownloads(downloads);
        release.setName("Spring Tool Suite 3.5.1.RELEASE - based on Eclipse Juno 3.8.2");
        releases.add(release);

        release = new Release();
        downloads = new ArrayList<>();
        download = new Download();
        download.setDescription("Mac OS X (Cocoa)");
        download.setOs("mac");
        download.setFile("release/STS/3.5.0/dist/e4.3/spring-tool-suite-3.5.0.RELEASE-e4.3.2-macosx-cocoa-installer.dmg");
        download.setBucket("http://download.springsource.com/");
        download.setEclipseVersion("4.3.2");
        download.setSize("361MB");
        download.setVersion("3.5.0.RELEASE");
        downloads.add(download);
        release.setDownloads(downloads);
        release.setName("Spring Tool Suite 3.5.0.RELEASE - based on Eclipse Kepler 4.3.2");
        releases.add(release);

        toolSuiteXml.setOthers(releases);

        toolXmlConverter = new ToolXmlConverter();
        toolSuites = toolXmlConverter.convertLegacy(toolSuiteXml, "Spring Tool Suite", "STS");
    }

    @Test
    public void addsAReleaseName() throws Exception {
        assertThat(toolSuites.stream().map(ToolSuiteDownloads::getReleaseName).collect(Collectors.toList()),
                contains("3.5.1.RELEASE", "3.5.0.RELEASE"));
    }

    @Test
    public void addsAPlatform() throws Exception {
        ToolSuiteDownloads toolSuite = toolSuites.get(0);
        assertThat(toolSuite.getPlatformList().size(), equalTo(3));
        assertThat(toolSuite.getPlatformList().get(1).getName(), equalTo("Mac"));
    }

    @Test
    public void addsAnEclipseVersionToThePlatform() throws Exception {
        ToolSuiteDownloads toolSuite = toolSuites.get(0);
        ToolSuitePlatform platform = toolSuite.getPlatformList().get(1);
        assertThat(platform.getEclipseVersions().size(), equalTo(2));
        assertThat(platform.getEclipseVersions().get(0).getName(), equalTo("4.3.2"));
        assertThat(platform.getEclipseVersions().get(1).getName(), equalTo("3.8.2"));
    }

    @Test
    public void addsAnArchitectureToTheEclipseVersion() throws Exception {
        ToolSuiteDownloads toolSuite = toolSuites.get(0);
        ToolSuitePlatform platform = toolSuite.getPlatformList().get(1);
        EclipseVersion eclipseVersion = platform.getEclipseVersions().get(0);
        assertThat(eclipseVersion.getArchitectures().size(), equalTo(1));
        assertThat(eclipseVersion.getArchitectures().get(0).getName(), equalTo("Mac OS X (Cocoa)"));
    }

    @Test
    public void addsADownloadLinkTheArchitecture() throws Exception {
        ToolSuiteDownloads toolSuite = toolSuites.get(0);
        ToolSuitePlatform platform = toolSuite.getPlatformList().get(1);
        EclipseVersion eclipseVersion = platform.getEclipseVersions().get(0);
        Architecture architecture = eclipseVersion.getArchitectures().get(0);

        assertThat(architecture.getDownloadLinks().size(), equalTo(1));
        assertThat(
                architecture.getDownloadLinks().get(0).getUrl(),
                equalTo("http://download.springsource.com/release/STS/3.5.1/dist/e4.3/spring-tool-suite-3.5.1.RELEASE-e4.3.2-macosx-cocoa-installer.dmg"));
        assertThat(architecture.getDownloadLinks().get(0).getOs(), equalTo("mac"));
        assertThat(architecture.getDownloadLinks().get(0).getArchitecture(), equalTo("32"));
    }
}
