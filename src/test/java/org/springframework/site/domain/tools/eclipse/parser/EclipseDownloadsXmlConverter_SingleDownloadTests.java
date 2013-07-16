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

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class EclipseDownloadsXmlConverter_SingleDownloadTests {

	private EclipseDownloads eclipseDownloads;

	@Before
	public void setup() {
		EclipseXmlDownload eclipseXmlDownload = new EclipseXmlDownload();
		eclipseXmlDownload.setOs("mac");
		eclipseXmlDownload.setEclipseVersion("4.3.0");
		eclipseXmlDownload.setSize("196MB");
		eclipseXmlDownload.setDescription("Mac OS X (Cocoa)");
		eclipseXmlDownload.setFile("release/ECLIPSE/kepler/R/eclipse-standard-kepler-R-macosx-cocoa.tar.gz");
		eclipseXmlDownload.setBucket("http://eclipseXmlDownload.springsource.com/");

		EclipseXmlPackage eclipseXmlPackage = new EclipseXmlPackage();
		eclipseXmlPackage.setName("Eclipse Standard 4.3 (Win32, 0MB)");
		eclipseXmlPackage.setEclipseXmlDownloads(Collections.singletonList(eclipseXmlDownload));

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
		assertThat(eclipseDownloads.getPlatforms().get("mac").getName(), equalTo("Mac"));
	}

	@Test
	public void addsAProduct() throws Exception {
		List<EclipseRelease> products = eclipseDownloads.getPlatforms().get("mac").getReleases();

		assertThat(products.size(), equalTo(1));
		assertThat(products.get(0).getName(), equalTo("Eclipse Kepler Package Downloads (based on Eclipse 4.3)"));
	}

	@Test
	public void addsAPackage() throws Exception {
		List<EclipsePackage> packages = eclipseDownloads.getPlatforms().get("mac").getReleases().get(0).getPackages();
		assertThat(packages.size(), equalTo(1));
		assertThat(packages.get(0).getName(), equalTo("Eclipse Standard 4.3 (Win32, 0MB)"));
	}

	@Test
	public void addsADownloadLink() throws Exception {
		List<EclipsePackage> packages = eclipseDownloads.getPlatforms().get("mac").getReleases().get(0).getPackages();
		List<EclipseDownloadLink> downloadLinks = packages.get(0).getDownloadLinks();
		assertThat(downloadLinks.size(), equalTo(1));
		assertThat(downloadLinks.get(0).getName(), equalTo("Mac OS X (Cocoa)"));
		assertThat(downloadLinks.get(0).getUrl(), equalTo("http://eclipseXmlDownload.springsource.com/release/ECLIPSE/kepler/R/eclipse-standard-kepler-R-macosx-cocoa.tar.gz"));
	}
}
