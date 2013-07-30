package org.springframework.site.domain.tools.toolsuite.parser;

import org.junit.Before;
import org.junit.Test;
import org.springframework.site.domain.tools.toolsuite.Architecture;
import org.springframework.site.domain.tools.toolsuite.EclipseVersion;
import org.springframework.site.domain.tools.toolsuite.ToolSuiteDownloads;
import org.springframework.site.domain.tools.toolsuite.ToolSuitePlatform;
import org.springframework.site.domain.tools.toolsuite.xml.Download;
import org.springframework.site.domain.tools.toolsuite.xml.Release;
import org.springframework.site.domain.tools.toolsuite.xml.ToolSuiteXml;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ToolXmlConverter_TwoDifferentPlatformsTests {
	private ToolSuiteDownloads toolSuite;
	private ToolXmlConverter toolXmlConverter;


	@Before
	public void setUp() throws Exception {
		ToolSuiteXml toolSuiteXml = new ToolSuiteXml();
		List<Release> releases = new ArrayList<Release>();
		Release release = new Release();
		List<Download> downloads = new ArrayList<Download>();

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
		toolSuite = toolXmlConverter.convert(toolSuiteXml, "Spring Tool Suite");
	}


	@Test
	public void setsTheReleaseName() {
		assertThat(toolSuite.getReleaseName(), equalTo("3.3.0.RELEASE"));
	}

	@Test
	public void addsBothPlatforms() throws Exception {
		assertThat(toolSuite.getPlatforms().size(), equalTo(2));
		assertThat(toolSuite.getPlatforms().get("mac").getName(), equalTo("Mac"));
		assertThat(toolSuite.getPlatforms().get("windows").getName(), equalTo("Windows"));
	}

	@Test
	public void addsAnEclipseVersionToEachPlatform() throws Exception {
		ToolSuitePlatform mac = toolSuite.getPlatforms().get("mac");
		assertThat(mac.getEclipseVersions().size(), equalTo(1));
		assertThat(mac.getEclipseVersions().get(0).getName(), equalTo("4.3"));

		ToolSuitePlatform windows = toolSuite.getPlatforms().get("windows");
		assertThat(windows.getEclipseVersions().size(), equalTo(1));
		assertThat(windows.getEclipseVersions().get(0).getName(), equalTo("4.3"));
	}

	@Test
	public void addsAnArchitectureToTheEclipseVersionInEachPlatform() throws Exception {
		ToolSuitePlatform mac = toolSuite.getPlatforms().get("mac");
		EclipseVersion eclipseVersion = mac.getEclipseVersions().get(0);
		assertThat(eclipseVersion.getArchitectures().size(), equalTo(1));
		assertThat(eclipseVersion.getArchitectures().get(0).getName(), equalTo("Mac OS X (Cocoa)"));

		ToolSuitePlatform windows = toolSuite.getPlatforms().get("windows");
		EclipseVersion windowsEclipseVersion = windows.getEclipseVersions().get(0);
		assertThat(windowsEclipseVersion.getArchitectures().size(), equalTo(1));
		assertThat(windowsEclipseVersion.getArchitectures().get(0).getName(), equalTo("Windows (64bit)"));
	}

	@Test
	public void addsADownloadLinkTheArchitectureInEachPlatform() throws Exception {
		Architecture macArchitecture = toolSuite.getPlatforms().get("mac").getEclipseVersions().get(0).getArchitectures().get(0);
		assertThat(macArchitecture.getDownloadLinks().size(), equalTo(1));
		assertThat(macArchitecture.getDownloadLinks().get(0).getUrl(), equalTo("http://dist.springsource.com/release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-macosx-cocoa-installer.dmg"));

		Architecture windowsArchitecture = toolSuite.getPlatforms().get("windows").getEclipseVersions().get(0).getArchitectures().get(0);
		assertThat(windowsArchitecture.getDownloadLinks().size(), equalTo(1));
		assertThat(windowsArchitecture.getDownloadLinks().get(0).getUrl(), equalTo("http://dist.springsource.com/release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-win32-x86_64-installer.exe"));
	}
}
