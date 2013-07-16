package org.springframework.site.domain.tools.eclipse.parser;

import org.junit.Before;
import org.junit.Test;
import org.springframework.site.domain.tools.eclipse.EclipseDownloadLink;
import org.springframework.site.domain.tools.eclipse.EclipseDownloads;
import org.springframework.site.domain.tools.eclipse.EclipsePackage;
import org.springframework.site.domain.tools.eclipse.EclipseRelease;
import org.springframework.site.domain.tools.eclipse.xml.EclipseXml;
import org.springframework.site.domain.tools.eclipse.xml.EclipseXmlDownload;
import org.springframework.site.domain.tools.eclipse.xml.EclipseXmlPackage;
import org.springframework.site.domain.tools.eclipse.xml.EclipseXmlProduct;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

		EclipseXmlDownload win64Download = new EclipseXmlDownload();
		win64Download.setOs("windows");
		win64Download.setDescription("Windows (64bit)");
		win64Download.setFile("release/ECLIPSE/kepler/R/eclipse-standard-kepler-R-win32-x86_64.zip");
		win64Download.setBucket("http://eclipseXmlDownload.springsource.com/");

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
		assertThat(windowsProducts.get(0).getName(), equalTo("Eclipse Kepler Package Downloads (based on Eclipse 4.3)"));
	}

	@Test
	public void addsAPackage() throws Exception {
		List<EclipsePackage> windowsPackages = eclipseDownloads.getPlatforms().get("windows").getReleases().get(0).getPackages();
		assertThat(windowsPackages.size(), equalTo(1));
		assertThat(windowsPackages.get(0).getName(), equalTo("Eclipse Standard 4.3 (Win32, 0MB)"));
	}

	@Test
	public void addsTwoDownloadLinksToThePackage() throws Exception {
		List<EclipsePackage> windowsPackages = eclipseDownloads.getPlatforms().get("windows").getReleases().get(0).getPackages();
		List<EclipseDownloadLink> windowsDownloadLinks = windowsPackages.get(0).getDownloadLinks();

		assertThat(windowsDownloadLinks.size(), equalTo(2));

		assertThat(windowsDownloadLinks.get(0).getName(), equalTo("Windows"));
		assertThat(windowsDownloadLinks.get(0).getUrl(), equalTo("http://eclipseXmlDownload.springsource.com/release/ECLIPSE/kepler/R/eclipse-standard-kepler-R-win32.zip"));

		assertThat(windowsDownloadLinks.get(1).getName(), equalTo("Windows (64bit)"));
		assertThat(windowsDownloadLinks.get(1).getUrl(), equalTo("http://eclipseXmlDownload.springsource.com/release/ECLIPSE/kepler/R/eclipse-standard-kepler-R-win32-x86_64.zip"));
	}
}
