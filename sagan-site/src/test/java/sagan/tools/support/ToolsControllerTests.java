package sagan.tools.support;

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
import sagan.tools.Architecture;
import sagan.tools.DownloadLink;
import sagan.tools.EclipseDownloads;
import sagan.tools.EclipsePlatform;
import sagan.tools.EclipseRelease;
import sagan.tools.EclipseVersion;
import sagan.tools.ToolSuiteDownloads;
import sagan.tools.ToolSuitePlatform;
import sagan.tools.UpdateSiteArchive;

import org.springframework.ui.ExtendedModelMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
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
        ToolSuiteDownloads toolSuite = new ToolSuiteDownloads("STS", "3.1.2.RELEASE",
                "http://static.springsource.org/sts/nan/v312/NewAndNoteworthy.html", platforms, archives);
        when(service.getStsGaDownloads()).thenReturn(toolSuite);
        controller.stsIndex(model);

        @SuppressWarnings("unchecked")
        Set<DownloadLink> actual = (Set<DownloadLink>) model.get("downloadLinks");
        assertThat(actual, equalTo(toolSuite.getPreferredDownloadLinks()));
        assertThat((String) model.get("version"), equalTo("3.1.2.RELEASE"));

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

        when(service.getEclipseDownloads()).thenReturn(eclipseDownloads);
        controller.eclipseIndex(model);

        assertThat((List<EclipsePlatform>) model.get("platforms"), contains(windows, mac, linux));
    }
}
