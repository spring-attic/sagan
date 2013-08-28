package io.spring.site.domain.tools.toolsuite.xml;

import io.spring.site.test.FixtureLoader;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ToolSuiteXmlParsingTests {
	private String responseXml = FixtureLoader.load("/fixtures/tools/sts_downloads.xml");

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
