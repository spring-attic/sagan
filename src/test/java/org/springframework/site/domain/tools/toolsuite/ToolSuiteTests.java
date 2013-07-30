package org.springframework.site.domain.tools.toolsuite;

import org.junit.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ToolSuiteTests {

	@Test
	public void testPreferredDownloadLinks() throws Exception {
		ToolSuiteDownloads toolSuite = buildToolSuite();

		Set<DownloadLink> links = new HashSet<>();
		links.add(new DownloadLink("http://example.com/download32.exe", "exe", "323MB", "windows", "32"));
		links.add(new DownloadLink("http://example.com/download64.exe", "exe", "323MB", "windows", "64"));
		links.add(new DownloadLink("http://example.com/v4_download32.dmg", "dmg", "323MB", "mac", "32"));
		links.add(new DownloadLink("http://example.com/v4_download64.dmg", "dmg", "323MB", "mac", "64"));
		links.add(new DownloadLink("http://example.com/linux.tar.gz", "tar.gz", "323MB", "linux", "32"));
		links.add(new DownloadLink("http://example.com/linux-x86_64.tar.gz", "tar.gz", "323MB", "linux", "64"));

		assertThat(toolSuite.getPreferredDownloadLinks(), equalTo(links));
	}

	private ToolSuiteDownloads buildToolSuite() {
		Map<String, ToolSuitePlatform> platforms = new HashMap<>();

		List<EclipseVersion> macEclipseVersions = new ArrayList<>();

		List<Architecture> v4MacArchitectures = new ArrayList<>();
		List<DownloadLink> v4Mac64Links = new ArrayList<>();
		String os = "mac";
		String architecture = "64";
		v4Mac64Links.add(new DownloadLink("http://example.com/v4_download64.tar.gz", "tar.gz", "323MB", os, architecture));
		v4Mac64Links.add(new DownloadLink("http://example.com/v4_download64.dmg", "dmg", "323MB", os, architecture));
		v4MacArchitectures.add(new Architecture("Mac OS X (Cocoa, 64bit)", v4Mac64Links));

		List<DownloadLink> v4Mac32Links = new ArrayList<>();
		architecture = "32";
		v4Mac32Links.add(new DownloadLink("http://example.com/v4_download32.dmg", "dmg", "323MB", os, architecture));
		v4Mac32Links.add(new DownloadLink("http://example.com/v4_download32.tar.gz", "tar.gz", "323MB", os, architecture));
		v4MacArchitectures.add(new Architecture("Mac OS X (Cocoa)", v4Mac32Links));
		macEclipseVersions.add(new EclipseVersion("4.0", v4MacArchitectures));

		List<Architecture> v3MacArchitectures = new ArrayList<>();
		List<DownloadLink> v3Mac64Links = new ArrayList<>();
		architecture = "64";
		v3Mac64Links.add(new DownloadLink("http://example.com/v3_download64.tar.gz", "tar.gz", "323MB", os, architecture));
		v3Mac64Links.add(new DownloadLink("http://example.com/v3_download64.dmg", "dmg", "323MB", os, architecture));
		v3MacArchitectures.add(new Architecture("Mac OS X (Cocoa, 64bit)", v3Mac64Links));

		List<DownloadLink> v3Mac32Links = new ArrayList<>();
		architecture = "32";
		v3Mac32Links.add(new DownloadLink("http://example.com/v3_download32.dmg", "dmg", "323MB", os, architecture));
		v3Mac32Links.add(new DownloadLink("http://example.com/v3_download32.tar.gz", "tar.gz", "323MB", os, architecture));
		v3MacArchitectures.add(new Architecture("Mac OS X (Cocoa)", v3Mac32Links));
		macEclipseVersions.add(new EclipseVersion("3.0", v3MacArchitectures));

		platforms.put("mac", new ToolSuitePlatform("Mac", macEclipseVersions));

		os = "windows";
		List<Architecture> winArchitectures = new ArrayList<>();
		List<DownloadLink> win32Links = new ArrayList<>();
		architecture = "32";
		win32Links.add(new DownloadLink("http://example.com/download32.exe", "exe", "323MB", os, architecture));
		win32Links.add(new DownloadLink("http://example.com/download32.zip", "zip", "323MB", os, architecture));
		winArchitectures.add(new Architecture("Windows", win32Links));

		List<DownloadLink> win64Links = new ArrayList<>();
		architecture = "64";
		win64Links.add(new DownloadLink("http://example.com/download64.exe", "exe", "323MB", os, architecture));
		win64Links.add(new DownloadLink("http://example.com/download64.zip", "zip", "323MB", os, architecture));
		winArchitectures.add(new Architecture("Windows (64bit)", win64Links));
		platforms.put("windows", new ToolSuitePlatform("Windows", Collections.singletonList(new EclipseVersion("4.3", winArchitectures))));

		os = "linux";
		List<Architecture> linuxArchitectures = new ArrayList<>();
		List<DownloadLink> linux32Links = new ArrayList<>();
		architecture = "32";
		linux32Links.add(new DownloadLink("http://example.com/linux.tar.gz", "tar.gz", "323MB", os, architecture));
		linux32Links.add(new DownloadLink("http://example.com/linux.sh", "sh", "323MB", os, architecture));
		linuxArchitectures.add(new Architecture("Linux (GTK)", linux32Links));

		List<DownloadLink> linux64Links = new ArrayList<>();
		architecture = "64";
		linux64Links.add(new DownloadLink("http://example.com/linux-x86_64.tar.gz", "tar.gz", "323MB", os, architecture));
		linux64Links.add(new DownloadLink("http://example.com/linux-x86_64.sh", "sh", "323MB", os, architecture));
		linuxArchitectures.add(new Architecture("Linux (GTK, 64bit)", linux64Links));
		platforms.put("linux", new ToolSuitePlatform("Linux", Collections.singletonList(new EclipseVersion("4.3", linuxArchitectures))));

		List<UpdateSiteArchive> archives = Collections.emptyList();
		return new ToolSuiteDownloads("3.1.2.RELEASE", platforms, archives);
	}
}
