package org.springframework.site.web.tools;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.site.domain.tools.ToolsService;
import org.springframework.site.domain.tools.eclipse.EclipseDownloads;
import org.springframework.site.domain.tools.eclipse.EclipsePlatform;
import org.springframework.site.domain.tools.eclipse.EclipseRelease;
import org.springframework.site.domain.tools.toolsuite.Architecture;
import org.springframework.site.domain.tools.toolsuite.DownloadLink;
import org.springframework.site.domain.tools.toolsuite.EclipseVersion;
import org.springframework.site.domain.tools.toolsuite.ToolSuiteDownloads;
import org.springframework.site.domain.tools.toolsuite.ToolSuitePlatform;
import org.springframework.site.domain.tools.toolsuite.UpdateSiteArchive;
import org.springframework.ui.ExtendedModelMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ToolsControllerTests {

	private ToolsController controller;
	private ExtendedModelMap model = new ExtendedModelMap();

	@Mock
	private ToolsService service;

	@Before
	public void setUp() throws Exception {
		this.controller = new ToolsController(this.service);
	}

	@Test
	public void stsIndexHasDownloadLinks() throws Exception {
		Map<String, ToolSuitePlatform> platforms = new HashMap<>();
		List<DownloadLink> downloadLinks = Collections.singletonList(new DownloadLink(
				"http://example.com/download.dmg", "dmg", "323MB", "mac", "64"));
		List<Architecture> architectures = Collections.singletonList(new Architecture(
				"Mac OS X (Cocoa, 64bit)", downloadLinks));
		List<EclipseVersion> eclipseVersions = Collections
				.singletonList(new EclipseVersion("1.2", architectures));

		ToolSuitePlatform windows = new ToolSuitePlatform("windows", eclipseVersions);
		platforms.put("windows", windows);

		List<UpdateSiteArchive> archives = Collections.emptyList();
		ToolSuiteDownloads toolSuite = new ToolSuiteDownloads("3.1.2.RELEASE", platforms,
				archives);
		when(this.service.getStsDownloads()).thenReturn(toolSuite);
		this.controller.stsIndex(this.model);

		@SuppressWarnings("unchecked")
		Set<DownloadLink> actual = (Set<DownloadLink>) this.model.get("downloadLinks");
		assertThat(actual, equalTo(toolSuite.getPreferredDownloadLinks()));
		assertThat((String) this.model.get("version"), equalTo("3.1.2.RELEASE"));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void allStsDownloadsAddsDownloadsToModel() throws Exception {
		Map<String, ToolSuitePlatform> platforms = new HashMap<>();
		ToolSuitePlatform windows = new ToolSuitePlatform("windows",

		Collections.<EclipseVersion> emptyList());
		platforms.put("windows", windows);
		ToolSuitePlatform mac = new ToolSuitePlatform("mac",
				Collections.<EclipseVersion> emptyList());
		platforms.put("mac", mac);
		ToolSuitePlatform linux = new ToolSuitePlatform("linux",
				Collections.<EclipseVersion> emptyList());
		platforms.put("linux", linux);

		List<UpdateSiteArchive> archives = Collections.emptyList();
		ToolSuiteDownloads toolSuite = new ToolSuiteDownloads("3.1.2.RELEASE", platforms,
				archives);
		when(this.service.getStsDownloads()).thenReturn(toolSuite);
		this.controller.allStsDownloads(this.model);

		assertThat((List<ToolSuitePlatform>) this.model.get("platforms"),
				contains(windows, mac, linux));
		assertThat((List<UpdateSiteArchive>) this.model.get("updateSiteArchives"),
				sameInstance(archives));
	}

	@Test
	public void ggtsIndexHasDownloadLinks() throws Exception {
		Map<String, ToolSuitePlatform> platforms = new HashMap<>();
		List<DownloadLink> downloadLinks = Collections.singletonList(new DownloadLink(
				"http://example.com/download.dmg", "dmg", "323MB", "mac", "64"));
		List<Architecture> architectures = Collections.singletonList(new Architecture(
				"Mac OS X (Cocoa, 64bit)", downloadLinks));
		List<EclipseVersion> eclipseVersions = Collections
				.singletonList(new EclipseVersion("1.2", architectures));

		ToolSuitePlatform windows = new ToolSuitePlatform("windows", eclipseVersions);
		platforms.put("windows", windows);

		List<UpdateSiteArchive> archives = Collections.emptyList();
		ToolSuiteDownloads toolSuite = new ToolSuiteDownloads("3.1.2.RELEASE", platforms,
				archives);
		when(this.service.getGgtsDownloads()).thenReturn(toolSuite);
		this.controller.ggtsIndex(this.model);

		@SuppressWarnings("unchecked")
		Set<DownloadLink> actual = (Set<DownloadLink>) this.model.get("downloadLinks");
		assertThat(actual, equalTo(toolSuite.getPreferredDownloadLinks()));
		assertThat((String) this.model.get("version"), equalTo("3.1.2.RELEASE"));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void allGgtsDownloadsAddsDownloadsToModel() throws Exception {
		Map<String, ToolSuitePlatform> platforms = new HashMap<>();
		ToolSuitePlatform windows = new ToolSuitePlatform("windows",
				Collections.<EclipseVersion> emptyList());
		platforms.put("windows", windows);
		ToolSuitePlatform mac = new ToolSuitePlatform("mac",
				Collections.<EclipseVersion> emptyList());
		platforms.put("mac", mac);
		ToolSuitePlatform linux = new ToolSuitePlatform("linux",
				Collections.<EclipseVersion> emptyList());
		platforms.put("linux", linux);

		List<UpdateSiteArchive> archives = Collections.emptyList();
		ToolSuiteDownloads toolSuite = new ToolSuiteDownloads("3.1.2.RELEASE", platforms,
				archives);
		when(this.service.getGgtsDownloads()).thenReturn(toolSuite);
		this.controller.allGgtsDownloads(this.model);

		assertThat((List<ToolSuitePlatform>) this.model.get("platforms"),
				contains(windows, mac, linux));
		assertThat((List<UpdateSiteArchive>) this.model.get("updateSiteArchives"),
				sameInstance(archives));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void allEclipseDownloadsAddsDownloadsToModel() throws Exception {
		Map<String, EclipsePlatform> platforms = new HashMap<>();

		EclipsePlatform windows = new EclipsePlatform("windows",
				Collections.<EclipseRelease> emptyList());
		platforms.put("windows", windows);
		EclipsePlatform mac = new EclipsePlatform("mac",
				Collections.<EclipseRelease> emptyList());
		platforms.put("mac", mac);
		EclipsePlatform linux = new EclipsePlatform("linux",
				Collections.<EclipseRelease> emptyList());
		platforms.put("linux", linux);

		EclipseDownloads eclipseDownloads = new EclipseDownloads(platforms);

		when(this.service.getEclipseDownloads()).thenReturn(eclipseDownloads);
		this.controller.eclipseIndex(this.model);

		assertThat((List<EclipsePlatform>) this.model.get("platforms"),
				contains(windows, mac, linux));
	}
}
