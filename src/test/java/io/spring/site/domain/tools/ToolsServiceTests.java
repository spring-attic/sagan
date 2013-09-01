package io.spring.site.domain.tools;

import io.spring.site.domain.services.CachedRestClient;
import io.spring.site.domain.tools.toolsuite.DownloadLink;
import io.spring.site.domain.tools.toolsuite.EclipseVersion;
import io.spring.site.domain.tools.toolsuite.ToolSuiteDownloads;
import io.spring.site.domain.tools.toolsuite.ToolSuitePlatform;
import io.spring.site.test.FixtureLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ToolsServiceTests {

    private ToolsService service;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CachedRestClient cachedRestClient;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        Serializer serializer = new Persister();
        this.service = new ToolsService(this.cachedRestClient, this.restTemplate,
                serializer);
        String responseXml = FixtureLoader.load("/fixtures/tools/sts_downloads.xml");
        when(
                this.cachedRestClient.get(eq(this.restTemplate), anyString(),
                        (Class<String>) anyObject())).thenReturn(responseXml);
    }

    @Test
    public void testGetStsDownloads() throws Exception {
        ToolSuiteDownloads toolSuite = this.service.getStsGaDownloads();
        assertThat(toolSuite, notNullValue());

        List<ToolSuitePlatform> platforms = toolSuite.getPlatformList();
        assertThat(platforms.size(), equalTo(3));

        ToolSuitePlatform windows = platforms.get(0);
        ToolSuitePlatform mac = platforms.get(1);

        assertThat(windows.getName(), equalTo("Windows"));
        assertThat(mac.getName(), equalTo("Mac"));
        assertThat(platforms.get(2).getName(), equalTo("Linux"));

        List<EclipseVersion> eclipseVersions = windows.getEclipseVersions();
        assertThat(eclipseVersions.size(), equalTo(2));
        assertThat(eclipseVersions.get(0).getName(), equalTo("4.3"));
        assertThat(eclipseVersions.get(1).getName(), equalTo("3.8.2"));

        assertThat(windows.getEclipseVersions().get(0)
                .getArchitectures().get(0).getDownloadLinks().size(), equalTo(2));
        DownloadLink downloadLink = windows.getEclipseVersions().get(0)
                .getArchitectures().get(0).getDownloadLinks().get(0);
        assertThat(
                downloadLink.getUrl(),
                equalTo("http://download.springsource.com/release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-win32-installer.exe"));
        downloadLink = windows.getEclipseVersions().get(0)
                .getArchitectures().get(0).getDownloadLinks().get(1);
        assertThat(
                downloadLink.getUrl(),
                equalTo("http://download.springsource.com/release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-win32.zip"));

        assertThat(toolSuite.getArchives().size(), equalTo(4));
        assertThat(toolSuite.getArchives().get(0).getVersion(), equalTo("4.3.x"));
        assertThat(toolSuite.getArchives().get(1).getVersion(), equalTo("4.2.2.x"));
        assertThat(toolSuite.getArchives().get(2).getVersion(), equalTo("3.8.2.x"));
        assertThat(toolSuite.getArchives().get(3).getVersion(), equalTo("3.7.2.x"));
    }

    @Test
    public void testGetGgtsDownloads() throws Exception {
        ToolSuiteDownloads toolSuite = this.service.getGgtsGaDownloads();
        assertThat(toolSuite, notNullValue());

        List<ToolSuitePlatform> platforms = toolSuite.getPlatformList();
        assertThat(platforms.size(), equalTo(3));

        ToolSuitePlatform windows = platforms.get(0);
        ToolSuitePlatform mac = platforms.get(1);
        
        assertThat(windows.getName(), equalTo("Windows"));
        assertThat(mac.getName(), equalTo("Mac"));
        assertThat(platforms.get(2).getName(), equalTo("Linux"));

        List<EclipseVersion> eclipseVersions = mac.getEclipseVersions();
        assertThat(eclipseVersions.size(), equalTo(2));
        assertThat(eclipseVersions.get(0).getName(), equalTo("4.3"));
        assertThat(eclipseVersions.get(1).getName(), equalTo("3.8.2"));

        assertThat(windows.getEclipseVersions().get(0)
                .getArchitectures().get(0).getDownloadLinks().size(), equalTo(2));
        DownloadLink downloadLink = windows.getEclipseVersions().get(0)
                .getArchitectures().get(0).getDownloadLinks().get(0);
        assertThat(
                downloadLink.getUrl(),
                equalTo("http://download.springsource.com/release/STS/3.3.0/dist/e4.3/groovy-grails-tool-suite-3.3.0.RELEASE-e4.3-win32-installer.exe"));
        downloadLink = windows.getEclipseVersions().get(0)
                .getArchitectures().get(0).getDownloadLinks().get(1);
        assertThat(
                downloadLink.getUrl(),
                equalTo("http://download.springsource.com/release/STS/3.3.0/dist/e4.3/groovy-grails-tool-suite-3.3.0.RELEASE-e4.3-win32.zip"));

        assertThat(toolSuite.getArchives().size(), equalTo(4));
        assertThat(toolSuite.getArchives().get(0).getVersion(), equalTo("4.3.x"));
        assertThat(toolSuite.getArchives().get(1).getVersion(), equalTo("4.2.2.x"));
        assertThat(toolSuite.getArchives().get(2).getVersion(), equalTo("3.8.2.x"));
        assertThat(toolSuite.getArchives().get(3).getVersion(), equalTo("3.7.2.x"));
    }
}
