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

public class EclipseDownloadsXmlConverter_TwoDownloadsForSamePackageWithDifferentArchitecturesTests {

    private EclipseDownloads eclipseDownloads;

    @Before
    public void setup() {
        EclipseXmlDownload win32Download = new EclipseXmlDownload();
        win32Download.setOs("windows");
        win32Download.setDescription("Windows");
        win32Download.setFile("release/ECLIPSE/kepler/R/eclipse-standard-kepler-R-win32.zip");
        win32Download.setBucket("http://eclipseXmlDownload.springsource.com/");
        win32Download.setSize("123MB");

        EclipseXmlDownload win64Download = new EclipseXmlDownload();
        win64Download.setOs("windows");
        win64Download.setDescription("Windows (64bit)");
        win64Download.setFile("release/ECLIPSE/kepler/R/eclipse-standard-kepler-R-win64-x86_64.zip");
        win64Download.setBucket("http://eclipseXmlDownload.springsource.com/");
        win64Download.setSize("456MB");

        EclipseXmlPackage eclipseXmlPackage = new EclipseXmlPackage();
        eclipseXmlPackage.setName("Eclipse Standard 4.3 (Win32, 0MB)");
        eclipseXmlPackage.setEclipseXmlDownloads(Arrays.asList(win32Download, win64Download));

        EclipseXmlProduct eclipseXmlProduct = new EclipseXmlProduct();
        eclipseXmlProduct.setName("Eclipse Kepler Package Downloads (based on Eclipse 4.3)");
        eclipseXmlProduct.setPackages(Collections.singletonList(eclipseXmlPackage));
        EclipseXml eclipseXml = new EclipseXml();
        eclipseXml.setEclipseXmlProducts(Collections.singletonList(eclipseXmlProduct));

        EclipseDownloadsXmlConverter converter = new EclipseDownloadsXmlConverter();
        eclipseDownloads = converter.convert(eclipseXml);
    }

    @Test
    public void addsAPlatform() throws Exception {
        assertThat(eclipseDownloads.getPlatforms().size(), equalTo(1));
        assertThat(eclipseDownloads.getPlatforms().get("windows").getName(), equalTo("Windows"));
    }

    @Test
    public void addsAProductToThePlatform() throws Exception {
        List<EclipseRelease> windowsProducts = eclipseDownloads.getPlatforms().get("windows").getReleases();
        assertThat(windowsProducts.size(), equalTo(1));
        assertThat(windowsProducts.get(0).getName(), equalTo("Eclipse Kepler"));
    }

    @Test
    public void addsAPackage() throws Exception {
        List<EclipsePackage> windowsPackages =
                eclipseDownloads.getPlatforms().get("windows").getReleases().get(0).getPackages();
        assertThat(windowsPackages.size(), equalTo(1));
        assertThat(windowsPackages.get(0).getName(), equalTo("Eclipse Standard 4.3"));
    }

    @Test
    public void addsTwoDownloadLinksToThePackage() throws Exception {
        List<EclipsePackage> windowsPackages =
                eclipseDownloads.getPlatforms().get("windows").getReleases().get(0).getPackages();
        List<DownloadLink> win32DownloadLinks = windowsPackages.get(0).getArchitectures().get(0).getDownloadLinks();

        assertThat(win32DownloadLinks.size(), equalTo(1));

        assertThat(win32DownloadLinks.get(0).getOs(), equalTo("windows"));
        assertThat(win32DownloadLinks.get(0).getArchitecture(), equalTo("32"));
        assertThat(
                win32DownloadLinks.get(0).getUrl(),
                equalTo("http://eclipseXmlDownload.springsource.com/release/ECLIPSE/kepler/R/eclipse-standard-kepler-R-win32.zip"));
        assertThat(win32DownloadLinks.get(0).getFileSize(), equalTo("123MB"));
        assertThat(win32DownloadLinks.get(0).getFileType(), equalTo("zip"));

        List<DownloadLink> win64DownloadLinks = windowsPackages.get(0).getArchitectures().get(1).getDownloadLinks();

        assertThat(win64DownloadLinks.size(), equalTo(1));

        assertThat(win64DownloadLinks.get(0).getOs(), equalTo("windows"));
        assertThat(win64DownloadLinks.get(0).getArchitecture(), equalTo("64"));
        assertThat(
                win64DownloadLinks.get(0).getUrl(),
                equalTo("http://eclipseXmlDownload.springsource.com/release/ECLIPSE/kepler/R/eclipse-standard-kepler-R-win64-x86_64.zip"));
        assertThat(win64DownloadLinks.get(0).getFileSize(), equalTo("456MB"));
        assertThat(win64DownloadLinks.get(0).getFileType(), equalTo("zip"));
    }
}
