package sagan.tools.web;

import sagan.tools.eclipse.EclipseDownloads;
import sagan.tools.eclipse.EclipsePlatform;
import sagan.tools.eclipse.EclipseRelease;
import sagan.tools.service.ToolsService;
import sagan.tools.toolsuite.Architecture;
import sagan.tools.toolsuite.DownloadLink;
import sagan.tools.toolsuite.EclipseVersion;
import sagan.tools.toolsuite.ToolSuiteDownloads;
import sagan.tools.toolsuite.ToolSuitePlatform;
import sagan.tools.toolsuite.UpdateSiteArchive;

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

import org.springframework.ui.ExtendedModelMap;

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
        ToolSuiteDownloads toolSuite = new ToolSuiteDownloads("STS", "3.1.2.RELEASE", platforms, archives);
        when(this.service.getStsGaDownloads()).thenReturn(toolSuite);
        this.controller.stsIndex(this.model);

        @SuppressWarnings("unchecked")
        Set<DownloadLink> actual = (Set<DownloadLink>) this.model.get("downloadLinks");
        assertThat(actual, equalTo(toolSuite.getPreferredDownloadLinks()));
        assertThat((String) this.model.get("version"), equalTo("3.1.2.RELEASE"));

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
        ToolSuiteDownloads toolSuite = new ToolSuiteDownloads("GGTS", "3.1.2.RELEASE", platforms, archives);
        when(this.service.getGgtsGaDownloads()).thenReturn(toolSuite);
        this.controller.ggtsIndex(this.model);

        @SuppressWarnings("unchecked")
        Set<DownloadLink> actual = (Set<DownloadLink>) this.model.get("downloadLinks");
        assertThat(actual, equalTo(toolSuite.getPreferredDownloadLinks()));
        assertThat((String) this.model.get("version"), equalTo("3.1.2.RELEASE"));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void allEclipseDownloadsAddsDownloadsToModel() throws Exception {
        Map<String, EclipsePlatform> platforms = new HashMap<>();

        EclipsePlatform windows = new EclipsePlatform("windows", Collections.<EclipseRelease> emptyList());
        platforms.put("windows", windows);
        EclipsePlatform mac = new EclipsePlatform("mac", Collections.<EclipseRelease> emptyList());
        platforms.put("mac", mac);
        EclipsePlatform linux = new EclipsePlatform("linux", Collections.<EclipseRelease> emptyList());
        platforms.put("linux", linux);

        EclipseDownloads eclipseDownloads = new EclipseDownloads(platforms);

        when(this.service.getEclipseDownloads()).thenReturn(eclipseDownloads);
        this.controller.eclipseIndex(this.model);

        assertThat((List<EclipsePlatform>) this.model.get("platforms"), contains(windows, mac, linux));
    }
}
