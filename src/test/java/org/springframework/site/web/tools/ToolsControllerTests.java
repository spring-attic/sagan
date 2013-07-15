package org.springframework.site.web.tools;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.site.domain.tools.ToolsService;
import org.springframework.site.domain.tools.eclipse.EclipseDownloads;
import org.springframework.site.domain.tools.eclipse.EclipsePlatform;
import org.springframework.site.domain.tools.toolsuite.EclipseVersion;
import org.springframework.site.domain.tools.toolsuite.ToolSuiteDownloads;
import org.springframework.site.domain.tools.toolsuite.ToolSuitePlatform;
import org.springframework.site.domain.tools.toolsuite.UpdateSiteArchive;
import org.springframework.ui.ExtendedModelMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
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

	@SuppressWarnings("unchecked")
	@Test
	public void allStsDownloadsAddsDownloadsToModel() throws Exception {
		Map<String, ToolSuitePlatform> platforms = new HashMap<String, ToolSuitePlatform>();
		ToolSuitePlatform windows = new ToolSuitePlatform("windows", "blah",
				Collections.<EclipseVersion> emptyList());
		platforms.put("windows", windows);
		ToolSuitePlatform mac = new ToolSuitePlatform("mac", "blah",
				Collections.<EclipseVersion> emptyList());
		platforms.put("mac", mac);
		ToolSuitePlatform linux = new ToolSuitePlatform("linux", "blah",
				Collections.<EclipseVersion> emptyList());
		platforms.put("linux", linux);

		List<UpdateSiteArchive> archives = Collections.emptyList();
		ToolSuiteDownloads toolSuite = new ToolSuiteDownloads(platforms, archives);
		when(this.service.getStsDownloads()).thenReturn(toolSuite);
		this.controller.allStsDownloads(this.model);

		assertThat((List<ToolSuitePlatform>) this.model.get("platforms"),
				contains(windows, mac, linux));
		assertThat((List<UpdateSiteArchive>) this.model.get("updateSiteArchives"),
				sameInstance(archives));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void allGgtsDownloadsAddsDownloadsToModel() throws Exception {
		Map<String, ToolSuitePlatform> platforms = new HashMap<String, ToolSuitePlatform>();
		ToolSuitePlatform windows = new ToolSuitePlatform("windows", "blah",
				Collections.<EclipseVersion> emptyList());
		platforms.put("windows", windows);
		ToolSuitePlatform mac = new ToolSuitePlatform("mac", "blah",
				Collections.<EclipseVersion> emptyList());
		platforms.put("mac", mac);
		ToolSuitePlatform linux = new ToolSuitePlatform("linux", "blah",
				Collections.<EclipseVersion> emptyList());
		platforms.put("linux", linux);

		List<UpdateSiteArchive> archives = Collections.emptyList();
		ToolSuiteDownloads toolSuite = new ToolSuiteDownloads(platforms, archives);
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
		Map<String, EclipsePlatform> platforms = new HashMap<String, EclipsePlatform>();

		EclipsePlatform windows = new EclipsePlatform("windows");
		platforms.put("windows", windows);
		EclipsePlatform mac = new EclipsePlatform("mac");
		platforms.put("mac", mac);
		EclipsePlatform linux = new EclipsePlatform("linux");
		platforms.put("linux", linux);

		EclipseDownloads eclipseDownloads = new EclipseDownloads(platforms);

		when(this.service.getEclipseDownloads()).thenReturn(eclipseDownloads);
		this.controller.eclipseIndex(this.model);

		assertThat((List<EclipsePlatform>) this.model.get("platforms"), contains(windows, mac, linux));
	}
}
