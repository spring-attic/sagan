package io.spring.site.domain.tools.toolsuite.xml;

import org.junit.Before;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import io.spring.site.domain.tools.toolsuite.xml.Release;
import io.spring.site.domain.tools.toolsuite.xml.ToolSuiteXml;

import java.io.InputStream;
import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ToolSuiteXmlParsingTests {
	private String responseXml;

	@Before
	public void setUp() throws Exception {
		InputStream response = new ClassPathResource("/sts_downloads.xml", getClass()).getInputStream();
		responseXml = StreamUtils.copyToString(response, Charset.forName("UTF-8"));
	}

	@Test
	public void unmarshal() throws Exception {
		Serializer serializer = new Persister();

		ToolSuiteXml toolSuiteXml = serializer.read(ToolSuiteXml.class, responseXml);
		assertThat(toolSuiteXml.getReleases(), notNullValue());
		assertThat(toolSuiteXml.getReleases().size(), equalTo(4));
		Release release = toolSuiteXml.getReleases().get(0);
		assertThat(release.getDownloads().size(), equalTo(16));
	}
}
