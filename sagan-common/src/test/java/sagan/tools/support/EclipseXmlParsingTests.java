package sagan.tools.support;

import sagan.support.Fixtures;

import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class EclipseXmlParsingTests {

    private String responseXml = Fixtures.load("/fixtures/tools/eclipse.xml");

    @Test
    public void unmarshal() throws Exception {
        Serializer serializer = new Persister();

        EclipseXml eclipseXml = serializer.read(EclipseXml.class, responseXml);
        assertThat(eclipseXml.getEclipseXmlProducts(), notNullValue());
        assertThat(eclipseXml.getEclipseXmlProducts().size(), equalTo(6));

        EclipseXmlProduct eclipseXmlProduct = eclipseXml.getEclipseXmlProducts().get(0);
        assertThat(eclipseXmlProduct.getName(), equalTo("SpringSource Tool Suites Downloads"));
        assertThat(eclipseXmlProduct.getPackages().size(), equalTo(4));
        assertThat(
                eclipseXmlProduct.getPackages().get(0).getDescription(),
                equalTo("Spring Tool Suite&trade; (STS) provides the best Eclipse-powered development environment for building Spring-based enterprise applications. STS includes tools for all of the latest enterprise Java and Spring based technologies. STS supports application targeting to local, and cloud-based servers and provides built in support for vFabric tc Server. Spring Tool Suite is freely available for development and internal business operations use with no time limits."));
        assertThat(eclipseXmlProduct.getPackages().get(0).getEclipseXmlDownloads().get(0).getFile(),
                equalTo("release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-win32-installer.exe"));
    }
}
