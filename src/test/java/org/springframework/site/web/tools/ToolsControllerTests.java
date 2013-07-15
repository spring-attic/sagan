package org.springframework.site.web.tools;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.site.domain.tools.ToolsService;
import org.springframework.site.domain.tools.toolsuite.*;
import org.springframework.ui.ExtendedModelMap;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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
		Map<String, Platform> platforms = new HashMap<>();
		List<DownloadLink> downloadLinks = Collections.singletonList(new DownloadLink("http://example.com/download.dmg", "dmg", "323MB", "mac", "64"));
		List<Architecture> architectures = Collections.singletonList(new Architecture("Mac OS X (Cocoa, 64bit)", downloadLinks));
		List<EclipseVersion> eclipseVersions = Collections.singletonList(new EclipseVersion("1.2", architectures));

		Platform windows = new Platform("windows", "3.1.2.RELEASE", eclipseVersions);
		platforms.put("windows", windows);

		List<UpdateSiteArchive> archives = Collections.emptyList();
		ToolSuite toolSuite = new ToolSuite(platforms, archives);
		when(this.service.getStsDownloads()).thenReturn(toolSuite);
		this.controller.stsIndex(this.model);

		assertThat((Set<DownloadLink>) this.model.get("downloadLinks"), equalTo(toolSuite.getPreferredDownloadLinks()));
		assertThat((String) this.model.get("version"), equalTo("3.1.2.RELEASE"));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void allStsDownloadsAddsDownloadsToModel() throws Exception {
		Map<String, Platform> platforms = new HashMap<>();
		Platform windows = new Platform("windows", "blah",
				Collections.<EclipseVersion> emptyList());
		platforms.put("windows", windows);
		Platform mac = new Platform("mac", "blah",
				Collections.<EclipseVersion> emptyList());
		platforms.put("mac", mac);
		Platform linux = new Platform("linux", "blah",
				Collections.<EclipseVersion> emptyList());
		platforms.put("linux", linux);

		List<UpdateSiteArchive> archives = Collections.emptyList();
		ToolSuite toolSuite = new ToolSuite(platforms, archives);
		when(this.service.getStsDownloads()).thenReturn(toolSuite);
		this.controller.allStsDownloads(this.model);

		assertThat((List<Platform>) this.model.get("platforms"),
				contains(windows, mac, linux));
		assertThat((List<UpdateSiteArchive>) this.model.get("updateSiteArchives"),
				sameInstance(archives));
	}

	@Test
	public void ggtsIndexHasDownloadLinks() throws Exception {
		Map<String, Platform> platforms = new HashMap<>();
		List<DownloadLink> downloadLinks = Collections.singletonList(new DownloadLink("http://example.com/download.dmg", "dmg", "323MB", "mac", "64"));
		List<Architecture> architectures = Collections.singletonList(new Architecture("Mac OS X (Cocoa, 64bit)", downloadLinks));
		List<EclipseVersion> eclipseVersions = Collections.singletonList(new EclipseVersion("1.2", architectures));

		Platform windows = new Platform("windows", "3.1.2.RELEASE", eclipseVersions);
		platforms.put("windows", windows);

		List<UpdateSiteArchive> archives = Collections.emptyList();
		ToolSuite toolSuite = new ToolSuite(platforms, archives);
		when(this.service.getGgtsDownloads()).thenReturn(toolSuite);
		this.controller.ggtsIndex(this.model);

		assertThat((Set<DownloadLink>) this.model.get("downloadLinks"), equalTo(toolSuite.getPreferredDownloadLinks()));
		assertThat((String) this.model.get("version"), equalTo("3.1.2.RELEASE"));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void allGgtsDownloadsAddsDownloadsToModel() throws Exception {
		Map<String, Platform> platforms = new HashMap<>();
		Platform windows = new Platform("windows", "blah",
				Collections.<EclipseVersion> emptyList());
		platforms.put("windows", windows);
		Platform mac = new Platform("mac", "blah",
				Collections.<EclipseVersion> emptyList());
		platforms.put("mac", mac);
		Platform linux = new Platform("linux", "blah",
				Collections.<EclipseVersion> emptyList());
		platforms.put("linux", linux);

		List<UpdateSiteArchive> archives = Collections.emptyList();
		ToolSuite toolSuite = new ToolSuite(platforms, archives);
		when(this.service.getGgtsDownloads()).thenReturn(toolSuite);
		this.controller.allGgtsDownloads(this.model);

		assertThat((List<Platform>) this.model.get("platforms"),
				contains(windows, mac, linux));
		assertThat((List<UpdateSiteArchive>) this.model.get("updateSiteArchives"),
				sameInstance(archives));
	}
}
