package sagan.tools.eclipse.parser;

import sagan.tools.eclipse.EclipseDownloads;
import sagan.tools.eclipse.EclipsePackage;
import sagan.tools.eclipse.EclipsePlatform;
import sagan.tools.eclipse.EclipseRelease;
import sagan.tools.eclipse.parser.EclipseDownloadsXmlConverter;
import sagan.tools.eclipse.xml.EclipseXml;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import sagan.util.FixtureLoader;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EclipseDownloadsXmlConverterTests {

    private EclipseDownloads eclipseDownloads;
    private Map<String, EclipsePlatform> platforms;

    @Before
    public void setUp() throws Exception {
        String responseXml = FixtureLoader.load("/fixtures/tools/eclipse.xml");
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
        List<EclipseRelease> packages = platforms.get("windows").getReleases();
        for (EclipseRelease eclipsePackage : packages) {
            assertThat(eclipsePackage.getName(), not(containsString("Spring Tool Suite")));
            assertThat(eclipsePackage.getName(), not(containsString("Groovy")));
        }
    }

    @Test
    public void hasProducts() throws Exception {
        List<EclipseRelease> products = platforms.get("windows").getReleases();
        assertThat(products.size(), is(5));
        assertThat(products.get(0).getName(), equalTo("Eclipse Kepler"));
        assertThat(products.get(0).getEclipseVersion(), equalTo("Eclipse 4.3"));
        assertThat(products.get(1).getName(), equalTo("Eclipse Juno"));
        assertThat(products.get(1).getEclipseVersion(), equalTo("Eclipse 4.2.2"));
        assertThat(products.get(2).getName(), equalTo("Eclipse Indigo"));
        assertThat(products.get(2).getEclipseVersion(), equalTo("Eclipse 3.7.2"));
        assertThat(products.get(3).getName(), equalTo("Eclipse Helios"));
        assertThat(products.get(3).getEclipseVersion(), equalTo("Eclipse 3.6.2"));
        assertThat(products.get(4).getName(), equalTo("Eclipse Galileo"));
        assertThat(products.get(4).getEclipseVersion(), equalTo("Eclipse 3.5.2"));
    }

    @Test
    public void productHasPackages() throws Exception {
        List<EclipsePackage> packages = platforms.get("windows").getReleases().get(0).getPackages();
        assertThat(packages.size(), is(4));
        assertThat(packages.get(0).getName(), equalTo("Eclipse Standard 4.3"));
        assertThat(packages.get(1).getName(), equalTo("Eclipse IDE for Java EE Developers"));
        assertThat(packages.get(2).getName(), equalTo("Eclipse IDE for Java Developers"));
        assertThat(packages.get(3).getName(), equalTo("Eclipse for RCP and RAP Developers"));
    }

}
