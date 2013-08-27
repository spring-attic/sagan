package io.spring.site.domain.tools.toolsuite.parser;

import org.junit.Before;
import org.junit.Test;

import io.spring.site.domain.tools.toolsuite.Architecture;
import io.spring.site.domain.tools.toolsuite.EclipseVersion;
import io.spring.site.domain.tools.toolsuite.ToolSuiteDownloads;
import io.spring.site.domain.tools.toolsuite.ToolSuitePlatform;
import io.spring.site.domain.tools.toolsuite.parser.ToolXmlConverter;
import io.spring.site.domain.tools.toolsuite.xml.Download;
import io.spring.site.domain.tools.toolsuite.xml.Release;
import io.spring.site.domain.tools.toolsuite.xml.ToolSuiteXml;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ToolXmlConverter_TwoDifferentFileTypesForSameVersionTests {

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
		download.setDescription("Mac OS X (Cocoa)");
		download.setOs("mac");
		download.setFile("release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-macosx-cocoa-installer.tar.gz");
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
	public void addsAPlatform() throws Exception {
		assertThat(toolSuite.getPlatforms().size(), equalTo(1));
		assertThat(toolSuite.getPlatforms().get("mac").getName(), equalTo("Mac"));
	}

	@Test
	public void addsAnEclipseVersionToThePlatform() throws Exception {
		ToolSuitePlatform platform = toolSuite.getPlatforms().get("mac");
		assertThat(platform.getEclipseVersions().size(), equalTo(1));
		assertThat(platform.getEclipseVersions().get(0).getName(), equalTo("4.3"));
	}

	@Test
	public void addsAnArchitectureToTheEclipseVersion() throws Exception {
		ToolSuitePlatform platform = toolSuite.getPlatforms().get("mac");
		EclipseVersion eclipseVersion = platform.getEclipseVersions().get(0);
		assertThat(eclipseVersion.getArchitectures().size(), equalTo(1));
		assertThat(eclipseVersion.getArchitectures().get(0).getName(), equalTo("Mac OS X (Cocoa)"));
	}

	@Test
	public void addsADownloadLinkTheArchitecture() throws Exception {
		ToolSuitePlatform platform = toolSuite.getPlatforms().get("mac");
		EclipseVersion eclipseVersion = platform.getEclipseVersions().get(0);
		Architecture architecture = eclipseVersion.getArchitectures().get(0);

		assertThat(architecture.getDownloadLinks().size(), equalTo(2));
		assertThat(architecture.getDownloadLinks().get(0).getUrl(), equalTo("http://dist.springsource.com/release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-macosx-cocoa-installer.dmg"));
		assertThat(architecture.getDownloadLinks().get(1).getUrl(), equalTo("http://dist.springsource.com/release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-macosx-cocoa-installer.tar.gz"));
	}

}
