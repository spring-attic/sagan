package sagan.tools.support;

import sagan.tools.DownloadLink;
import sagan.tools.EclipseDownloads;
import sagan.tools.EclipsePackage;
import sagan.tools.EclipseRelease;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class EclipseDownloadsXmlConverter_TwoDownloadsForDifferentPlatformsTests {

    private EclipseDownloads eclipseDownloads;

    @Before
    public void setup() {
        EclipseXmlDownload macDownload = new EclipseXmlDownload();
        macDownload.setOs("mac");
        macDownload.setDescription("Mac OS X (Cocoa)");
        macDownload.setFile("release/ECLIPSE/kepler/R/eclipse-standard-kepler-R-macosx-cocoa.tar.gz");
        macDownload.setBucket("http://eclipseXmlDownload.springsource.com/");

        EclipseXmlDownload windowsDownload = new EclipseXmlDownload();
        windowsDownload.setOs("windows");
        windowsDownload.setDescription("Windows (64bit)");
        windowsDownload.setFile("release/ECLIPSE/kepler/R/eclipse-standard-kepler-R-win32-x86_64.zip");
        windowsDownload.setBucket("http://eclipseXmlDownload.springsource.com/");

        EclipseXmlPackage eclipseXmlPackage = new EclipseXmlPackage();
        eclipseXmlPackage.setName("Eclipse Standard 4.3 (Win32, 0MB)");
        eclipseXmlPackage.setEclipseXmlDownloads(Arrays.asList(macDownload, windowsDownload));

        EclipseXmlProduct eclipseXmlProduct = new EclipseXmlProduct();
        eclipseXmlProduct.setName("Eclipse Kepler Package Downloads (based on Eclipse 4.3)");
        eclipseXmlProduct.setPackages(Collections.singletonList(eclipseXmlPackage));
        EclipseXml eclipseXml = new EclipseXml();
        eclipseXml.setEclipseXmlProducts(Collections.singletonList(eclipseXmlProduct));

        EclipseDownloadsXmlConverter converter = new EclipseDownloadsXmlConverter();
        eclipseDownloads = converter.convert(eclipseXml);
    }

    @Test
    public void addsTwoPlatforms() throws Exception {
        assertThat(eclipseDownloads.getPlatforms().size(), equalTo(2));
        assertThat(eclipseDownloads.getPlatforms().get("mac").getName(), equalTo("Mac"));
        assertThat(eclipseDownloads.getPlatforms().get("windows").getName(), equalTo("Windows"));
    }

    @Test
    public void addsAProductToEachPlatform() throws Exception {
        List<EclipseRelease> macProducts = eclipseDownloads.getPlatforms().get("mac").getReleases();
        assertThat(macProducts.size(), equalTo(1));
        assertThat(macProducts.get(0).getName(), equalTo("Eclipse Kepler"));

        List<EclipseRelease> windowsProducts = eclipseDownloads.getPlatforms().get("windows").getReleases();
        assertThat(windowsProducts.size(), equalTo(1));
        assertThat(windowsProducts.get(0).getName(), equalTo("Eclipse Kepler"));
    }

    @Test
    public void addsTwoPackages() throws Exception {
        List<EclipsePackage> macPackages =
                eclipseDownloads.getPlatforms().get("mac").getReleases().get(0).getPackages();
        assertThat(macPackages.size(), equalTo(1));
        assertThat(macPackages.get(0).getName(), equalTo("Eclipse Standard 4.3"));

        List<EclipsePackage> windowsPackages =
                eclipseDownloads.getPlatforms().get("windows").getReleases().get(0).getPackages();
        assertThat(windowsPackages.size(), equalTo(1));
        assertThat(windowsPackages.get(0).getName(), equalTo("Eclipse Standard 4.3"));
    }

    @Test
    public void addsDownloadLinksToTheArchitectures() throws Exception {
        List<EclipsePackage> macPackages =
                eclipseDownloads.getPlatforms().get("mac").getReleases().get(0).getPackages();
        List<DownloadLink> macDownloadLinks = macPackages.get(0).getArchitectures().get(0).getDownloadLinks();
        assertThat(macDownloadLinks.size(), equalTo(1));
        assertThat(macDownloadLinks.get(0).getOs(), equalTo("mac"));
        assertThat(
                macDownloadLinks.get(0).getUrl(),
                equalTo("http://eclipseXmlDownload.springsource.com/release/ECLIPSE/kepler/R/eclipse-standard-kepler-R-macosx-cocoa.tar.gz"));

        List<EclipsePackage> windowsPackages =
                eclipseDownloads.getPlatforms().get("windows").getReleases().get(0).getPackages();
        List<DownloadLink> windowsDownloadLinks = windowsPackages.get(0).getArchitectures().get(0).getDownloadLinks();
        assertThat(windowsDownloadLinks.size(), equalTo(1));
        assertThat(windowsDownloadLinks.get(0).getOs(), equalTo("windows"));
        assertThat(
                windowsDownloadLinks.get(0).getUrl(),
                equalTo("http://eclipseXmlDownload.springsource.com/release/ECLIPSE/kepler/R/eclipse-standard-kepler-R-win32-x86_64.zip"));
    }
}
