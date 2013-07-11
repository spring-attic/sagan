package org.springframework.site.web.tools;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.site.domain.tools.ToolsService;
import org.springframework.site.domain.tools.toolsuite.EclipseVersion;
import org.springframework.site.domain.tools.toolsuite.Platform;
import org.springframework.site.domain.tools.toolsuite.ToolSuite;
import org.springframework.ui.ExtendedModelMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
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
		controller = new ToolsController(service);
	}

	@Test
	public void allStsDownloadsAddsDownloadsToModel() throws Exception {
		Map<String, Platform> platforms = new HashMap<String, Platform>();
		Platform windows = new Platform("windows", "blah", Collections.<EclipseVersion>emptyList());
		platforms.put("windows", windows);
		Platform mac = new Platform("mac", "blah", Collections.<EclipseVersion>emptyList());
		platforms.put("mac", mac);
		Platform linux = new Platform("linux", "blah", Collections.<EclipseVersion>emptyList());
		platforms.put("linux", linux);

		ToolSuite toolSuite = new ToolSuite(platforms);
		when(service.getStsDownloads()).thenReturn(toolSuite);
		controller.allStsDownloads(model);
		assertThat((Platform) model.get("windows"), sameInstance(windows));
		assertThat((Platform) model.get("mac"), sameInstance(mac));
		assertThat((Platform) model.get("linux"), sameInstance(linux));
	}
}
