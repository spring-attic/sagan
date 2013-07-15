package org.springframework.site.domain.tools.eclipse.xml;

import org.junit.Before;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class EclipseXmlParsingTests {
	private String responseXml;

	@Before
	public void setUp() throws Exception {
		InputStream response = new ClassPathResource("/eclipse.xml", getClass()).getInputStream();
		responseXml = StreamUtils.copyToString(response, Charset.forName("UTF-8"));
	}

	@Test
	public void unmarshal() throws Exception {
		Serializer serializer = new Persister();

		EclipseXml eclipseXml = serializer.read(EclipseXml.class, responseXml);
		assertThat(eclipseXml.getProducts(), notNullValue());
		assertThat(eclipseXml.getProducts().size(), equalTo(6));

		Product product = eclipseXml.getProducts().get(0);
		assertThat(product.getName(), equalTo("SpringSource Tool Suites Downloads"));
		assertThat(product.getPackages().size(), equalTo(4));
		assertThat(product.getPackages().get(0).getDescription(), equalTo("Spring Tool Suite&trade; (STS) provides the best Eclipse-powered development environment for building Spring-based enterprise applications. STS includes tools for all of the latest enterprise Java and Spring based technologies. STS supports application targeting to local, and cloud-based servers and provides built in support for vFabric tc Server. Spring Tool Suite is freely available for development and internal business operations use with no time limits."));
		assertThat(product.getPackages().get(0).getDownloads().get(0).getFile(), equalTo("release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-win32-installer.exe"));
	}
}
