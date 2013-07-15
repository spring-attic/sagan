package org.springframework.site.domain.tools.eclipse.parser;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.core.io.ClassPathResource;
import org.springframework.site.domain.tools.eclipse.EclipseDownloads;
import org.springframework.site.domain.tools.eclipse.EclipsePackage;
import org.springframework.site.domain.tools.eclipse.EclipsePlatform;
import org.springframework.site.domain.tools.eclipse.xml.EclipseXml;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EclipseDownloadsXmlConverterTests {

	private EclipseDownloads eclipseDownloads;
	private Map<String, EclipsePlatform> platforms;

	@Before
	public void setUp() throws Exception {
		InputStream response = new ClassPathResource("/eclipse.xml", getClass()).getInputStream();
		String responseXml = StreamUtils.copyToString(response, Charset.forName("UTF-8"));
		Serializer serializer = new Persister();
		EclipseXml eclipseXml = serializer.read(EclipseXml.class, responseXml);
		eclipseDownloads = new EclipseDownloadsXmlConverter().convert(eclipseXml);
		platforms = eclipseDownloads.getPlatforms();
	}

	@Test
	public void hasTheThreePlatforms() throws Exception {
		assertThat(platforms.size(), is(3));
		assertThat(platforms , hasKey("windows"));
		assertThat(platforms , hasKey("mac"));
		assertThat(platforms , hasKey("linux"));
	}

	@Test
	public void hasTheCorrectPlatformName() throws Exception {
		assertThat(platforms.get("windows").getName() , is("Windows"));
	}

	@Test
	public void excludesGroovyAndStsPackages() throws Exception {
		List<EclipsePackage> packages = platforms.get("windows").getPackages();
		for (EclipsePackage eclipsePackage : packages) {
			assertThat(eclipsePackage.getName(), not(containsString("Spring Tool Suite")));
			assertThat(eclipsePackage.getName(), not(containsString("Groovy")));
		}
	}

	@Ignore("This needs to be confirmed as the UX doesn't map well to the xml data source")
	@Test
	public void hasFourPackages() throws Exception {
		assertThat(platforms.get("windows").getPackages().size(), is(4));
	}
}
