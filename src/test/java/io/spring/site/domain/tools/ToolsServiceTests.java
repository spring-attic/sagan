package io.spring.site.domain.tools;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import io.spring.site.domain.tools.toolsuite.DownloadLink;
import io.spring.site.domain.tools.toolsuite.EclipseVersion;
import io.spring.site.domain.tools.toolsuite.ToolSuiteDownloads;
import io.spring.site.domain.tools.toolsuite.ToolSuitePlatform;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ToolsServiceTests {

	private ToolsService service;

	@Mock
	private RestTemplate restTemplate;

	@Before
	public void setUp() throws Exception {
		Serializer serializer = new Persister();
		service = new ToolsService(restTemplate, serializer);
		InputStream response = new ClassPathResource("/fixtures/tools/sts_downloads.xml", getClass()).getInputStream();
		String responseXml = StreamUtils.copyToString(response, Charset.forName("UTF-8"));
		when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(responseXml);
	}

	@Test
	public void testGetStsDownloads() throws Exception {
		ToolSuiteDownloads toolSuite = service.getStsDownloads();
		assertThat(toolSuite, notNullValue());

		Map<String, ToolSuitePlatform> platforms = toolSuite.getPlatforms();
		assertThat(platforms.size(), equalTo(3));

		assertThat(platforms.get("windows").getName(), equalTo("Windows"));
		assertThat(platforms.get("mac").getName(), equalTo("Mac"));
		assertThat(platforms.get("linux").getName(), equalTo("Linux"));

		List<EclipseVersion> eclipseVersions = platforms.get("mac").getEclipseVersions();
		assertThat(eclipseVersions.size(), equalTo(2));
		assertThat(eclipseVersions.get(0).getName(), equalTo("4.3"));
		assertThat(eclipseVersions.get(1).getName(), equalTo("3.8.2"));

		assertThat(platforms.get("windows").getEclipseVersions().get(0).getArchitectures().get(0).getDownloadLinks().size(), equalTo(2));
		DownloadLink downloadLink = platforms.get("windows").getEclipseVersions().get(0).getArchitectures().get(0).getDownloadLinks().get(0);
		assertThat(downloadLink.getUrl(), equalTo("http://download.springsource.com/release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-win32-installer.exe"));
		downloadLink = platforms.get("windows").getEclipseVersions().get(0).getArchitectures().get(0).getDownloadLinks().get(1);
		assertThat(downloadLink.getUrl(), equalTo("http://download.springsource.com/release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-win32.zip"));

		assertThat(toolSuite.getArchives().size(), equalTo(4));
		assertThat(toolSuite.getArchives().get(0).getVersion(), equalTo("4.3"));
		assertThat(toolSuite.getArchives().get(1).getVersion(), equalTo("4.2.2"));
		assertThat(toolSuite.getArchives().get(2).getVersion(), equalTo("3.8.2"));
		assertThat(toolSuite.getArchives().get(3).getVersion(), equalTo("3.7.2"));
	}

	@Test
	public void testGetGgtsDownloads() throws Exception {
		ToolSuiteDownloads toolSuite = service.getGgtsDownloads();
		assertThat(toolSuite, notNullValue());

		Map<String, ToolSuitePlatform> platforms = toolSuite.getPlatforms();
		assertThat(platforms.size(), equalTo(3));

		assertThat(platforms.get("windows").getName(), equalTo("Windows"));
		assertThat(platforms.get("mac").getName(), equalTo("Mac"));
		assertThat(platforms.get("linux").getName(), equalTo("Linux"));

		List<EclipseVersion> eclipseVersions = platforms.get("mac").getEclipseVersions();
		assertThat(eclipseVersions.size(), equalTo(2));
		assertThat(eclipseVersions.get(0).getName(), equalTo("4.3"));
		assertThat(eclipseVersions.get(1).getName(), equalTo("3.8.2"));

		assertThat(platforms.get("windows").getEclipseVersions().get(0).getArchitectures().get(0).getDownloadLinks().size(), equalTo(2));
		DownloadLink downloadLink = platforms.get("windows").getEclipseVersions().get(0).getArchitectures().get(0).getDownloadLinks().get(0);
		assertThat(downloadLink.getUrl(), equalTo("http://download.springsource.com/release/STS/3.3.0/dist/e4.3/groovy-grails-tool-suite-3.3.0.RELEASE-e4.3-win32-installer.exe"));
		downloadLink = platforms.get("windows").getEclipseVersions().get(0).getArchitectures().get(0).getDownloadLinks().get(1);
		assertThat(downloadLink.getUrl(), equalTo("http://download.springsource.com/release/STS/3.3.0/dist/e4.3/groovy-grails-tool-suite-3.3.0.RELEASE-e4.3-win32.zip"));

		assertThat(toolSuite.getArchives().size(), equalTo(4));
		assertThat(toolSuite.getArchives().get(0).getVersion(), equalTo("4.3"));
		assertThat(toolSuite.getArchives().get(1).getVersion(), equalTo("4.2.2"));
		assertThat(toolSuite.getArchives().get(2).getVersion(), equalTo("3.8.2"));
		assertThat(toolSuite.getArchives().get(3).getVersion(), equalTo("3.7.2"));
	}
}
